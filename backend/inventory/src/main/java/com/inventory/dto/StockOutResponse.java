package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOutResponse {
    private Integer id;
    private Integer warehouseId;
    private String warehouseName;
    private String createdByUsername;
    private String reason;
    private String note;
    private LocalDateTime createdAt;
    private List<StockOutItemResponse> items;
}
