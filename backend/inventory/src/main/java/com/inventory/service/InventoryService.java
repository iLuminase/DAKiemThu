package com.inventory.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.inventory.dto.InventoryStockDTO;
import com.inventory.entity.Inventory;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryRepository;
import com.inventory.repository.ProductRepository;
import com.inventory.repository.WarehouseRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final PermissionChecker permissionChecker;
    
    public List<InventoryStockDTO> getAllInventory(AuthContext context) {
        List<Inventory> inventories;
        
        if (context.getRoleCode().equals("ADMIN")) {
            inventories = inventoryRepository.findAll();
        } else {
            // Filter by warehouses the user has access to
            inventories = inventoryRepository.findAll().stream()
                    .filter(inv -> context.getWarehouseIds().contains(inv.getWarehouseId()))
                    .collect(Collectors.toList());
        }
        
        return inventories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<InventoryStockDTO> getInventoryByWarehouse(Integer warehouseId, AuthContext context) {
        permissionChecker.requireWarehouseAccess(context, warehouseId);
        
        return inventoryRepository.findByWarehouseId(warehouseId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<InventoryStockDTO> getInventoryByProduct(Integer productId, AuthContext context) {
        List<Inventory> inventories = inventoryRepository.findByProductId(productId);
        
        // Filter by warehouse access
        if (!context.getRoleCode().equals("ADMIN")) {
            inventories = inventories.stream()
                    .filter(inv -> context.getWarehouseIds().contains(inv.getWarehouseId()))
                    .collect(Collectors.toList());
        }
        
        return inventories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public InventoryStockDTO getInventoryByProductAndWarehouse(Integer productId, Integer warehouseId, 
                                                               AuthContext context) {
        permissionChecker.requireWarehouseAccess(context, warehouseId);
        
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", 
                        "productId & warehouseId", productId + " & " + warehouseId));
        
        return toDTO(inventory);
    }
    
    public List<InventoryStockDTO> getLowStockByWarehouse(Integer warehouseId, Integer threshold, 
                                                           AuthContext context) {
        permissionChecker.requireWarehouseAccess(context, warehouseId);
        
        return inventoryRepository.findLowStockByWarehouse(warehouseId, threshold).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<InventoryStockDTO> getAllLowStock(Integer threshold, AuthContext context) {
        List<Inventory> inventories;
        
        if (context.getRoleCode().equals("ADMIN")) {
            inventories = inventoryRepository.findLowStock(threshold);
        } else {
            inventories = inventoryRepository.findLowStock(threshold).stream()
                    .filter(inv -> context.getWarehouseIds().contains(inv.getWarehouseId()))
                    .collect(Collectors.toList());
        }
        
        return inventories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private InventoryStockDTO toDTO(Inventory inventory) {
        InventoryStockDTO dto = new InventoryStockDTO();
        dto.setProductId(String.valueOf(inventory.getProductId()));
        dto.setWarehouseId(String.valueOf(inventory.getWarehouseId()));
        dto.setQuantityOnHand(inventory.getQuantity());
        dto.setQuantityAvailable(inventory.getQuantity());
        dto.setQuantityReserved(0);
        
        if (inventory.getProduct() != null) {
            dto.setProductName(inventory.getProduct().getName());
            dto.setUnit(inventory.getProduct().getUnit());
            dto.setUnitPrice(inventory.getProduct().getPriceIn());
        }
        
        if (inventory.getWarehouse() != null) {
            dto.setWarehouseName(inventory.getWarehouse().getName());
        }
        
        dto.setLastUpdated(java.time.LocalDateTime.now().toString());
        
        return dto;
    }
}
