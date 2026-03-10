package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInItemResponse {
    private Integer id;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
}
