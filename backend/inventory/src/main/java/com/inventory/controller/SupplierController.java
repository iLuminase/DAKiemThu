package com.inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.SupplierDTO;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.SupplierService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    
    private final SupplierService supplierService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<SupplierDTO>>> getAllSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách nhà cung cấp thành công", suppliers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDTO>> getSupplierById(@PathVariable Integer id) {
        SupplierDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin nhà cung cấp thành công", supplier));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<SupplierDTO>>> searchSuppliers(@RequestParam String keyword) {
        List<SupplierDTO> suppliers = supplierService.searchSuppliers(keyword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm nhà cung cấp thành công", suppliers));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<SupplierDTO>> createSupplier(@Valid @RequestBody SupplierDTO dto, 
                                                                   HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        SupplierDTO created = supplierService.createSupplier(dto, context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo nhà cung cấp thành công", created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SupplierDTO>> updateSupplier(@PathVariable Integer id, 
                                                                   @Valid @RequestBody SupplierDTO dto,
                                                                   HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        SupplierDTO updated = supplierService.updateSupplier(id, dto, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật nhà cung cấp thành công", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Integer id, 
                                                           HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        supplierService.deleteSupplier(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa nhà cung cấp thành công", null));
    }
}
