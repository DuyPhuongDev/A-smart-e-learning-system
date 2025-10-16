package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponseDto;
import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {
    
    Page<UserResponseDto> getAllUsers(String search, UserStatus status, String department, UUID roleId, Pageable pageable);
    
    UserDetailResponseDto getUserById(UUID id);
    
    UserResponseDto createUser(CreateUserRequestDto dto);
    
    UserResponseDto updateUser(UUID id, UpdateUserRequestDto dto);
    
    void deleteUser(UUID id);
    
    void lockUser(UUID id);
    
    void unlockUser(UUID id);
    
    void resetPassword(UUID id);
}

