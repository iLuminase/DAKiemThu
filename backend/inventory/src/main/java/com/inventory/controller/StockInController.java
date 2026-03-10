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
import com.inventory.dto.StockInRequest;
import com.inventory.dto.StockInResponse;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.StockInService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stock-in")
@RequiredArgsConstructor
public class StockInController {
    
    private final StockInService stockInService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<StockInResponse>>> getAllStockIns(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<StockInResponse> stockIns = stockInService.getAllStockIns(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu nhập thành công", stockIns));
    }
    
    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<ApiResponse<List<StockInResponse>>> getStockInsByWarehouse(
            @PathVariable Integer warehouseId,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<StockInResponse> stockIns = stockInService.getStockInsByWarehouse(warehouseId, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phiếu nhập theo kho thành công", stockIns));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StockInResponse>> getStockInById(@PathVariable Integer id, 
                                                                       HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        StockInResponse stockIn = stockInService.getStockInById(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phiếu nhập thành công", stockIn));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<StockInResponse>> createStockIn(@Valid @RequestBody StockInRequest request, 
                                                                      HttpServletRequest httpRequest) {
        AuthContext context = authService.getAuthContext(httpRequest);
        StockInResponse created = stockInService.createStockIn(request, context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo phiếu nhập kho thành công", created));
    }
}
