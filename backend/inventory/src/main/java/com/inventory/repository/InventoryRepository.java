package com.inventory.repository;

import com.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Inventory.InventoryId> {
    
    Optional<Inventory> findByProductIdAndWarehouseId(Integer productId, Integer warehouseId);
    
    List<Inventory> findByProductId(Integer productId);
    
    List<Inventory> findByWarehouseId(Integer warehouseId);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity < :threshold AND i.warehouseId = :warehouseId")
    List<Inventory> findLowStockByWarehouse(@Param("warehouseId") Integer warehouseId, 
                                            @Param("threshold") Integer threshold);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity < :threshold")
    List<Inventory> findLowStock(@Param("threshold") Integer threshold);
    
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :quantity " +
           "WHERE i.productId = :productId AND i.warehouseId = :warehouseId")
    int increaseStock(@Param("productId") Integer productId, 
                      @Param("warehouseId") Integer warehouseId, 
                      @Param("quantity") Integer quantity);
    
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :quantity " +
           "WHERE i.productId = :productId AND i.warehouseId = :warehouseId")
    int decreaseStock(@Param("productId") Integer productId, 
                      @Param("warehouseId") Integer warehouseId, 
                      @Param("quantity") Integer quantity);
}
