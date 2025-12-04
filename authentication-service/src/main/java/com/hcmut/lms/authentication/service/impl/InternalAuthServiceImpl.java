package com.hcmut.lms.authentication.service.impl;

import com.hcmut.lms.authentication.exception.DuplicateResourceException;
import com.hcmut.lms.authentication.exception.ResourceNotFoundException;
import com.hcmut.lms.authentication.model.dto.request.CreateCredentialsRequest;
import com.hcmut.lms.authentication.model.dto.request.UpdateEmailRequest;
import com.hcmut.lms.authentication.model.entity.UserCredentials;
import com.hcmut.lms.authentication.repository.RefreshTokenRepository;
import com.hcmut.lms.authentication.repository.UserCredentialsRepository;
import com.hcmut.lms.authentication.service.InternalAuthService;
import com.hcmut.lms.authentication.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InternalAuthServiceImpl implements InternalAuthService {
    
    private final UserCredentialsRepository userCredentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordUtil passwordUtil;
    
    @Override
    @Transactional
    public void createCredentials(CreateCredentialsRequest request) {
        // Check if credentials already exist
        if (userCredentialsRepository.existsByUserId(request.getUserId())) {
            throw new DuplicateResourceException("UserCredentials", "userId", request.getUserId());
        }
        
        if (userCredentialsRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("UserCredentials", "email", request.getEmail());
        }
        
        // Hash password
        String passwordHash = passwordUtil.hashPassword(request.getPassword());
        
        // Create credentials
        @SuppressWarnings("null")
        UserCredentials credentialsEntity = UserCredentials.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .passwordHash(passwordHash)
                .isAccountLocked(false)
                .failedLoginAttempts(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        userCredentialsRepository.save(credentialsEntity);
        
        log.info("Credentials created for user {} ({})", request.getEmail(), request.getUserId());
    }
    
    @Override
    @Transactional
    public void lockAccount(UUID userId) {
        UserCredentials credentials = userCredentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        credentials.setIsAccountLocked(true);
        credentials.setLockedUntil(LocalDateTime.now().plusDays(1)); // Lock for 1 day
        userCredentialsRepository.save(credentials);
        
        // Revoke all refresh tokens
        refreshTokenRepository.revokeAllByUserId(userId, LocalDateTime.now());
        
        log.info("Account locked for user {}", userId);
    }
    
    @Override
    @Transactional
    public void unlockAccount(UUID userId) {
        UserCredentials credentials = userCredentialsRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        credentials.setIsAccountLocked(false);
        credentials.setFailedLoginAttempts(0);
        credentials.setLockedUntil(null);
        userCredentialsRepository.save(credentials);
        
        log.info("Account unlocked for user {}", userId);
    }
    
    @Override
    @Transactional
    public void updateEmail(UpdateEmailRequest request) {
        UserCredentials credentials = userCredentialsRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
        
        // Verify old email matches
        if (!credentials.getEmail().equals(request.getOldEmail())) {
            throw new IllegalArgumentException("Old email does not match current email");
        }
        
        // Check if new email already exists
        if (userCredentialsRepository.existsByEmail(request.getNewEmail())) {
            throw new DuplicateResourceException("UserCredentials", "email", request.getNewEmail());
        }
        
        // Update email
        credentials.setEmail(request.getNewEmail());
        userCredentialsRepository.save(credentials);
        
        log.info("Email updated for user {} from {} to {}", 
                request.getUserId(), request.getOldEmail(), request.getNewEmail());
    }
}

