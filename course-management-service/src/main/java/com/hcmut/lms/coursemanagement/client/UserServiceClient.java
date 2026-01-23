package com.hcmut.lms.coursemanagement.client;

import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class UserServiceClient {

    private final UserManagementClient userManagementClient;

    @CircuitBreaker(name = "user-management-service", fallbackMethod = "getUserFallback")
    public UserResponse getUserById(UUID id) {
        return userManagementClient.getUserById(id);
    }

    public UserResponse getUserFallback(UUID teacherId, Throwable ex) {
        log.warn(
                "User service unavailable. teacherId={}, reason={}",
                teacherId,
                ex.getMessage()
        );
        return null;
    }
}
