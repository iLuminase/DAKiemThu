package com.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.StockOutItemRequest;
import com.inventory.dto.StockOutItemResponse;
import com.inventory.dto.StockOutRequest;
import com.inventory.dto.StockOutResponse;
import com.inventory.entity.Inventory;
import com.inventory.entity.Product;
import com.inventory.entity.StockOut;
import com.inventory.entity.StockOutItem;
import com.inventory.entity.Warehouse;
import com.inventory.exception.InsufficientStockException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockOutItemRepository;
import com.inventory.repository.StockOutRepository;
import com.inventory.repository.WarehouseRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockOutService {
    
    private final StockOutRepository stockOutRepository;
    private final StockOutItemRepository stockOutItemRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;
    private final StockUpdatePublisher stockUpdatePublisher;
    
    public List<StockOutResponse> getAllStockOuts(AuthContext context) {
        List<StockOut> stockOuts;
        
        if (context.getRoleCode().equals("ADMIN")) {
            stockOuts = stockOutRepository.findAllOrderByDateDesc();
        } else {
            stockOuts = stockOutRepository.findAllOrderByDateDesc().stream()
                    .filter(so -> context.getWarehouseIds().contains(so.getWarehouse().getId()))
                    .collect(Collectors.toList());
        }
        
        return stockOuts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<StockOutResponse> getStockOutsByWarehouse(Integer warehouseId, AuthContext context) {
        permissionChecker.requireWarehouseAccess(context, warehouseId);
        
        return stockOutRepository.findByWarehouse_Id(warehouseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public StockOutResponse getStockOutById(Integer id, AuthContext context) {
        StockOut stockOut = stockOutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockOut", "id", id));
        
        permissionChecker.requireWarehouseAccess(context, stockOut.getWarehouse().getId());
        
        return toResponse(stockOut);
    }
    
    @Transactional
    public StockOutResponse createStockOut(StockOutRequest request, AuthContext context) {
        permissionChecker.requireWarehouseAccess(context, request.getWarehouseId());
        
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));
        
        // Kiểm tra tồn kho trước
        for (StockOutItemRequest itemReq : request.getItems()) {
            Inventory inventory = inventoryRepository
                    .findByProductIdAndWarehouseId(itemReq.getProductId(), request.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory", 
                            "productId & warehouseId", 
                            itemReq.getProductId() + " & " + request.getWarehouseId()));
            
            if (inventory.getQuantity() < itemReq.getQuantity()) {
                Product product = productRepository.findById(itemReq.getProductId()).orElse(null);
                String productName = product != null ? product.getName() : "Unknown";
                throw new InsufficientStockException(productName, itemReq.getQuantity(), inventory.getQuantity());
            }
        }
        
        // Tạo StockOut
        StockOut stockOut = new StockOut();
        stockOut.setWarehouse(warehouse);
        stockOut.setCreatedBy(context.getUserId());
        stockOut.setReason(request.getReason());
        stockOut.setNote(request.getNote());
        
        stockOut = stockOutRepository.save(stockOut);
        
        // Tạo StockOutItems và cập nhật inventory
        List<StockOutItem> items = new ArrayList<>();
        for (StockOutItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));
            
            StockOutItem item = new StockOutItem();
            item.setStockOut(stockOut);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            
            items.add(stockOutItemRepository.save(item));
            
            // Cập nhật tồn kho (trừ đi)
            decreaseInventory(product.getId(), warehouse.getId(), itemReq.getQuantity());
            
            // Publish WebSocket event
            stockUpdatePublisher.publishStockUpdate(product.getId(), warehouse.getId());
        }
        
        stockOut.setItems(items);
        
        auditLogService.log("StockOut", stockOut.getId().toString(), "CREATE", context.getUserId());
        
        return toResponse(stockOut);
    }
    
    private void decreaseInventory(Integer productId, Integer warehouseId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", 
                        "productId & warehouseId", productId + " & " + warehouseId));
        
        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }
    
    private StockOutResponse toResponse(StockOut stockOut) {
        StockOutResponse response = new StockOutResponse();
        response.setId(stockOut.getId());
        response.setWarehouseId(stockOut.getWarehouse() != null ? stockOut.getWarehouse().getId() : null);
        response.setWarehouseName(stockOut.getWarehouse() != null ? stockOut.getWarehouse().getName() : null);
        response.setCreatedByUsername(stockOut.getCreatedByUser() != null ? 
                stockOut.getCreatedByUser().getUsername() : null);
        response.setReason(stockOut.getReason());
        response.setNote(stockOut.getNote());
        response.setCreatedAt(stockOut.getCreatedAt());
        
        if (stockOut.getItems() != null) {
            response.setItems(stockOut.getItems().stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    private StockOutItemResponse toItemResponse(StockOutItem item) {
        StockOutItemResponse response = new StockOutItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        response.setProductCode(item.getProduct() != null ? item.getProduct().getCode() : null);
        response.setProductName(item.getProduct() != null ? item.getProduct().getName() : null);
        response.setQuantity(item.getQuantity());
        return response;
    }
}
