package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponseDto;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    
    List<RoleResponseDto> getAllRoles(boolean includeCustom);
    
    RoleDetailResponseDto getRoleById(UUID id);
    
    RoleResponseDto createRole(CreateRoleRequestDto dto);
    
    RoleResponseDto updateRole(UUID id, UpdateRoleRequestDto dto);
    
    void deleteRole(UUID id);
    
    void assignRoleToUser(UUID userId, UUID roleId);
    
    void removeRoleFromUser(UUID userId, UUID roleId);
    
    List<RoleResponseDto> getUserRoles(UUID userId);
}

