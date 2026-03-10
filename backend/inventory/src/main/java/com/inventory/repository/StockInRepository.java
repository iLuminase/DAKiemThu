package com.inventory.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventory.entity.StockIn;

@Repository
public interface StockInRepository extends JpaRepository<StockIn, Integer> {
    
    List<StockIn> findByWarehouse_Id(Integer warehouseId);
    
    List<StockIn> findBySupplier_Id(Integer supplierId);
    
    List<StockIn> findByCreatedBy(UUID createdBy);
    
    @Query("SELECT s FROM StockIn s WHERE s.warehouse.id = :warehouseId " +
           "AND s.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY s.createdAt DESC")
    List<StockIn> findByWarehouseAndDateRange(
        @Param("warehouseId") Integer warehouseId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT s FROM StockIn s ORDER BY s.createdAt DESC")
    List<StockIn> findAllOrderByDateDesc();
}
