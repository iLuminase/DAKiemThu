package com.inventory.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryStockDTO {
    private String productId;
    private String productName;
    private String warehouseId;
    private String warehouseName;
    private Integer quantityOnHand;
    private Integer quantityReserved;
    private Integer quantityAvailable;
    private String unit;
    private BigDecimal unitPrice;
    private String lastUpdated;
}
