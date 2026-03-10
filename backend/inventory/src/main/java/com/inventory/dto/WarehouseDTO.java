package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDTO {
    private Integer id;
    
    @NotBlank(message = "Mã kho không được để trống")
    private String code;
    
    @NotBlank(message = "Tên kho không được để trống")
    private String name;
    
    private String location;
}
