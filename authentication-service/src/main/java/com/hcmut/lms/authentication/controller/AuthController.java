package com.hcmut.lms.authentication.controller;

import com.hcmut.lms.authentication.model.dto.request.*;
import com.hcmut.lms.authentication.model.dto.response.AuthResponse;
import com.hcmut.lms.authentication.model.dto.response.TokenValidationResponse;
import com.hcmut.lms.authentication.service.AuthService;
import com.hcmut.lms.authentication.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Authentication Controller
 * Handles user authentication and token management
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/validate-token")
    @ResponseStatus(HttpStatus.OK)
    public TokenValidationResponse validateToken(@RequestHeader("Authorization") String authorization) {
        String token = extractTokenFromHeader(authorization);
        return authService.validateToken(token);
    }
    
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
    }

    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
    }

    @PostMapping("/change-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody ChangePasswordRequest request) {
        String token = extractTokenFromHeader(authorization);
        UUID userId = jwtUtil.extractUserId(token);
        authService.changePassword(userId, request);
    }
    
    private String extractTokenFromHeader(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("Invalid Authorization header");
    }
}


