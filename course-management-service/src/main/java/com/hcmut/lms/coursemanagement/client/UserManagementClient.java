package com.hcmut.lms.coursemanagement.client;

import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management-service", url = "http://localhost:8082")
public interface UserManagementClient {
    
    @GetMapping("/api/users/internal/{id}")
    UserResponse getUserById(@PathVariable UUID id);
}

