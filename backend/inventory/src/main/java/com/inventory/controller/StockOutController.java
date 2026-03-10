package com.inventory.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.StockOutRequest;
import com.inventory.dto.StockOutResponse;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.StockOutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stock-out")
@RequiredArgsConstructor
public class StockOutController {
    
    private final StockOutService stockOutService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockOutResponse>>> getAllStockOuts(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<StockOutResponse> stockOuts = stockOutService.getAllStockOuts(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu xuất thành công", stockOuts));
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<StockOutResponse>>> getStockOutsByWarehouse(
            @PathVariable Integer warehouseId,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<StockOutResponse> stockOuts = stockOutService.getStockOutsByWarehouse(warehouseId, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu xuất theo kho thành công", stockOuts));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockOutResponse>> getStockOutById(@PathVariable Integer id, 
                                                                        HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        StockOutResponse stockOut = stockOutService.getStockOutById(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phiếu xuất thành công", stockOut));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<StockOutResponse>> createStockOut(@Valid @RequestBody StockOutRequest request, 
                                                                        HttpServletRequest httpRequest) {
        AuthContext context = authService.getAuthContext(httpRequest);
        StockOutResponse created = stockOutService.createStockOut(request, context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo phiếu xuất kho thành công", created));
    }
}
