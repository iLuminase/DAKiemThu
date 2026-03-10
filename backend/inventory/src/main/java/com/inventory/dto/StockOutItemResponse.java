package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutItemResponse {
    private Integer id;
    private Integer productId;
    private String productCode;
    private String productName;
    private Integer quantity;
}
