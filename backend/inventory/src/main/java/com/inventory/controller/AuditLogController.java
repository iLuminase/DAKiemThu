package com.inventory.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.dto.ApiResponse;
import com.inventory.dto.AuditLogDTO;
import com.inventory.security.AuthContext;
import com.inventory.security.AuthService;
import com.inventory.service.AuditLogService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AuditLogDTO>>> getAllLogs(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            HttpServletRequest request) {
        
        AuthContext context = authService.getAuthContext(request);
        List<AuditLogDTO> logs = auditLogService.getLogs(entityType, entityId, action, startDate, endDate, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách log thành công", logs));
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<AuditLogDTO>>> getRecentLogs(
            @RequestParam(defaultValue = "50") Integer limit,
            HttpServletRequest request) {
        
        AuthContext context = authService.getAuthContext(request);
        List<AuditLogDTO> logs = auditLogService.getRecentLogs(limit, context);
        return ResponseEntity.ok(new ApiResponse<>(true, "Lấy log gần nhất thành công", logs));
    }
}
