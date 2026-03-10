package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String userId;
    private String email;
    private String role;
    private String warehouseName;
    private String token;
}
