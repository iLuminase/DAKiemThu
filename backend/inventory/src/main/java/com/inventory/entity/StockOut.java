package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "stock_out")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockOut {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private User createdByUser;
    
    @Column(length = 100, columnDefinition = "NVARCHAR(100)")
    private String reason;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String note;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "stockOut", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockOutItem> items = new ArrayList<>();
    
    // Helper methods
    public void addItem(StockOutItem item) {
        items.add(item);
        item.setStockOut(this);
    }
    
    public void removeItem(StockOutItem item) {
        items.remove(item);
        item.setStockOut(null);
    }
}
