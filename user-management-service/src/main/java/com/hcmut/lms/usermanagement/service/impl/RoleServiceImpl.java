package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.exception.*;
import com.hcmut.lms.usermanagement.mapper.RoleMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponseDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.model.entity.UserRole;
import com.hcmut.lms.usermanagement.model.enums.RoleType;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.UserRepository;
import com.hcmut.lms.usermanagement.repository.UserRoleRepository;
import com.hcmut.lms.usermanagement.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMapper roleMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAllRoles(boolean includeCustom) {
        List<Role> roles;
        if (includeCustom) {
            roles = roleRepository.findByIsActiveTrue();
        } else {
            roles = roleRepository.findByType(RoleType.SYSTEM_PREDEFINED);
        }
        return roleMapper.toResponseDtoList(roles);
    }
    
    @Override
    @Transactional(readOnly = true)
    public RoleDetailResponseDto getRoleById(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        return roleMapper.toDetailResponseDto(role);
    }
    
    @Override
    @Transactional
    public RoleResponseDto createRole(CreateRoleRequestDto dto) {
        // Check if role name already exists
        if (roleRepository.existsByName(dto.getName())) {
            throw new DuplicateRoleNameException("Role name already exists: " + dto.getName());
        }
        
        Role role = roleMapper.toEntity(dto);
        role.setType(RoleType.CUSTOM);
        role.setIsActive(true);
        
        Role savedRole = roleRepository.save(role);
        log.info("Created custom role: {}", savedRole.getName());
        
        return roleMapper.toResponseDto(savedRole);
    }
    
    @Override
    @Transactional
    public RoleResponseDto updateRole(UUID id, UpdateRoleRequestDto dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        
        // Only allow updating custom roles
        if (role.getType() == RoleType.SYSTEM_PREDEFINED) {
            throw new SystemRoleModificationException("Cannot modify system predefined role: " + role.getName());
        }
        
        // Check if new name already exists (if changing name)
        if (dto.getName() != null && !dto.getName().equals(role.getName())) {
            if (roleRepository.existsByName(dto.getName())) {
                throw new DuplicateRoleNameException("Role name already exists: " + dto.getName());
            }
            role.setName(dto.getName());
        }
        
        if (dto.getDescription() != null) {
            role.setDescription(dto.getDescription());
        }
        
        if (dto.getIsActive() != null) {
            role.setIsActive(dto.getIsActive());
        }
        
        Role updatedRole = roleRepository.save(role);
        log.info("Updated role: {}", updatedRole.getName());
        
        return roleMapper.toResponseDto(updatedRole);
    }
    
    @Override
    @Transactional
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + id));
        
        // Only allow deleting custom roles
        if (role.getType() == RoleType.SYSTEM_PREDEFINED) {
            throw new SystemRoleModificationException("Cannot delete system predefined role: " + role.getName());
        }
        
        // Check if role is assigned to any users
        if (userRoleRepository.existsByRoleId(id)) {
            throw new RoleInUseException("Cannot delete role that is assigned to users: " + role.getName());
        }
        
        roleRepository.delete(role);
        log.info("Deleted role: {}", role.getName());
    }
    
    @Override
    @Transactional
    public void assignRoleToUser(UUID userId, UUID roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found with id: " + roleId));
        
        // Check if already assigned
        if (userRoleRepository.existsByUserIdAndRoleId(userId, roleId)) {
            log.warn("Role {} already assigned to user {}", role.getName(), user.getEmail());
            return;
        }
        
        UserRole userRole = UserRole.builder()
                .user(user)
                .role(role)
                .build();
        
        userRoleRepository.save(userRole);
        log.info("Assigned role {} to user {}", role.getName(), user.getEmail());
    }
    
    @Override
    @Transactional
    public void removeRoleFromUser(UUID userId, UUID roleId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        
        if (!roleRepository.existsById(roleId)) {
            throw new RoleNotFoundException("Role not found with id: " + roleId);
        }
        
        userRoleRepository.deleteByUserIdAndRoleId(userId, roleId);
        log.info("Removed role from user");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getUserRoles(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        
        List<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        List<Role> roles = userRoles.stream()
                .map(UserRole::getRole)
                .toList();
        
        return roleMapper.toResponseDtoList(roles);
    }
}

