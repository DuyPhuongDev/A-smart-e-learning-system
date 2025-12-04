package com.hcmut.lms.authentication.client;

import com.hcmut.lms.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserManagementClientFallback implements UserManagementClient {
    
    @Override
    public ApiResponse<Object> getUserById(UUID id) {
        log.error("User Management Service is unavailable. Failed to get user by id: {}", id);
        return null;
    }
    
    @Override
    public ApiResponse<Object> getUserByEmail(String email) {
        log.error("User Management Service is unavailable. Failed to get user by email: {}", email);
        return null;
    }
}

