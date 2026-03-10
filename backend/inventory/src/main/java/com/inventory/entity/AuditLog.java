package com.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "NVARCHAR(100)")
    private String entity;
    
    @Column(name = "entity_id", columnDefinition = "NVARCHAR(100)")
    private String entityId;
    
    @Column(columnDefinition = "NVARCHAR(50)")
    private String action; // CREATE, UPDATE, DELETE, VIEW
    
    @Column(name = "performed_by")
    private UUID performedBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by", insertable = false, updatable = false)
    private User performedByUser;
    
    @Column(name = "performed_at")
    private LocalDateTime performedAt;
    
    @PrePersist
    protected void onCreate() {
        performedAt = LocalDateTime.now();
    }
}
