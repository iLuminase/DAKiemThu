package com.inventory.security;

import com.inventory.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PermissionChecker {
    
    private final AuthService authService;
    
    /**
     * Yêu cầu user phải là ADMIN
     */
    public void requireAdmin(AuthContext context) {
        if (!authService.isAdmin(context)) {
            throw new ForbiddenException("Chỉ ADMIN mới có quyền thực hiện thao tác này");
        }
    }
    
    /**
     * Yêu cầu user phải là MANAGER hoặc ADMIN
     */
    public void requireManager(AuthContext context) {
        if (!authService.isManager(context)) {
            throw new ForbiddenException("Chỉ MANAGER hoặc ADMIN mới có quyền thực hiện thao tác này");
        }
    }
    
    /**
     * Yêu cầu user phải có quyền truy cập warehouse
     */
    public void requireWarehouseAccess(AuthContext context, Integer warehouseId) {
        if (!authService.hasWarehouseAccess(context, warehouseId)) {
            throw new ForbiddenException("Bạn không có quyền truy cập kho này");
        }
    }
}
