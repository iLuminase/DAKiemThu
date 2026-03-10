package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    private Integer id;
    
    @NotBlank(message = "Tên danh mục không được để trống")
    private String name;
    
    private String slug;
    private String description;
}
