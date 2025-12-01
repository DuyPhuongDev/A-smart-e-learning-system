package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UrlPermissionResponse;
import com.hcmut.lms.usermanagement.model.entity.UrlPermission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UrlPermissionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceFunction", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UrlPermission toEntity(CreateUrlPermissionRequest request);
    
    @Mapping(target = "serviceFunctionId", source = "serviceFunction.id")
    @Mapping(target = "serviceFunctionName", source = "serviceFunction.name")
    UrlPermissionResponse toResponse(UrlPermission urlPermission);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceFunction", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateUrlPermissionRequest request, @MappingTarget UrlPermission urlPermission);
}

