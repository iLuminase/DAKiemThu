package com.inventory.security;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.inventory.entity.User;
import com.inventory.entity.Warehouse;
import com.inventory.exception.UnauthorizedException;
import com.inventory.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    
    /**
     * Lấy thông tin user từ Clerk header
     */
    public AuthContext getAuthContext(HttpServletRequest request) {
        String clerkUserId = request.getHeader("X-Clerk-User-Id");
        
        if (clerkUserId == null || clerkUserId.isBlank()) {
            throw new UnauthorizedException("Không tìm thấy thông tin xác thực");
        }
        
        User user = userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new UnauthorizedException("User không tồn tại trong hệ thống"));
        
        if (!"ACTIVE".equals(user.getStatus())) {
            throw new UnauthorizedException("Tài khoản đã bị vô hiệu hóa");
        }
        
        Set<Integer> warehouseIds = user.getUserGroups().stream()
                .flatMap(group -> group.getWarehouses().stream())
                .map(Warehouse::getId)
                .collect(Collectors.toSet());
        
        AuthContext context = new AuthContext();
        context.setUserId(user.getId());
        context.setClerkUserId(user.getClerkUserId());
        context.setEmail(user.getEmail());
        context.setUsername(user.getUsername());
        context.setRoleCode(user.getRole().getCode());
        context.setWarehouseIds(warehouseIds);
        
        return context;
    }
    
    /**
     * Kiểm tra user có quyền ADMIN không
     */
    public boolean isAdmin(AuthContext context) {
        return "ADMIN".equals(context.getRoleCode());
    }
    
    /**
     * Kiểm tra user có quyền MANAGER không
     */
    public boolean isManager(AuthContext context) {
        return "MANAGER".equals(context.getRoleCode()) || isAdmin(context);
    }
    
    /**
     * Kiểm tra user có quyền truy cập warehouse này không
     */
    public boolean hasWarehouseAccess(AuthContext context, Integer warehouseId) {
        if (isAdmin(context)) {
            return true; // ADMIN có quyền truy cập tất cả kho
        }
        return context.getWarehouseIds().contains(warehouseId);
    }
}
