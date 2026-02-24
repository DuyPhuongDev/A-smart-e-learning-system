package com.hcmut.lms.coursemanagement.client;

import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
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
                ex.getMessage());
        return null;
    }

    @CircuitBreaker(name = "user-management-service", fallbackMethod = "getAllTeachersFallback")
    public List<UserResponse> getAllTeachers() {
        return userManagementClient.getAllTeachers();
    }

    public List<UserResponse> getAllTeachersFallback(Throwable ex) {
        log.warn("User service unavailable for teachers lookup: {}", ex.getMessage());
        return Collections.emptyList();
    }
}
