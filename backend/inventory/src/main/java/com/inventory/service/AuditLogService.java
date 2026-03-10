package com.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.dto.AuditLogDTO;
import com.inventory.entity.AuditLog;
import com.inventory.entity.User;
import com.inventory.repository.AuditLogRepository;
import com.inventory.repository.UserRepository;
import com.inventory.security.AuthContext;
import com.inventory.security.PermissionChecker;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final PermissionChecker permissionChecker;
    
    @Transactional
    public void log(String entity, String entityId, String action, UUID performedBy) {
        AuditLog log = new AuditLog();
        log.setEntity(entity);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setPerformedBy(performedBy);
        log.setPerformedAt(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
    
    @Transactional
    public void logCreate(UUID performedBy, String entityType, String entityId, String description) {
        log(entityType, entityId, "CREATE: " + description, performedBy);
    }
    
    @Transactional
    public void logUpdate(UUID performedBy, String entityType, String entityId, String description) {
        log(entityType, entityId, "UPDATE: " + description, performedBy);
    }
    
    @Transactional
    public void logDelete(UUID performedBy, String entityType, String entityId, String description) {
        log(entityType, entityId, "DELETE: " + description, performedBy);
    }
    
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getLogs(String entityType, String entityId, String action, 
                                      LocalDateTime startDate, LocalDateTime endDate, AuthContext context) {
        // Chỉ ADMIN mới xem được log
        permissionChecker.requireAdmin(context);
        
        List<AuditLog> logs;
        
        if (entityType != null && entityId != null) {
            logs = auditLogRepository.findByEntityAndEntityId(entityType, entityId);
        } else if (entityType != null) {
            logs = auditLogRepository.findByEntity(entityType);
        } else if (startDate != null && endDate != null) {
            logs = auditLogRepository.findByDateRange(startDate, endDate);
        } else {
            logs = auditLogRepository.findAllOrderByDateDesc();
        }
        
        // Filter by action if provided
        if (action != null) {
            logs = logs.stream()
                    .filter(log -> log.getAction().contains(action))
                    .collect(Collectors.toList());
        }
        
        return logs.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<AuditLogDTO> getRecentLogs(Integer limit, AuthContext context) {
        // Chỉ ADMIN mới xem được log
        permissionChecker.requireAdmin(context);
        
        List<AuditLog> logs = auditLogRepository.findAllOrderByDateDesc();
        
        return logs.stream()
                .limit(limit)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<AuditLogDTO> getAllLogs() {
        return auditLogRepository.findAllOrderByDateDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<AuditLogDTO> getLogsByEntity(String entity) {
        return auditLogRepository.findByEntity(entity).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<AuditLogDTO> getLogsByEntityAndId(String entity, String entityId) {
        return auditLogRepository.findByEntityAndEntityId(entity, entityId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public List<AuditLogDTO> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByDateRange(startDate, endDate).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private AuditLogDTO toDTO(AuditLog log) {
        AuditLogDTO dto = new AuditLogDTO();
        dto.setId(log.getId());
        dto.setEntity(log.getEntity());
        dto.setEntityId(log.getEntityId());
        dto.setAction(log.getAction());
        dto.setPerformedAt(log.getPerformedAt());
        
        if (log.getPerformedBy() != null) {
            User user = userRepository.findById(log.getPerformedBy()).orElse(null);
            if (user != null) {
                dto.setPerformedByUsername(user.getUsername() != null ? user.getUsername() : user.getEmail());
            }
        }
        
        return dto;
    }
}
