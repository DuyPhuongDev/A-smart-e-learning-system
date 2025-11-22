package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.response.PermissionDto;
import com.hcmut.lms.usermanagement.model.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    
    @Mapping(target = "description", ignore = true)
    PermissionDto toDto(Permission permission);
    
    List<PermissionDto> toDtoList(List<Permission> permissions);
}

