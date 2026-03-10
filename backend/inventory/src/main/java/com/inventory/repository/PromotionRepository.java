package com.inventory.repository;

import com.inventory.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    Optional<Promotion> findByCode(String code);
    
    List<Promotion> findByIsActive(Boolean isActive);
    
    @Query("SELECT p FROM Promotion p WHERE " +
           "p.isActive = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)")
    List<Promotion> findActivePromotions(@Param("now") LocalDateTime now);
    
    @Query("SELECT p FROM Promotion p JOIN p.products prod WHERE prod.id = :productId")
    List<Promotion> findByProductId(@Param("productId") Integer productId);
    
    @Query("SELECT p FROM Promotion p JOIN p.products prod WHERE prod.id = :productId " +
           "AND p.isActive = true AND " +
           "(p.startDate IS NULL OR p.startDate <= :now) AND " +
           "(p.endDate IS NULL OR p.endDate >= :now)")
    List<Promotion> findActivePromotionsByProductId(
        @Param("productId") Integer productId, 
        @Param("now") LocalDateTime now
    );
    
    boolean existsByCode(String code);
}
