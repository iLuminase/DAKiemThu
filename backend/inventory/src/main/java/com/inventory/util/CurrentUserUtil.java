package com.inventory.util;

import com.inventory.entity.User;
import com.inventory.exception.UnauthorizedException;
import com.inventory.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Utility class để lấy thông tin User hiện tại từ request
 */
@Component
@RequiredArgsConstructor
public class CurrentUserUtil {
    
    private final UserRepository userRepository;
    
    public User getCurrentUser(HttpServletRequest request) {
        String clerkUserId = request.getHeader("X-Clerk-User-Id");
        
        if (clerkUserId == null || clerkUserId.isBlank()) {
            throw new UnauthorizedException("Không tìm thấy thông tin xác thực");
        }
        
        return userRepository.findByClerkUserId(clerkUserId)
                .orElseThrow(() -> new UnauthorizedException("User không tồn tại trong hệ thống"));
    }
}
