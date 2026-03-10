package com.inventory.repository;

import com.inventory.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
    
    Optional<UserGroup> findByCode(String code);
    
    boolean existsByCode(String code);
}
