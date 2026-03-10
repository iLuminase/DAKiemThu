package com.inventory.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "warehouse_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(WarehouseGroup.WarehouseGroupId.class)
public class WarehouseGroup {
    
    @Id
    @Column(name = "warehouse_id")
    private Integer warehouseId;
    
    @Id
    @Column(name = "group_id")
    private Integer groupId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", insertable = false, updatable = false)
    private Warehouse warehouse;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", insertable = false, updatable = false)
    private UserGroup userGroup;
    
    // Composite Key Class
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseGroupId implements Serializable {
        private Integer warehouseId;
        private Integer groupId;
    }
}
