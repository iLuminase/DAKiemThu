package com.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.StockInItemRequest;
import com.inventory.dto.StockInItemResponse;
import com.inventory.dto.StockInRequest;
import com.inventory.dto.StockInResponse;
import com.inventory.entity.Inventory;
import com.inventory.entity.Product;
import com.inventory.entity.StockIn;
import com.inventory.entity.StockInItem;
import com.inventory.entity.Supplier;
import com.inventory.entity.Warehouse;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.StockInItemRepository;
import com.inventory.repository.StockInRepository;
import com.inventory.repository.SupplierRepository;
import com.inventory.repository.WarehouseRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockInService {
    
    private final StockInRepository stockInRepository;
    private final StockInItemRepository stockInItemRepository;
    private final SupplierRepository supplierRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;
    private final StockUpdatePublisher stockUpdatePublisher; // WebSocket publisher
    
    public List<StockInResponse> getAllStockIns(AuthContext context) {
        List<StockIn> stockIns;
        
        if (context.getRoleCode().equals("ADMIN")) {
            stockIns = stockInRepository.findAllOrderByDateDesc();
        } else {
            // MANAGER và STAFF chỉ xem được stock in của các kho họ có quyền
            stockIns = stockInRepository.findAllOrderByDateDesc().stream()
                    .filter(si -> context.getWarehouseIds().contains(si.getWarehouse().getId()))
                    .collect(Collectors.toList());
        }
        
        return stockIns.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public List<StockInResponse> getStockInsByWarehouse(Integer warehouseId, AuthContext context) {
        permissionChecker.requireWarehouseAccess(context, warehouseId);
        
        return stockInRepository.findByWarehouse_Id(warehouseId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public StockInResponse getStockInById(Integer id, AuthContext context) {
        StockIn stockIn = stockInRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StockIn", "id", id));
        
        permissionChecker.requireWarehouseAccess(context, stockIn.getWarehouse().getId());
        
        return toResponse(stockIn);
    }
    
    @Transactional
    public StockInResponse createStockIn(StockInRequest request, AuthContext context) {
        // Kiểm tra quyền truy cập warehouse
        permissionChecker.requireWarehouseAccess(context, request.getWarehouseId());
        
        // Validate
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.getSupplierId()));
        
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", request.getWarehouseId()));
        
        // Tạo StockIn
        StockIn stockIn = new StockIn();
        stockIn.setSupplier(supplier);
        stockIn.setWarehouse(warehouse);
        stockIn.setCreatedBy(context.getUserId());
        stockIn.setNote(request.getNote());
        
        stockIn = stockInRepository.save(stockIn);
        
        // Tạo StockInItems và cập nhật inventory
        List<StockInItem> items = new ArrayList<>();
        for (StockInItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product", "id", itemReq.getProductId()));
            
            StockInItem item = new StockInItem();
            item.setStockIn(stockIn);
            item.setProduct(product);
            item.setQuantity(itemReq.getQuantity());
            item.setPrice(itemReq.getPrice());
            
            items.add(stockInItemRepository.save(item));
            
            // Cập nhật tồn kho
            updateInventory(product.getId(), warehouse.getId(), itemReq.getQuantity());
            
            // Publish WebSocket event
            stockUpdatePublisher.publishStockUpdate(product.getId(), warehouse.getId());
        }
        
        stockIn.setItems(items);
        
        auditLogService.log("StockIn", stockIn.getId().toString(), "CREATE", context.getUserId());
        
        return toResponse(stockIn);
    }
    
    private void updateInventory(Integer productId, Integer warehouseId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElse(null);
        
        if (inventory == null) {
            // Tạo mới inventory
            inventory = new Inventory();
            inventory.setProductId(productId);
            inventory.setWarehouseId(warehouseId);
            inventory.setQuantity(quantity);
        } else {
            // Cập nhật quantity
            inventory.setQuantity(inventory.getQuantity() + quantity);
        }
        
        inventoryRepository.save(inventory);
    }
    
    private StockInResponse toResponse(StockIn stockIn) {
        StockInResponse response = new StockInResponse();
        response.setId(stockIn.getId());
        response.setSupplierId(stockIn.getSupplier() != null ? stockIn.getSupplier().getId() : null);
        response.setSupplierName(stockIn.getSupplier() != null ? stockIn.getSupplier().getName() : null);
        response.setWarehouseId(stockIn.getWarehouse() != null ? stockIn.getWarehouse().getId() : null);
        response.setWarehouseName(stockIn.getWarehouse() != null ? stockIn.getWarehouse().getName() : null);
        response.setCreatedByUsername(stockIn.getCreatedByUser() != null ? 
                stockIn.getCreatedByUser().getUsername() : null);
        response.setNote(stockIn.getNote());
        response.setCreatedAt(stockIn.getCreatedAt());
        
        if (stockIn.getItems() != null) {
            response.setItems(stockIn.getItems().stream()
                    .map(this::toItemResponse)
                    .collect(Collectors.toList()));
        }
        
        return response;
    }
    
    private StockInItemResponse toItemResponse(StockInItem item) {
        StockInItemResponse response = new StockInItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProduct() != null ? item.getProduct().getId() : null);
        response.setProductCode(item.getProduct() != null ? item.getProduct().getCode() : null);
        response.setProductName(item.getProduct() != null ? item.getProduct().getName() : null);
        response.setQuantity(item.getQuantity());
        response.setPrice(item.getPrice());
        return response;
    }
}
