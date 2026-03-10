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
import com.inventory.dto.ProductDTO;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.ProductService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductService productService;
    private final AuthService authService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<ProductDTO> products = productService.getAllProducts(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách sản phẩm thành công", products));
    }
    
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getActiveProducts(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<ProductDTO> products = productService.getActiveProducts(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách sản phẩm active thành công", products));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Integer id, 
                                                                  HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        ProductDTO product = productService.getProductById(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin sản phẩm thành công", product));
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductByCode(@PathVariable String code, 
                                                                     HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        ProductDTO product = productService.getProductByCode(code, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin sản phẩm thành công", product));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(@RequestParam String keyword, 
                                                                        HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<ProductDTO> products = productService.searchProducts(keyword, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm sản phẩm thành công", products));
    }
    
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO dto, 
                                                                 HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        ProductDTO created = productService.createProduct(dto, context);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Tạo sản phẩm thành công", created));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Integer id, 
                                                                 @Valid @RequestBody ProductDTO dto,
                                                                 HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        ProductDTO updated = productService.updateProduct(id, dto, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật sản phẩm thành công", updated));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Integer id, 
                                                          HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        productService.deleteProduct(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa sản phẩm thành công", null));
    }
}
