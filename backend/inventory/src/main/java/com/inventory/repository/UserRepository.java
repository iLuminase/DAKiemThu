package com.inventory.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventory.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    
    Optional<User> findByClerkUserId(String clerkUserId);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByUsername(String username);
    
    List<User> findByStatus(String status);
    
    @Query("SELECT u FROM User u JOIN u.userGroups g WHERE g.id = :groupId")
    List<User> findByUserGroupId(@Param("groupId") Integer groupId);
    
    @Query("SELECT u FROM User u WHERE u.role.code = :roleCode")
    List<User> findByRoleCode(@Param("roleCode") String roleCode);
    
    boolean existsByClerkUserId(String clerkUserId);
    
    boolean existsByEmail(String email);
}
