package com.hcmut.lms.usermanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "service_functions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFunction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(length = 255)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "system_service_id", nullable = false)
    private SystemService systemService;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "serviceFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UrlPermission> urlPermissions = new HashSet<>();
    
    @OneToMany(mappedBy = "serviceFunction", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RolesServiceFunction> rolesServiceFunctions = new HashSet<>();
}

