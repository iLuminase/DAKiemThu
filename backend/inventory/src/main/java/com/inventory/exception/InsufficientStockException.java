package com.inventory.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String productName, Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("Không đủ hàng cho sản phẩm '%s'. Yêu cầu: %d, Tồn kho: %d", 
                          productName, requestedQuantity, availableQuantity));
    }
}
