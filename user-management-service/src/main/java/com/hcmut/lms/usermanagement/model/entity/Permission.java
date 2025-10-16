package com.hcmut.lms.usermanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "permissions", indexes = {
    @Index(name = "idx_role_id", columnList = "role_id"),
    @Index(name = "idx_endpoint", columnList = "endpoint, http_method")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @Column(nullable = false, length = 255)
    private String endpoint;
    
    @Column(name = "http_method", nullable = false, length = 10)
    private String httpMethod;
    
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;
    
    @Column(name = "is_allowed", nullable = false)
    @Builder.Default
    private Boolean isAllowed = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

