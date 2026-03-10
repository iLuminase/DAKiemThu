package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Inventory.InventoryId.class)
public class Inventory {
    
    @Id
    @Column(name = "product_id")
    private Integer productId;
    
    @Id
    @Column(name = "warehouse_id")
    private Integer warehouseId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", insertable = false, updatable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;
    
    @Column(nullable = false)
    private Integer quantity = 0;
    
    // Composite Key Class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryId implements Serializable {
        private Integer productId;
        private Integer warehouseId;
    }
}
