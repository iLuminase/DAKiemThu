package com.inventory.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.inventory.dto.StockUpdateMessage;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Client gửi message lên /app/inventory/subscribe
     * Server broadcast đến /topic/inventory
     */
    @MessageMapping("/inventory/subscribe")
    @SendTo("/topic/inventory")
    public StockUpdateMessage handleInventoryUpdate(StockUpdateMessage message) {
        return message;
    }

    /**
     * Method để Service layer gọi khi cần push update
     */
    public void sendStockUpdate(StockUpdateMessage message) {
        messagingTemplate.convertAndSend("/topic/inventory", message);
    }

    /**
     * Push update theo warehouse cụ thể
     */
    public void sendStockUpdateToWarehouse(String warehouseId, StockUpdateMessage message) {
        messagingTemplate.convertAndSend("/topic/inventory/" + warehouseId, message);
    }
}
