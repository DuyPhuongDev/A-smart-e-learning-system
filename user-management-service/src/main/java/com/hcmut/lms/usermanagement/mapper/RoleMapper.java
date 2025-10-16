package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponseDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface RoleMapper {
    
    RoleResponseDto toResponseDto(Role role);
    
    RoleDetailResponseDto toDetailResponseDto(Role role);
    
    List<RoleResponseDto> toResponseDtoList(List<Role> roles);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    Role toEntity(CreateRoleRequestDto dto);
    
    default RoleResponseDto userRoleToRoleResponseDto(UserRole userRole) {
        if (userRole == null || userRole.getRole() == null) {
            return null;
        }
        return toResponseDto(userRole.getRole());
    }
    
    default List<RoleResponseDto> userRolesToRoleResponseDtos(List<UserRole> userRoles) {
        if (userRoles == null) {
            return List.of();
        }
        return userRoles.stream()
                .map(this::userRoleToRoleResponseDto)
                .toList();
    }
}

