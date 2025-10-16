package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.exception.EndpointNotFoundException;
import com.hcmut.lms.usermanagement.exception.RoleNotFoundException;
import com.hcmut.lms.usermanagement.mapper.EndpointMapper;
import com.hcmut.lms.usermanagement.mapper.PermissionMapper;
import com.hcmut.lms.usermanagement.model.dto.request.UpdatePermissionsRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.EndpointDto;
import com.hcmut.lms.usermanagement.model.dto.response.PermissionDto;
import com.hcmut.lms.usermanagement.model.dto.response.ScanResultDto;
import com.hcmut.lms.usermanagement.model.entity.EndpointRegistry;
import com.hcmut.lms.usermanagement.model.entity.Permission;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.repository.EndpointRegistryRepository;
import com.hcmut.lms.usermanagement.repository.PermissionRepository;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    
    private final EndpointRegistryRepository endpointRegistryRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final EndpointMapper endpointMapper;
    private final PermissionMapper permissionMapper;
    private final DiscoveryClient discoveryClient;
    
    @Override
    @Transactional(readOnly = true)
    public List<EndpointDto> getAllEndpoints() {
        List<EndpointRegistry> endpoints = endpointRegistryRepository.findByIsActiveTrue();
        return endpointMapper.toDtoList(endpoints);
    }
    
    @Override
    @Transactional
    public ScanResultDto scanEndpointsFromServices() {
        int totalEndpoints = 0;
        int newEndpoints = 0;
        int updatedEndpoints = 0;
        
        // Get all registered services from Eureka
        List<String> services = discoveryClient.getServices();
        
        for (String serviceName : services) {
            // Skip infrastructure services
            if (serviceName.equals("api-gateway") || serviceName.equals("eureka-server") || 
                serviceName.equals("config-server")) {
                continue;
            }
            
            List<ServiceInstance> instances = discoveryClient.getInstances(serviceName);
            if (instances.isEmpty()) {
                log.warn("No instances found for service: {}", serviceName);
                continue;
            }
            
            // For now, we'll manually register some common endpoints
            // In production, you would call actuator/mappings endpoint on each service
            List<EndpointInfo> endpointInfos = getDefaultEndpointsForService(serviceName);
            
            for (EndpointInfo info : endpointInfos) {
                totalEndpoints++;
                
                if (!endpointRegistryRepository.existsByEndpointAndHttpMethodAndServiceName(
                        info.endpoint, info.httpMethod, serviceName)) {
                    
                    EndpointRegistry endpoint = EndpointRegistry.builder()
                            .endpoint(info.endpoint)
                            .httpMethod(info.httpMethod)
                            .serviceName(serviceName)
                            .description(info.description)
                            .isActive(true)
                            .build();
                    
                    endpointRegistryRepository.save(endpoint);
                    newEndpoints++;
                } else {
                    updatedEndpoints++;
                }
            }
        }
        
        log.info("Endpoint scan completed. Total: {}, New: {}, Updated: {}", 
                totalEndpoints, newEndpoints, updatedEndpoints);
        
        return ScanResultDto.builder()
                .totalEndpoints(totalEndpoints)
                .newEndpoints(newEndpoints)
                .updatedEndpoints(updatedEndpoints)
                .build();
    }
    
    @Override
    @Transactional
    public void updateRolePermissions(UUID roleId, UpdatePermissionsRequestDto dto) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));
        
        for (UpdatePermissionsRequestDto.PermissionUpdateItem item : dto.getPermissions()) {
            EndpointRegistry endpoint = endpointRegistryRepository.findById(item.getEndpointId())
                    .orElseThrow(() -> new EndpointNotFoundException("Endpoint not found with id: " + item.getEndpointId()));
            
            // Check if permission already exists
            Permission permission = permissionRepository
                    .findByRoleIdAndEndpointAndHttpMethod(roleId, endpoint.getEndpoint(), endpoint.getHttpMethod())
                    .orElse(null);
            
            if (permission == null) {
                // Create new permission
                permission = Permission.builder()
                        .role(role)
                        .endpoint(endpoint.getEndpoint())
                        .httpMethod(endpoint.getHttpMethod())
                        .serviceName(endpoint.getServiceName())
                        .isAllowed(item.getIsAllowed())
                        .build();
            } else {
                // Update existing permission
                permission.setIsAllowed(item.getIsAllowed());
            }
            
            permissionRepository.save(permission);
        }
        
        log.info("Updated permissions for role: {}", role.getName());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PermissionDto> getRolePermissions(UUID roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new RoleNotFoundException("Role not found with id: " + roleId);
        }
        
        List<Permission> permissions = permissionRepository.findByRoleId(roleId);
        return permissionMapper.toDtoList(permissions);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean checkUserPermission(UUID userId, String endpoint, String method) {
        List<Permission> userPermissions = permissionRepository.findAllowedPermissionsByUserId(userId);
        
        return userPermissions.stream()
                .anyMatch(p -> p.getEndpoint().equals(endpoint) && 
                              p.getHttpMethod().equalsIgnoreCase(method) && 
                              p.getIsAllowed());
    }
    
    // Helper method to define default endpoints for each service
    private List<EndpointInfo> getDefaultEndpointsForService(String serviceName) {
        return switch (serviceName) {
            case "user-management-service" -> List.of(
                    new EndpointInfo("/api/users", "GET", "Get all users"),
                    new EndpointInfo("/api/users", "POST", "Create user"),
                    new EndpointInfo("/api/users/{id}", "GET", "Get user by ID"),
                    new EndpointInfo("/api/users/{id}", "PUT", "Update user"),
                    new EndpointInfo("/api/users/{id}", "DELETE", "Delete user"),
                    new EndpointInfo("/api/roles", "GET", "Get all roles"),
                    new EndpointInfo("/api/roles", "POST", "Create role")
            );
            case "course-management-service" -> List.of(
                    new EndpointInfo("/api/courses", "GET", "Get all courses"),
                    new EndpointInfo("/api/courses", "POST", "Create course"),
                    new EndpointInfo("/api/courses/{id}", "GET", "Get course details"),
                    new EndpointInfo("/api/courses/{id}", "PUT", "Update course"),
                    new EndpointInfo("/api/courses/{id}", "DELETE", "Delete course")
            );
            case "assessment-management-service" -> List.of(
                    new EndpointInfo("/api/assessments", "GET", "Get all assessments"),
                    new EndpointInfo("/api/assessments", "POST", "Create assessment"),
                    new EndpointInfo("/api/assessments/{id}", "PUT", "Update assessment")
            );
            default -> List.of();
        };
    }
    
    private static class EndpointInfo {
        String endpoint;
        String httpMethod;
        String description;
        
        EndpointInfo(String endpoint, String httpMethod, String description) {
            this.endpoint = endpoint;
            this.httpMethod = httpMethod;
            this.description = description;
        }
    }
}

