package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.common.dto.ApiResponse;
import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.exception.DuplicateEmailException;
import com.hcmut.lms.usermanagement.exception.UserNotFoundException;
import com.hcmut.lms.usermanagement.mapper.UserMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponseDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.model.entity.UserRole;
import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.UserRepository;
import com.hcmut.lms.usermanagement.service.UserService;
import com.hcmut.lms.usermanagement.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final AuthServiceClient authServiceClient;
    
    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDto> getAllUsers(String search, UserStatus status, String department, UUID roleId, Pageable pageable) {
        Page<User> usersPage;
        
        if (roleId != null) {
            usersPage = userRepository.findByRoleAndFilters(roleId, search, status, pageable);
        } else {
            usersPage = userRepository.findByFilters(search, status, department, pageable);
        }
        
        return usersPage.map(userMapper::toResponseDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public UserDetailResponseDto getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toDetailResponseDto(user);
    }
    
    @Override
    @Transactional
    public UserResponseDto createUser(CreateUserRequestDto dto) {
        // Check if email already exists
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + dto.getEmail());
        }
        
        // Create user entity
        User user = userMapper.toEntity(dto);
        user.setStatus(UserStatus.ACTIVE);
        
        // Assign roles if provided
        if (dto.getRoleIds() != null && !dto.getRoleIds().isEmpty()) {
            Set<UserRole> userRoles = new HashSet<>();
            for (UUID roleId : dto.getRoleIds()) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));
                
                UserRole userRole = UserRole.builder()
                        .user(user)
                        .role(role)
                        .build();
                userRoles.add(userRole);
            }
            user.setUserRoles(userRoles);
        }
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Create credentials in Authentication Service
        String temporaryPassword = PasswordGenerator.generateTemporaryPassword();
        ApiResponse<Void> authResponse = authServiceClient.createUserCredentials(
                savedUser.getId(), 
                savedUser.getEmail(), 
                temporaryPassword
        );
        
        if (authResponse.getStatus() != 200 && authResponse.getStatus() != 201) {
            log.error("Failed to create credentials for user: {}", savedUser.getEmail());
            // Continue anyway - user is created but credentials creation failed
        } else {
            log.info("Created user with temporary password. Email: {}, Password: {}", savedUser.getEmail(), temporaryPassword);
        }
        
        return userMapper.toResponseDto(savedUser);
    }
    
    @Override
    @Transactional
    public UserResponseDto updateUser(UUID id, UpdateUserRequestDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        userMapper.updateEntityFromDto(dto, user);
        User updatedUser = userRepository.save(user);
        
        return userMapper.toResponseDto(updatedUser);
    }
    
    @Override
    @Transactional
    public void deleteUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Soft delete
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
        
        log.info("User deleted (soft): {}", user.getEmail());
    }
    
    @Override
    @Transactional
    public void lockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        user.setStatus(UserStatus.LOCKED);
        userRepository.save(user);
        
        // Lock account in Authentication Service
        authServiceClient.lockUserAccount(user.getId());
        
        log.info("User locked: {}", user.getEmail());
    }
    
    @Override
    @Transactional
    public void unlockUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        
        // Unlock account in Authentication Service
        authServiceClient.unlockUserAccount(user.getId());
        
        log.info("User unlocked: {}", user.getEmail());
    }
    
    @Override
    @Transactional
    public void resetPassword(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        
        // Trigger password reset in Authentication Service
        ApiResponse<Void> response = authServiceClient.resetUserPassword(user.getId(), user.getEmail());
        
        if (response.getStatus() != 200) {
            log.error("Failed to reset password for user: {}", user.getEmail());
            throw new RuntimeException("Failed to reset password");
        }
        
        log.info("Password reset triggered for user: {}", user.getEmail());
    }
}

