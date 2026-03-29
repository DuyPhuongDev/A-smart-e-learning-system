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
@Table(name = "url_permissions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlPermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "url_pattern", length = 500)
    private String urlPattern;
    
    @Column(name = "http_method", length = 10)
    private String httpMethod;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_function_id", nullable = false)
    private ServiceFunction serviceFunction;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

