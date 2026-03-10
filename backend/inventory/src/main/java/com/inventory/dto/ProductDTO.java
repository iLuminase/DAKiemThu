package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    
    @NotBlank(message = "Mã sản phẩm không được để trống")
    private String code;
    
    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name;
    
    private String slug;
    private String imageUrl;
    
    @NotNull(message = "Danh mục không được để trống")
    private Integer categoryId;
    
    private String categoryName;
    
    @NotBlank(message = "Đơn vị không được để trống")
    private String unit;
    
    @Positive(message = "Giá nhập phải lớn hơn 0")
    private BigDecimal priceIn;
    
    @Positive(message = "Giá bán phải lớn hơn 0")
    private BigDecimal priceOut;
    
    private Boolean isActive;
}
