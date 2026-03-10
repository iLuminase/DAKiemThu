package com.inventory.repository;

import com.inventory.entity.StockOutItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOutItemRepository extends JpaRepository<StockOutItem, Integer> {
    
    List<StockOutItem> findByStockOutId(Integer stockOutId);
    
    List<StockOutItem> findByProductId(Integer productId);
}
