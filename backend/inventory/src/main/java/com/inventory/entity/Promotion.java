package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "promotions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 50, unique = true, nullable = false)
    private String code;
    
    @Column(columnDefinition = "NVARCHAR(255)")
    private String name;
    
    @Column(name = "discount_type", length = 20)
    private String discountType; // PERCENT / FIXED
    
    @Column(name = "discount_value", precision = 18, scale = 2)
    private BigDecimal discountValue;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @ManyToMany
    @JoinTable(
        name = "promotion_products",
        joinColumns = @JoinColumn(name = "promotion_id"),
        inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products = new HashSet<>();
    
    // Helper methods
    public void addProduct(Product product) {
        products.add(product);
        product.getPromotions().add(this);
    }
    
    public void removeProduct(Product product) {
        products.remove(product);
        product.getPromotions().remove(this);
    }
    
    public boolean isCurrentlyActive() {
        if (!isActive) return false;
        LocalDateTime now = LocalDateTime.now();
        return (startDate == null || now.isAfter(startDate)) && 
               (endDate == null || now.isBefore(endDate));
    }
}
