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
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.WarehouseDTO;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.WarehouseService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    
    private final WarehouseService warehouseService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<WarehouseDTO>>> getAllWarehouses(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách kho thành công", warehouses));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> getWarehouseById(@PathVariable Integer id, 
                                                                      HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        WarehouseDTO warehouse = warehouseService.getWarehouseById(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin kho thành công", warehouse));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<WarehouseDTO>> createWarehouse(@Valid @RequestBody WarehouseDTO dto, 
                                                                     HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        WarehouseDTO created = warehouseService.createWarehouse(dto, context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo kho thành công", created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WarehouseDTO>> updateWarehouse(@PathVariable Integer id, 
                                                                     @Valid @RequestBody WarehouseDTO dto,
                                                                     HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        WarehouseDTO updated = warehouseService.updateWarehouse(id, dto, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật kho thành công", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteWarehouse(@PathVariable Integer id, 
                                                            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        warehouseService.deleteWarehouse(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa kho thành công", null));
    }
}
