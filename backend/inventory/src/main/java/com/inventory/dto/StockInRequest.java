package com.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockInRequest {
    
    @NotNull(message = "Nhà cung cấp không được để trống")
    private Integer supplierId;
    
    @NotNull(message = "Kho không được để trống")
    private Integer warehouseId;
    
    private String note;
    
    @Valid
    @NotEmpty(message = "Danh sách sản phẩm không được để trống")
    private List<StockInItemRequest> items;
}
