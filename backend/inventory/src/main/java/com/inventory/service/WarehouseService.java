package com.inventory.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.WarehouseDTO;
import com.inventory.entity.Warehouse;
import com.inventory.exception.BadRequestException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.WarehouseRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    
    private final WarehouseRepository warehouseRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;
    
    public List<WarehouseDTO> getAllWarehouses(AuthContext context) {
        List<Warehouse> warehouses;
        
        if (context.getRoleCode().equals("ADMIN")) {
            warehouses = warehouseRepository.findAll();
        } else {
            // User chỉ xem được các kho họ có quyền truy cập
            warehouses = warehouseRepository.findAll().stream()
                    .filter(w -> context.getWarehouseIds().contains(w.getId()))
                    .collect(Collectors.toList());
        }
        
        return warehouses.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public WarehouseDTO getWarehouseById(Integer id, AuthContext context) {
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        
        if (!context.getRoleCode().equals("ADMIN") && !context.getWarehouseIds().contains(id)) {
            permissionChecker.requireWarehouseAccess(context, id);
        }
        
        return toDTO(warehouse);
    }
    
    @Transactional
    public WarehouseDTO createWarehouse(WarehouseDTO dto, AuthContext context) {
        permissionChecker.requireAdmin(context); // Chỉ ADMIN
        
        if (warehouseRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Mã kho đã tồn tại: " + dto.getCode());
        }
        
        Warehouse warehouse = new Warehouse();
        warehouse.setCode(dto.getCode());
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        
        warehouse = warehouseRepository.save(warehouse);
        
        auditLogService.log("Warehouse", warehouse.getId().toString(), "CREATE", context.getUserId());
        
        return toDTO(warehouse);
    }
    
    @Transactional
    public WarehouseDTO updateWarehouse(Integer id, WarehouseDTO dto, AuthContext context) {
        permissionChecker.requireAdmin(context);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        
        if (!warehouse.getCode().equals(dto.getCode()) && warehouseRepository.existsByCode(dto.getCode())) {
            throw new BadRequestException("Mã kho đã tồn tại: " + dto.getCode());
        }
        
        warehouse.setCode(dto.getCode());
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        
        warehouse = warehouseRepository.save(warehouse);
        
        auditLogService.log("Warehouse", id.toString(), "UPDATE", context.getUserId());
        
        return toDTO(warehouse);
    }
    
    @Transactional
    public void deleteWarehouse(Integer id, AuthContext context) {
        permissionChecker.requireAdmin(context);
        
        Warehouse warehouse = warehouseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", "id", id));
        
        warehouseRepository.delete(warehouse);
        
        auditLogService.log("Warehouse", id.toString(), "DELETE", context.getUserId());
    }
    
    private WarehouseDTO toDTO(Warehouse warehouse) {
        WarehouseDTO dto = new WarehouseDTO();
        dto.setId(warehouse.getId());
        dto.setCode(warehouse.getCode());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        return dto;
    }
}
