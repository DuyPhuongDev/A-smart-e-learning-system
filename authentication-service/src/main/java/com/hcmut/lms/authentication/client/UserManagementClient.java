package com.hcmut.lms.authentication.client;

import com.hcmut.lms.common.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management-service", fallback = UserManagementClientFallback.class)
public interface UserManagementClient {
    
    @GetMapping("/api/users/{id}")
    ApiResponse<Object> getUserById(@PathVariable UUID id);
    
    @GetMapping("/api/users/email/{email}")
    ApiResponse<Object> getUserByEmail(@PathVariable String email);
}

