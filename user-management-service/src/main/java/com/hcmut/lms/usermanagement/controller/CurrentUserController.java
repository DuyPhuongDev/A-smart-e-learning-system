package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.usermanagement.config.UserContextHolder;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

/**
 * Controller for current authenticated user operations
 * 
 * These endpoints operate on the currently authenticated user,
 * whose information is extracted from headers set by API Gateway
 */
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class CurrentUserController {

    private final UserService userService;

    /**
     * Get current user's profile
     * 
     * The user ID is extracted from the X-User-Id header set by API Gateway
     * after JWT validation
     */
    @GetMapping
    public UserResponse getCurrentUser(@CurrentUser CurrentUserInfo userInfo) {
        UUID userId = userInfo.getId();
        
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                    "User not authenticated");
        }
        
        return userService.getById(userId);
    }

    /**
     * Get current user's email (from gateway header)
     * Useful for debugging/verification
     */
    @GetMapping("/email")
    public String getCurrentUserEmail(@CurrentUser CurrentUserInfo userInfo) {
        String email = userInfo.getEmail();
        
        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                    "User not authenticated");
        }
        
        return email;
    }
}

