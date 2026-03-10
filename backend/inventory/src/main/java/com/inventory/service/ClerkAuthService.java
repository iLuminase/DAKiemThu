package com.inventory.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.inventory.dto.AuthRequest;
import com.inventory.dto.AuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClerkAuthService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Xác thực user từ Clerk callback
     * Kiểm tra/tạo user trong DB, lấy role và warehouse
     */
    public AuthResponse authenticate(AuthRequest request) {
        // Check user tồn tại chưa
        String checkSql = "SELECT user_id, email FROM Users WHERE clerk_user_id = ?";
        
        Map<String, Object> user = null;
        try {
            user = jdbcTemplate.queryForMap(checkSql, request.getClerkUserId());
        } catch (Exception e) {
            // User chưa tồn tại -> tạo mới
            String userId = UUID.randomUUID().toString();
            String insertSql = "INSERT INTO Users (user_id, clerk_user_id, email, first_name, last_name, phone_number, is_active) " +
                              "VALUES (?, ?, ?, ?, ?, ?, 1)";
            
            jdbcTemplate.update(insertSql, 
                userId,
                request.getClerkUserId(),
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber()
            );
            
            user = Map.of("user_id", userId, "email", request.getEmail());
        }

        String userId = (String) user.get("user_id");
        String email = (String) user.get("email");

        // Lấy role và warehouse
        String roleSql = "SELECT r.role_name, w.warehouse_name " +
                        "FROM UserRoles ur " +
                        "JOIN Roles r ON ur.role_id = r.role_id " +
                        "LEFT JOIN UserGroups ug ON ur.user_id = ug.user_id " +
                        "LEFT JOIN Warehouses w ON ug.warehouse_id = w.warehouse_id " +
                        "WHERE ur.user_id = ?";

        try {
            Map<String, Object> roleData = jdbcTemplate.queryForMap(roleSql, userId);
            String role = (String) roleData.get("role_name");
            String warehouseName = (String) roleData.getOrDefault("warehouse_name", "");

            // Generate simple token (trong thực tế dùng JWT)
            String token = "token_" + userId + "_" + System.currentTimeMillis();

            return new AuthResponse(userId, email, role, warehouseName, token);
        } catch (Exception e) {
            // User chưa có role -> gán STAFF mặc định
            return new AuthResponse(userId, email, "STAFF", "", "token_temp_" + userId);
        }
    }

    /**
     * Lấy thông tin user hiện tại
     */
    public AuthResponse getUserInfo(String userId) {
        String sql = "SELECT u.email, r.role_name, w.warehouse_name " +
                    "FROM Users u " +
                    "LEFT JOIN UserRoles ur ON u.user_id = ur.user_id " +
                    "LEFT JOIN Roles r ON ur.role_id = r.role_id " +
                    "LEFT JOIN UserGroups ug ON u.user_id = ug.user_id " +
                    "LEFT JOIN Warehouses w ON ug.warehouse_id = w.warehouse_id " +
                    "WHERE u.user_id = ?";

        Map<String, Object> data = jdbcTemplate.queryForMap(sql, userId);
        
        return new AuthResponse(
            userId,
            (String) data.get("email"),
            (String) data.getOrDefault("role_name", "STAFF"),
            (String) data.getOrDefault("warehouse_name", ""),
            "token_" + userId
        );
    }

    /**
     * Logout
     */
    public void logout(String userId) {
        // Xóa session nếu có
        // Trong hệ thống đơn giản có thể bỏ qua
    }
}
