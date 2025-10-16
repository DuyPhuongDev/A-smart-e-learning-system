package com.hcmut.lms.usermanagement.config;

import com.hcmut.lms.usermanagement.model.entity.EndpointRegistry;
import com.hcmut.lms.usermanagement.model.entity.Permission;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.repository.EndpointRegistryRepository;
import com.hcmut.lms.usermanagement.repository.PermissionRepository;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final EndpointRegistryRepository endpointRegistryRepository;
    private final PermissionRepository permissionRepository;
    
    @Override
    public void run(String... args) {
        log.info("Initializing default data...");
        
        // Initialize some default endpoints
        initializeDefaultEndpoints();
        
        // Initialize default permissions for predefined roles
        initializeDefaultPermissions();
        
        log.info("Data initialization completed");
    }
    
    private void initializeDefaultEndpoints() {
        if (endpointRegistryRepository.count() > 0) {
            log.info("Endpoints already initialized, skipping...");
            return;
        }
        
        log.info("Creating default endpoints...");
        
        // User Management Service endpoints
        createEndpoint("/api/users", "GET", "user-management-service", "Get all users");
        createEndpoint("/api/users", "POST", "user-management-service", "Create user");
        createEndpoint("/api/users/{id}", "GET", "user-management-service", "Get user by ID");
        createEndpoint("/api/users/{id}", "PUT", "user-management-service", "Update user");
        createEndpoint("/api/users/{id}", "DELETE", "user-management-service", "Delete user");
        createEndpoint("/api/roles", "GET", "user-management-service", "Get all roles");
        createEndpoint("/api/roles", "POST", "user-management-service", "Create role");
        
        // Course Management Service endpoints
        createEndpoint("/api/courses", "GET", "course-management-service", "Get all courses");
        createEndpoint("/api/courses", "POST", "course-management-service", "Create course");
        createEndpoint("/api/courses/{id}", "GET", "course-management-service", "Get course");
        createEndpoint("/api/courses/{id}", "PUT", "course-management-service", "Update course");
        
        // Assessment Management Service endpoints
        createEndpoint("/api/assessments", "GET", "assessment-management-service", "Get all assessments");
        createEndpoint("/api/assessments", "POST", "assessment-management-service", "Create assessment");
        createEndpoint("/api/assessments/{id}/grade", "POST", "assessment-management-service", "Grade assessment");
        
        log.info("Default endpoints created");
    }
    
    private void createEndpoint(String endpoint, String method, String serviceName, String description) {
        if (!endpointRegistryRepository.existsByEndpointAndHttpMethodAndServiceName(endpoint, method, serviceName)) {
            EndpointRegistry registry = EndpointRegistry.builder()
                    .endpoint(endpoint)
                    .httpMethod(method)
                    .serviceName(serviceName)
                    .description(description)
                    .isActive(true)
                    .build();
            endpointRegistryRepository.save(registry);
        }
    }
    
    private void initializeDefaultPermissions() {
        // Check if permissions already exist
        if (permissionRepository.count() > 0) {
            log.info("Permissions already initialized, skipping...");
            return;
        }
        
        log.info("Creating default permissions for predefined roles...");
        
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        Role teacherRole = roleRepository.findByName("TEACHER").orElse(null);
        Role studentRole = roleRepository.findByName("STUDENT").orElse(null);
        
        if (adminRole == null || teacherRole == null || studentRole == null) {
            log.warn("Predefined roles not found, skipping permission initialization");
            return;
        }
        
        // Admin gets all permissions
        List<EndpointRegistry> allEndpoints = endpointRegistryRepository.findAll();
        for (EndpointRegistry endpoint : allEndpoints) {
            createPermission(adminRole, endpoint, true);
        }
        
        // Teacher permissions
        createPermissionByPattern(teacherRole, "/api/courses", true);
        createPermissionByPattern(teacherRole, "/api/assessments", true);
        createPermissionByPattern(teacherRole, "/api/users", "GET", true); // Read only
        
        // Student permissions
        createPermissionByPattern(studentRole, "/api/courses", "GET", true); // Read courses
        createPermissionByPattern(studentRole, "/api/assessments", "GET", true); // View assessments
        
        log.info("Default permissions created");
    }
    
    private void createPermission(Role role, EndpointRegistry endpoint, boolean isAllowed) {
        Permission permission = Permission.builder()
                .role(role)
                .endpoint(endpoint.getEndpoint())
                .httpMethod(endpoint.getHttpMethod())
                .serviceName(endpoint.getServiceName())
                .isAllowed(isAllowed)
                .build();
        permissionRepository.save(permission);
    }
    
    private void createPermissionByPattern(Role role, String endpointPattern, boolean isAllowed) {
        List<EndpointRegistry> endpoints = endpointRegistryRepository.findAll().stream()
                .filter(e -> e.getEndpoint().startsWith(endpointPattern))
                .toList();
        
        for (EndpointRegistry endpoint : endpoints) {
            createPermission(role, endpoint, isAllowed);
        }
    }
    
    private void createPermissionByPattern(Role role, String endpointPattern, String method, boolean isAllowed) {
        List<EndpointRegistry> endpoints = endpointRegistryRepository.findAll().stream()
                .filter(e -> e.getEndpoint().startsWith(endpointPattern) && e.getHttpMethod().equalsIgnoreCase(method))
                .toList();
        
        for (EndpointRegistry endpoint : endpoints) {
            createPermission(role, endpoint, isAllowed);
        }
    }
}

