package com.inventory.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthContext {
    private UUID userId;
    private String clerkUserId;
    private String email;
    private String username;
    private String roleCode;
    private Set<Integer> warehouseIds; // Các kho user có quyền truy cập
}
