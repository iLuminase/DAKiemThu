package com.inventory.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {
    private Integer id;
    
    @NotBlank(message = "Tên nhà cung cấp không được để trống")
    private String name;
    
    private String phone;
    
    @Email(message = "Email không hợp lệ")
    private String email;
    
    private String address;
}
