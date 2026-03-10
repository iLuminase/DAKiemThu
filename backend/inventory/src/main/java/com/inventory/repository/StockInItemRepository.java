package com.inventory.repository;

import com.inventory.entity.StockInItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockInItemRepository extends JpaRepository<StockInItem, Integer> {
    
    List<StockInItem> findByStockInId(Integer stockInId);
    
    List<StockInItem> findByProductId(Integer productId);
}
