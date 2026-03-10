package com.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockUpdateMessage {
    private String warehouseId;
    private String productId;
    private Integer newQuantity;
    private String updateType; // IMPORT, EXPORT, ADJUST
    private String timestamp;
}
