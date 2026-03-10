package com.inventory.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.inventory.dto.StockUpdateMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockUpdatePublisher {
    
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * Publish stock update qua WebSocket
     */
    public void publishStockUpdate(Integer productId, Integer warehouseId) {
        StockUpdateMessage message = new StockUpdateMessage(
            String.valueOf(warehouseId),
            String.valueOf(productId),
            0, // quantity sẽ được update sau
            "UPDATE",
            String.valueOf(System.currentTimeMillis())
        );
        
        // Gửi đến topic /topic/stock-updates
        messagingTemplate.convertAndSend("/topic/stock-updates", message);
        
        // Gửi đến topic cụ thể của warehouse
        messagingTemplate.convertAndSend("/topic/stock-updates/" + warehouseId, message);
    }
}
