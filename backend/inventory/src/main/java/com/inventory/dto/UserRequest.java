package com.inventory.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRequest {
    
    @NotBlank(message = "Clerk User ID không được để trống")
    private String clerkUserId;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String username;
    private String phone;
    private String fullName;
    
    @NotNull(message = "Role không được để trống")
    private Integer roleId;
    
    private List<Integer> groupIds;
    private Boolean isActive;
}
