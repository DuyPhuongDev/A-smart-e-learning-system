package com.hcmut.lms.authentication.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserManagementClientFallback implements UserManagementClient {
    
    @Override
    public Object getUserById(UUID id) {
        log.error("User Management Service is unavailable. Failed to get user by id: {}", id);
        throw new RuntimeException("User Management Service is temporarily unavailable");
    }
    
    @Override
    public Object getUserByEmail(String email) {
        log.error("User Management Service is unavailable. Failed to get user by email: {}", email);
        throw new RuntimeException("User Management Service is temporarily unavailable");
    }
    
    @Override
    public UserRoleResponse getUserRole(UUID userId) {
        log.error("User Management Service is unavailable. Failed to get user role for: {}", userId);
        throw new RuntimeException("User Management Service is temporarily unavailable");
    }
}

