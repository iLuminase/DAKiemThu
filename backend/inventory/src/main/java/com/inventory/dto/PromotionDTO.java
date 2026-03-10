package com.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionDTO {
    private Integer id;
    
    @NotBlank(message = "Mã khuyến mãi không được để trống")
    private String code;
    
    @NotBlank(message = "Tên khuyến mãi không được để trống")
    private String name;
    
    @NotBlank(message = "Loại giảm giá không được để trống")
    private String discountType; // PERCENT / FIXED
    
    @NotNull(message = "Giá trị giảm không được để trống")
    @Positive(message = "Giá trị giảm phải lớn hơn 0")
    private BigDecimal discountValue;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private List<Integer> productIds;
}
