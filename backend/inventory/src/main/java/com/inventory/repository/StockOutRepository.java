package com.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventory.entity.StockOut;

@Repository
public interface StockOutRepository extends JpaRepository<StockOut, Integer> {
    
    List<StockOut> findByWarehouse_Id(Integer warehouseId);
    
    List<StockOut> findByCreatedBy(UUID createdBy);
    
    List<StockOut> findByReason(String reason);
    
    @Query("SELECT s FROM StockOut s WHERE s.warehouse.id = :warehouseId " +
           "AND s.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY s.createdAt DESC")
    List<StockOut> findByWarehouseAndDateRange(
        @Param("warehouseId") Integer warehouseId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT s FROM StockOut s ORDER BY s.createdAt DESC")
    List<StockOut> findAllOrderByDateDesc();
}
