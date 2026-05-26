package com.hcmut.lms.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import com.hcmut.lms.authentication.util.JwtUtil;
import com.hcmut.lms.authentication.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private UserCredentialsRepository userCredentialsRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordResetTokenRepository passwordResetTokenRepository;
    @Mock private PasswordUtil passwordUtil;
    @Mock private JwtUtil jwtUtil;
    @Mock private UserManagementClient userManagementClient;

    @InjectMocks
    private AuthServiceImpl authService;

    private static final UUID userId = UUID.randomUUID();
    private static final String email = "test@hcmut.edu.vn";
    private static final String password = "password123";
    private static final String accessToken = "access-token-value";
    private static final String refreshTokenValue = "refresh-token-value";
    private static final String refreshTokenHash = "hashed-refresh-token";
    private static final String role = "STUDENT";

    // === login tests ===

    @Test
    void login_shouldReturnAuthResponse_whenValidCredentials() {
        ReflectionTestUtils.setField(authService, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(authService, "lockoutDurationMinutes", 30);

        LoginRequest request = new LoginRequest(email, password);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(false).failedLoginAttempts(0).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches(password, "hashed")).thenReturn(true);
        when(userManagementClient.getUserRole(userId))
                .thenReturn(UserManagementClient.UserRoleResponse.builder().userId(userId).roleName(role).build());
        when(jwtUtil.generateAccessToken(userId, email, role)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(userId, email, role)).thenReturn(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshTokenValue, response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(userId, response.getUserId());
        assertEquals(email, response.getEmail());
        assertEquals(role, response.getRole());
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenEmailNotFound() {
        LoginRequest request = new LoginRequest(email, password);
        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowInvalidCredentialsException_whenWrongPassword() {
        ReflectionTestUtils.setField(authService, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(authService, "lockoutDurationMinutes", 30);

        LoginRequest request = new LoginRequest(email, password);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(false).failedLoginAttempts(0).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches(password, "hashed")).thenReturn(false);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowAccountLockedException_whenAccountLockedAndNotExpired() {
        LoginRequest request = new LoginRequest(email, password);
        LocalDateTime futureLock = LocalDateTime.now().plusMinutes(10);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(true).lockedUntil(futureLock).failedLoginAttempts(5).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));

        assertThrows(AccountLockedException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldUnlockAccount_whenLockedButLockedUntilNull() {
        ReflectionTestUtils.setField(authService, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(authService, "lockoutDurationMinutes", 30);

        LoginRequest request = new LoginRequest(email, password);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(true).lockedUntil(null).failedLoginAttempts(5).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches(password, "hashed")).thenReturn(true);
        when(userManagementClient.getUserRole(userId))
                .thenReturn(UserManagementClient.UserRoleResponse.builder().userId(userId).roleName(role).build());
        when(jwtUtil.generateAccessToken(userId, email, role)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(userId, email, role)).thenReturn(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        AuthResponse response = authService.login(request);
        assertNotNull(response);
    }

    @Test
    void login_shouldUnlockAccount_whenLockExpired() {
        ReflectionTestUtils.setField(authService, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(authService, "lockoutDurationMinutes", 30);

        LoginRequest request = new LoginRequest(email, password);
        LocalDateTime pastLock = LocalDateTime.now().minusMinutes(10);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(true).lockedUntil(pastLock).failedLoginAttempts(5).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches(password, "hashed")).thenReturn(true);
        when(userManagementClient.getUserRole(userId))
                .thenReturn(UserManagementClient.UserRoleResponse.builder().userId(userId).roleName(role).build());
        when(jwtUtil.generateAccessToken(userId, email, role)).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(userId, email, role)).thenReturn(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        AuthResponse response = authService.login(request);
        assertNotNull(response);
    }

    @Test
    void login_shouldLockAccount_whenMaxFailedAttemptsReached() {
        ReflectionTestUtils.setField(authService, "maxFailedAttempts", 3);
        ReflectionTestUtils.setField(authService, "lockoutDurationMinutes", 30);

        LoginRequest request = new LoginRequest(email, password);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(false).failedLoginAttempts(2).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches(password, "hashed")).thenReturn(false);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldUseDefaultRole_whenGetUserRoleFails() {
        ReflectionTestUtils.setField(authService, "maxFailedAttempts", 5);
        ReflectionTestUtils.setField(authService, "lockoutDurationMinutes", 30);

        LoginRequest request = new LoginRequest(email, password);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hashed")
                .isAccountLocked(false).failedLoginAttempts(0).build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches(password, "hashed")).thenReturn(true);
        when(userManagementClient.getUserRole(userId)).thenThrow(new RuntimeException("Service down"));
        when(jwtUtil.generateAccessToken(userId, email, "UNKNOWN")).thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(userId, email, "UNKNOWN")).thenReturn(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        when(refreshTokenRepository.save(any())).thenReturn(null);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        AuthResponse response = authService.login(request);
        assertEquals("UNKNOWN", response.getRole());
    }

    // === logout tests ===

    @Test
    void logout_shouldRevokeToken_whenValidToken() {
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(refreshTokenHash).isRevoked(false)
                .userCredentials(UserCredentials.builder().userId(userId).build()).build();
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(refreshToken));
        when(refreshTokenRepository.save(any())).thenReturn(null);

        assertDoesNotThrow(() -> authService.logout(refreshTokenValue));
    }

    @Test
    void logout_shouldThrowInvalidTokenException_whenTokenNotFound() {
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.logout(refreshTokenValue));
    }

    @Test
    void logout_shouldThrowInvalidTokenException_whenTokenAlreadyRevoked() {
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(refreshTokenHash).isRevoked(true).build();
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(refreshToken));

        assertThrows(InvalidTokenException.class, () -> authService.logout(refreshTokenValue));
    }

    // === refreshToken tests ===

    @Test
    void refreshToken_shouldReturnNewAccessToken_whenValidRefreshToken() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(refreshTokenHash).isRevoked(false)
                .expiresAt(LocalDateTime.now().plusDays(1))
                .userCredentials(UserCredentials.builder().userId(userId).email(email).build()).build();
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(refreshToken));
        when(userManagementClient.getUserRole(userId))
                .thenReturn(UserManagementClient.UserRoleResponse.builder().userId(userId).roleName(role).build());
        when(jwtUtil.generateAccessToken(userId, email, role)).thenReturn(accessToken);

        AuthResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals(accessToken, response.getAccessToken());
        assertEquals(refreshTokenValue, response.getRefreshToken());
    }

    @Test
    void refreshToken_shouldThrowInvalidTokenException_whenTokenNotFound() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshToken_shouldThrowInvalidTokenException_whenTokenRevoked() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(refreshTokenHash).isRevoked(true).build();
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(refreshToken));

        assertThrows(InvalidTokenException.class, () -> authService.refreshToken(request));
    }

    @Test
    void refreshToken_shouldThrowTokenExpiredException_whenTokenExpired() {
        RefreshTokenRequest request = new RefreshTokenRequest(refreshTokenValue);
        when(passwordUtil.hashToken(refreshTokenValue)).thenReturn(refreshTokenHash);
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenHash(refreshTokenHash).isRevoked(false)
                .expiresAt(LocalDateTime.now().minusDays(1)).build();
        when(refreshTokenRepository.findByTokenHash(refreshTokenHash)).thenReturn(Optional.of(refreshToken));

        assertThrows(TokenExpiredException.class, () -> authService.refreshToken(request));
    }

    // === validateToken tests ===

    @Test
    void validateToken_shouldReturnValidTrue_whenValidToken() {
        when(jwtUtil.validateToken(accessToken)).thenReturn(true);
        when(jwtUtil.extractUserId(accessToken)).thenReturn(userId);
        when(jwtUtil.extractEmail(accessToken)).thenReturn(email);
        when(jwtUtil.extractRole(accessToken)).thenReturn(role);

        TokenValidationResponse response = authService.validateToken(accessToken);

        assertTrue(response.getValid());
        assertEquals(userId, response.getUserId());
        assertEquals(email, response.getEmail());
        assertEquals(role, response.getRole());
    }

    @Test
    void validateToken_shouldReturnValidFalse_whenInvalidToken() {
        when(jwtUtil.validateToken(accessToken)).thenReturn(false);

        TokenValidationResponse response = authService.validateToken(accessToken);

        assertFalse(response.getValid());
        assertNull(response.getUserId());
    }

    @Test
    void validateToken_shouldReturnValidFalse_whenExceptionOccurs() {
        when(jwtUtil.validateToken(accessToken)).thenThrow(new RuntimeException("Token malformed"));

        TokenValidationResponse response = authService.validateToken(accessToken);

        assertFalse(response.getValid());
    }

    // === changePassword tests ===

    @Test
    void changePassword_shouldUpdatePassword_whenValidRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("oldHash").build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches("oldPass", "oldHash")).thenReturn(true);
        when(passwordUtil.hashPassword("newPass")).thenReturn("newHash");
        when(userCredentialsRepository.save(any())).thenReturn(null);
        when(refreshTokenRepository.revokeAllByUserId(eq(userId), any())).thenReturn(1);

        assertDoesNotThrow(() -> authService.changePassword(userId, request));
    }

    @Test
    void changePassword_shouldThrowResourceNotFoundException_whenUserNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.changePassword(userId, request));
    }

    @Test
    void changePassword_shouldThrowInvalidCredentialsException_whenWrongCurrentPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPass", "newPass");
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("oldHash").build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));
        when(passwordUtil.matches("oldPass", "oldHash")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.changePassword(userId, request));
    }

    // === forgotPassword tests ===

    @Test
    void forgotPassword_shouldGenerateResetToken_whenEmailExists() {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hash").build();

        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.of(credentials));
        when(passwordUtil.hashToken(any())).thenReturn("hashedResetToken");
        when(passwordResetTokenRepository.save(any())).thenReturn(null);

        assertDoesNotThrow(() -> authService.forgotPassword(request));
    }

    @Test
    void forgotPassword_shouldThrowResourceNotFoundException_whenEmailNotFound() {
        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        when(userCredentialsRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.forgotPassword(request));
    }

    // === resetPassword tests ===

    @Test
    void resetPassword_shouldResetPassword_whenValidToken() {
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword");
        String resetTokenHash = "hashedResetToken";
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(resetTokenHash).isUsed(false)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .userCredentials(UserCredentials.builder().userId(userId).email(email).passwordHash("oldHash").build())
                .build();

        when(passwordUtil.hashToken("reset-token")).thenReturn(resetTokenHash);
        when(passwordResetTokenRepository.findByTokenHash(resetTokenHash)).thenReturn(Optional.of(resetToken));
        when(passwordUtil.hashPassword("newPassword")).thenReturn("newHash");
        when(userCredentialsRepository.save(any())).thenReturn(null);
        when(passwordResetTokenRepository.save(any())).thenReturn(null);
        when(refreshTokenRepository.revokeAllByUserId(eq(userId), any())).thenReturn(1);

        assertDoesNotThrow(() -> authService.resetPassword(request));
    }

    @Test
    void resetPassword_shouldThrowInvalidTokenException_whenTokenNotFound() {
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword");
        when(passwordUtil.hashToken("reset-token")).thenReturn("hashedResetToken");
        when(passwordResetTokenRepository.findByTokenHash("hashedResetToken")).thenReturn(Optional.empty());

        assertThrows(InvalidTokenException.class, () -> authService.resetPassword(request));
    }

    @Test
    void resetPassword_shouldThrowInvalidTokenException_whenTokenAlreadyUsed() {
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword");
        String resetTokenHash = "hashedResetToken";
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(resetTokenHash).isUsed(true).build();

        when(passwordUtil.hashToken("reset-token")).thenReturn(resetTokenHash);
        when(passwordResetTokenRepository.findByTokenHash(resetTokenHash)).thenReturn(Optional.of(resetToken));

        assertThrows(InvalidTokenException.class, () -> authService.resetPassword(request));
    }

    @Test
    void resetPassword_shouldThrowTokenExpiredException_whenTokenExpired() {
        ResetPasswordRequest request = new ResetPasswordRequest("reset-token", "newPassword");
        String resetTokenHash = "hashedResetToken";
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .tokenHash(resetTokenHash).isUsed(false)
                .expiresAt(LocalDateTime.now().minusHours(1)).build();

        when(passwordUtil.hashToken("reset-token")).thenReturn(resetTokenHash);
        when(passwordResetTokenRepository.findByTokenHash(resetTokenHash)).thenReturn(Optional.of(resetToken));

        assertThrows(TokenExpiredException.class, () -> authService.resetPassword(request));
    }
}
