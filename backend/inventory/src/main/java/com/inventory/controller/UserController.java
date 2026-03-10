package com.inventory.controller;

import java.util.List;
import java.util.UUID;

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
import com.inventory.dto.UserDTO;
import com.inventory.dto.UserRequest;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        List<UserDTO> users = userService.getAllUsers(context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách người dùng thành công", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID id, HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        UserDTO user = userService.getUserById(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin người dùng thành công", user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserRequest userRequest, HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        UserDTO user = userService.createUser(userRequest, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tạo người dùng thành công", user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UserRequest userRequest,
            HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        UserDTO user = userService.updateUser(id, userRequest, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật người dùng thành công", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID id, HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        userService.deleteUser(id, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Xóa người dùng thành công", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO>> getCurrentUser(HttpServletRequest request) {
        AuthContext context = authService.getAuthContext(request);
        UserDTO user = userService.getUserById(context.getUserId(), context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin người dùng hiện tại thành công", user));
    }
}
