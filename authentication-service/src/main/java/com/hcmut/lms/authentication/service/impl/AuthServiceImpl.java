package com.hcmut.lms.authentication.service.impl;

import com.hcmut.lms.authentication.client.UserManagementClient;
import com.hcmut.lms.authentication.exception.*;
import com.hcmut.lms.authentication.model.dto.request.*;
import com.hcmut.lms.authentication.model.dto.response.AuthResponse;
import com.hcmut.lms.authentication.model.dto.response.TokenValidationResponse;
import com.hcmut.lms.authentication.model.entity.PasswordResetToken;
import com.hcmut.lms.authentication.model.entity.RefreshToken;
import com.hcmut.lms.authentication.model.entity.UserCredentials;
import com.hcmut.lms.authentication.repository.PasswordResetTokenRepository;
import com.hcmut.lms.authentication.repository.RefreshTokenRepository;
import com.hcmut.lms.authentication.repository.UserCredentialsRepository;
import com.hcmut.lms.authentication.service.AuthService;
import com.hcmut.lms.authentication.util.JwtUtil;
import com.hcmut.lms.authentication.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserCredentialsRepository userCredentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordUtil passwordUtil;
    private final JwtUtil jwtUtil;
    private final UserManagementClient userManagementClient;
    
    @Value("${auth.max-failed-attempts:5}")
    private int maxFailedAttempts;
    
    @Value("${auth.lockout-duration-minutes:30}")
    private int lockoutDurationMinutes;
    
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        UserCredentials credentials = userCredentialsRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));
        
        // Check if account is locked
        if (credentials.getIsAccountLocked()) {
            if (credentials.getLockedUntil() != null && credentials.getLockedUntil().isAfter(LocalDateTime.now())) {
                throw new AccountLockedException(
                    "Account is locked. Please try again after " + credentials.getLockedUntil(),
                    credentials.getLockedUntil()
                );
            } else {
                // Lock expired, unlock account
                credentials.setIsAccountLocked(false);
                credentials.setFailedLoginAttempts(0);
                credentials.setLockedUntil(null);
            }
        }
        
        // Validate password
        if (!passwordUtil.matches(request.getPassword(), credentials.getPasswordHash())) {
            handleFailedLogin(credentials);
            throw new InvalidCredentialsException("Invalid email or password");
        }
        
        // Successful login
        credentials.setFailedLoginAttempts(0);
        credentials.setLastLoginAt(LocalDateTime.now());
        credentials.setIsAccountLocked(false);
        credentials.setLockedUntil(null);
        userCredentialsRepository.save(credentials);
        
        // Get user role from user-management-service
        String role = getUserRole(credentials.getUserId());
        
        // Generate tokens with role
        String accessToken = jwtUtil.generateAccessToken(credentials.getUserId(), credentials.getEmail(), role);
        String refreshTokenValue = jwtUtil.generateRefreshToken(credentials.getUserId(), credentials.getEmail(), role);
        
        // Save refresh token
        String refreshTokenHash = passwordUtil.hashToken(refreshTokenValue);
        @SuppressWarnings("null")
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .userId(credentials.getUserId())
                .tokenHash(refreshTokenHash)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .isRevoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        refreshTokenRepository.save(refreshTokenEntity);
        
        log.info("User {} logged in successfully", credentials.getEmail());
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenValue)
                .tokenType("Bearer")
                .userId(credentials.getUserId())
                .email(credentials.getEmail())
                .build();
    }
    
    private void handleFailedLogin(UserCredentials credentials) {
        int attempts = credentials.getFailedLoginAttempts() + 1;
        credentials.setFailedLoginAttempts(attempts);
        
        if (attempts >= maxFailedAttempts) {
            credentials.setIsAccountLocked(true);
            credentials.setLockedUntil(LocalDateTime.now().plusMinutes(lockoutDurationMinutes));
            log.warn("Account {} locked after {} failed attempts", credentials.getEmail(), attempts);
        }
        
        userCredentialsRepository.save(credentials);
    }
    
    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        String refreshTokenHash = passwordUtil.hashToken(refreshTokenValue);
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(refreshTokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        
        if (refreshToken.getIsRevoked()) {
            throw new InvalidTokenException("Token already revoked");
        }
        
        refreshToken.setIsRevoked(true);
        refreshToken.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(refreshToken);
        
        log.info("User {} logged out", refreshToken.getUserId());
    }
    
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenHash = passwordUtil.hashToken(request.getRefreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(refreshTokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));
        
        if (refreshToken.getIsRevoked()) {
            throw new InvalidTokenException("Token has been revoked");
        }
        
        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Refresh token has expired");
        }
        
        // Get user credentials
        UserCredentials credentials = userCredentialsRepository.findByUserId(refreshToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", refreshToken.getUserId()));
        
        // Get user role from user-management-service
        String role = getUserRole(credentials.getUserId());
        
        // Generate new access token with role
        String newAccessToken = jwtUtil.generateAccessToken(credentials.getUserId(), credentials.getEmail(), role);
        
        log.info("Token refreshed for user {}", credentials.getEmail());
        
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(request.getRefreshToken()) // Return same refresh token
                .tokenType("Bearer")
                .userId(credentials.getUserId())
                .email(credentials.getEmail())
                .build();
    }
    
    @Override
    public TokenValidationResponse validateToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .build();
            }
            
            UUID userId = jwtUtil.extractUserId(token);
            String email = jwtUtil.extractEmail(token);
            String role = jwtUtil.extractRole(token);
            
            return TokenValidationResponse.builder()
                    .valid(true)
                    .userId(userId)
                    .email(email)
                    .role(role)
                    .build();
        } catch (Exception e) {
            log.warn("Token validation failed: {}", e.getMessage());
            return TokenValidationResponse.builder()
                    .valid(false)
                    .build();
        }
    }
    
    /**
     * Get user role from user-management-service
     */
    private String getUserRole(UUID userId) {
        try {
            UserManagementClient.UserRoleResponse roleResponse = userManagementClient.getUserRole(userId);
            return roleResponse.getRoleName();
        } catch (Exception e) {
            log.warn("Failed to get user role for {}: {}", userId, e.getMessage());
            return "UNKNOWN"; // Default role if service unavailable
        }
    }
    
    @Override
    @Transactional
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        UserCredentials credentials = userCredentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // Validate current password
        if (!passwordUtil.matches(request.getCurrentPassword(), credentials.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        
        // Update password
        credentials.setPasswordHash(passwordUtil.hashPassword(request.getNewPassword()));
        userCredentialsRepository.save(credentials);
        
        // Revoke all refresh tokens (force re-login)
        refreshTokenRepository.revokeAllByUserId(userId, LocalDateTime.now());
        
        log.info("Password changed for user {}", credentials.getEmail());
    }
    
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        UserCredentials credentials = userCredentialsRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
        
        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        String resetTokenHash = passwordUtil.hashToken(resetToken);
        
        // Save reset token
        @SuppressWarnings("null")
        PasswordResetToken resetTokenEntity = PasswordResetToken.builder()
                .userId(credentials.getUserId())
                .tokenHash(resetTokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1)) // 1 hour expiration
                .isUsed(false)
                .createdAt(LocalDateTime.now())
                .build();
        passwordResetTokenRepository.save(resetTokenEntity);
        
        // TODO: Send email with reset token
        log.info("Password reset token generated for user {}. Token: {}", request.getEmail(), resetToken);
        // In production, send email instead of logging
    }
    
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        String resetTokenHash = passwordUtil.hashToken(request.getToken());
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByTokenHash(resetTokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid reset token"));
        
        if (passwordResetToken.getIsUsed()) {
            throw new InvalidTokenException("Reset token has already been used");
        }
        
        if (passwordResetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Reset token has expired");
        }
        
        // Update password
        UserCredentials credentials = userCredentialsRepository.findByUserId(passwordResetToken.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", passwordResetToken.getUserId()));
        
        credentials.setPasswordHash(passwordUtil.hashPassword(request.getNewPassword()));
        userCredentialsRepository.save(credentials);
        
        // Mark token as used
        passwordResetToken.setIsUsed(true);
        passwordResetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(passwordResetToken);
        
        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllByUserId(credentials.getUserId(), LocalDateTime.now());
        
        log.info("Password reset successful for user {}", credentials.getEmail());
    }
}

