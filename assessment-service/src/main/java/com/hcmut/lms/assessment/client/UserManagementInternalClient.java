package com.hcmut.lms.assessment.client;

import com.hcmut.lms.assessment.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-management-service", contextId = "assessmentUserManagementInternalClient")
public interface UserManagementInternalClient {

    @GetMapping("/api/users/internal/{id}")
    UserResponse getUserById(@PathVariable("id") UUID id);
}
