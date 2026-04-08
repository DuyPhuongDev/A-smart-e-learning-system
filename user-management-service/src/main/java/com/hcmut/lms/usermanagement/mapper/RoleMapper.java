package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateRoleRequest;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponse;
import com.hcmut.lms.usermanagement.model.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RoleMapper {
    Role toEntity(CreateRoleRequest request);
    RoleResponse toResponse(Role role);
    void updateEntity(UpdateRoleRequest request, @MappingTarget Role role);
}
