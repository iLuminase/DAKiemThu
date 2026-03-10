package com.inventory.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.SupplierDTO;
import com.inventory.entity.Supplier;
import com.inventory.exception.BadRequestException;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.SupplierRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    private final PermissionChecker permissionChecker;
    private final AuditLogService auditLogService;
    
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public SupplierDTO getSupplierById(Integer id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        return toDTO(supplier);
    }
    
    public List<SupplierDTO> searchSuppliers(String keyword) {
        return supplierRepository.searchByKeyword(keyword).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public SupplierDTO createSupplier(SupplierDTO dto, AuthContext context) {
        permissionChecker.requireManager(context);
        
        if (dto.getEmail() != null && supplierRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email nhà cung cấp đã tồn tại: " + dto.getEmail());
        }
        
        Supplier supplier = new Supplier();
        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        
        supplier = supplierRepository.save(supplier);
        
        auditLogService.log("Supplier", supplier.getId().toString(), "CREATE", context.getUserId());
        
        return toDTO(supplier);
    }
    
    @Transactional
    public SupplierDTO updateSupplier(Integer id, SupplierDTO dto, AuthContext context) {
        permissionChecker.requireManager(context);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        if (dto.getEmail() != null && 
            !dto.getEmail().equals(supplier.getEmail()) && 
            supplierRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email nhà cung cấp đã tồn tại: " + dto.getEmail());
        }
        
        supplier.setName(dto.getName());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        
        supplier = supplierRepository.save(supplier);
        
        auditLogService.log("Supplier", id.toString(), "UPDATE", context.getUserId());
        
        return toDTO(supplier);
    }
    
    @Transactional
    public void deleteSupplier(Integer id, AuthContext context) {
        permissionChecker.requireAdmin(context);
        
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
        
        supplierRepository.delete(supplier);
        
        auditLogService.log("Supplier", id.toString(), "DELETE", context.getUserId());
    }
    
    private SupplierDTO toDTO(Supplier supplier) {
        SupplierDTO dto = new SupplierDTO();
        dto.setId(supplier.getId());
        dto.setName(supplier.getName());
        dto.setPhone(supplier.getPhone());
        dto.setEmail(supplier.getEmail());
        dto.setAddress(supplier.getAddress());
        return dto;
    }
}
