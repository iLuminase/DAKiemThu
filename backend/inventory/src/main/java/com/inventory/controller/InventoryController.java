package com.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.InventoryStockDTO;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.InventoryService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getAllInventory(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<InventoryStockDTO> inventory = inventoryService.getAllInventory(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách tồn kho thành công", inventory));
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getInventoryByWarehouse(
            @PathVariable Integer warehouseId,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<InventoryStockDTO> inventory = inventoryService.getInventoryByWarehouse(warehouseId, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tồn kho theo kho thành công", inventory));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getInventoryByProduct(
            @PathVariable Integer productId,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<InventoryStockDTO> inventory = inventoryService.getInventoryByProduct(productId, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tồn kho theo sản phẩm thành công", inventory));
    }

    @GetMapping("/warehouse/{warehouseId}/product/{productId}")
    public ResponseEntity<ApiResponse<InventoryStockDTO>> getInventoryByProductAndWarehouse(
            @PathVariable Integer warehouseId,
            @PathVariable Integer productId,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        InventoryStockDTO inventory = inventoryService.getInventoryByProductAndWarehouse(productId, warehouseId, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tồn kho thành công", inventory));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getAllLowStock(
            @RequestParam(defaultValue = "10") Integer threshold,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<InventoryStockDTO> inventory = inventoryService.getAllLowStock(threshold, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách hàng sắp hết thành công", inventory));
    }

    @GetMapping("/warehouse/{warehouseId}/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryStockDTO>>> getLowStockByWarehouse(
            @PathVariable Integer warehouseId,
            @RequestParam(defaultValue = "10") Integer threshold,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<InventoryStockDTO> inventory = inventoryService.getLowStockByWarehouse(warehouseId, threshold, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách hàng sắp hết theo kho thành công", inventory));
    }
}
