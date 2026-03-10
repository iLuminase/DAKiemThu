package com.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    
    @NotBlank(message = "Clerk User ID không được để trống")
    private String clerkUserId;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String username;
    private String phone;
    
    @NotNull(message = "Role không được để trống")
    private Integer roleId;
    
    private String roleCode;
    private String roleName;
    private String status;
    private List<Integer> groupIds;
}
