package com.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.AuthRequest;
import com.inventory.dto.AuthResponse;
import com.inventory.service.ClerkAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final ClerkAuthService authService;

    /**
     * Xác thực user từ Clerk và trả về thông tin + role
     */
    @PostMapping("/callback")
    public ApiResponse<AuthResponse> authenticateUser(@RequestBody AuthRequest request) {
        AuthResponse response = authService.authenticate(request);
        return ApiResponse.success("Đăng nhập thành công", response);
    }

    /**
     * Lấy thông tin user hiện tại
     */
    @GetMapping("/me")
    public ApiResponse<AuthResponse> getCurrentUser(@RequestHeader("X-User-Id") String userId) {
        AuthResponse response = authService.getUserInfo(userId);
        return ApiResponse.success(response);
    }

    /**
     * Logout - xóa session nếu cần
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("X-User-Id") String userId) {
        authService.logout(userId);
        return ApiResponse.success("Đăng xuất thành công", null);
    }
}
