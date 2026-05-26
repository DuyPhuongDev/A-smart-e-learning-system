package com.hcmut.lms.authentication.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.authentication.exception.DuplicateResourceException;
import com.hcmut.lms.authentication.exception.ResourceNotFoundException;
import com.hcmut.lms.authentication.model.dto.request.CreateCredentialsRequest;
import com.hcmut.lms.authentication.model.dto.request.UpdateEmailRequest;
import com.hcmut.lms.authentication.model.entity.UserCredentials;
import com.hcmut.lms.authentication.repository.RefreshTokenRepository;
import com.hcmut.lms.authentication.repository.UserCredentialsRepository;
import com.hcmut.lms.authentication.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class InternalAuthServiceImplTest {

    @Mock private UserCredentialsRepository userCredentialsRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordUtil passwordUtil;

    @InjectMocks
    private InternalAuthServiceImpl internalAuthService;

    private static final UUID userId = UUID.randomUUID();
    private static final String email = "test@hcmut.edu.vn";
    private static final String password = "password123";

    // === createCredentials tests ===

    @Test
    void createCredentials_shouldCreateCredentials_whenValidRequest() {
        CreateCredentialsRequest request = new CreateCredentialsRequest(userId, email, password);

        when(userCredentialsRepository.existsByUserId(userId)).thenReturn(false);
        when(userCredentialsRepository.existsByEmail(email)).thenReturn(false);
        when(passwordUtil.hashPassword(password)).thenReturn("hashedPassword");
        when(userCredentialsRepository.save(any())).thenReturn(null);

        assertDoesNotThrow(() -> internalAuthService.createCredentials(request));
    }

    @Test
    void createCredentials_shouldThrowException_whenUserIdAlreadyExists() {
        CreateCredentialsRequest request = new CreateCredentialsRequest(userId, email, password);
        when(userCredentialsRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> internalAuthService.createCredentials(request));
    }

    @Test
    void createCredentials_shouldThrowException_whenEmailAlreadyExists() {
        CreateCredentialsRequest request = new CreateCredentialsRequest(userId, email, password);
        when(userCredentialsRepository.existsByUserId(userId)).thenReturn(false);
        when(userCredentialsRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> internalAuthService.createCredentials(request));
    }

    // === lockAccount tests ===

    @Test
    void lockAccount_shouldLockAccount_whenUserExists() {
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hash")
                .isAccountLocked(false).build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));
        when(userCredentialsRepository.save(any())).thenReturn(null);
        when(refreshTokenRepository.revokeAllByUserId(eq(userId), any())).thenReturn(1);

        assertDoesNotThrow(() -> internalAuthService.lockAccount(userId));
        assertTrue(credentials.getIsAccountLocked());
    }

    @Test
    void lockAccount_shouldThrowException_whenUserNotFound() {
        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> internalAuthService.lockAccount(userId));
    }

    // === unlockAccount tests ===

    @Test
    void unlockAccount_shouldUnlockAccount_whenUserExists() {
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hash")
                .isAccountLocked(true).failedLoginAttempts(5).lockedUntil(LocalDateTime.now()).build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));
        when(userCredentialsRepository.save(any())).thenReturn(null);

        assertDoesNotThrow(() -> internalAuthService.unlockAccount(userId));
        assertFalse(credentials.getIsAccountLocked());
        assertEquals(0, credentials.getFailedLoginAttempts());
    }

    @Test
    void unlockAccount_shouldThrowException_whenUserNotFound() {
        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> internalAuthService.unlockAccount(userId));
    }

    // === updateEmail tests ===

    @Test
    void updateEmail_shouldUpdateEmail_whenValidRequest() {
        String newEmail = "newemail@hcmut.edu.vn";
        UpdateEmailRequest request = new UpdateEmailRequest(userId, email, newEmail);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hash").build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));
        when(userCredentialsRepository.existsByEmail(newEmail)).thenReturn(false);
        when(userCredentialsRepository.save(any())).thenReturn(null);

        assertDoesNotThrow(() -> internalAuthService.updateEmail(request));
        assertEquals(newEmail, credentials.getEmail());
    }

    @Test
    void updateEmail_shouldThrowException_whenUserNotFound() {
        String newEmail = "newemail@hcmut.edu.vn";
        UpdateEmailRequest request = new UpdateEmailRequest(userId, email, newEmail);
        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> internalAuthService.updateEmail(request));
    }

    @Test
    void updateEmail_shouldThrowException_whenOldEmailDoesNotMatch() {
        String newEmail = "newemail@hcmut.edu.vn";
        UpdateEmailRequest request = new UpdateEmailRequest(userId, "wrong@email.com", newEmail);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hash").build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));

        assertThrows(IllegalArgumentException.class, () -> internalAuthService.updateEmail(request));
    }

    @Test
    void updateEmail_shouldThrowException_whenNewEmailAlreadyExists() {
        String newEmail = "newemail@hcmut.edu.vn";
        UpdateEmailRequest request = new UpdateEmailRequest(userId, email, newEmail);
        UserCredentials credentials = UserCredentials.builder()
                .userId(userId).email(email).passwordHash("hash").build();

        when(userCredentialsRepository.findByUserId(userId)).thenReturn(Optional.of(credentials));
        when(userCredentialsRepository.existsByEmail(newEmail)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> internalAuthService.updateEmail(request));
    }
}
