package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ServiceFunctionResponse;
import com.hcmut.lms.usermanagement.model.entity.ServiceFunction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ServiceFunctionMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "systemService", ignore = true)
    @Mapping(target = "urlPermissions", ignore = true)
    @Mapping(target = "rolesServiceFunctions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ServiceFunction toEntity(CreateServiceFunctionRequest request);
    
    @Mapping(target = "systemServiceId", source = "systemService.id")
    @Mapping(target = "systemServiceName", source = "systemService.name")
    ServiceFunctionResponse toResponse(ServiceFunction serviceFunction);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "systemService", ignore = true)
    @Mapping(target = "urlPermissions", ignore = true)
    @Mapping(target = "rolesServiceFunctions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(UpdateServiceFunctionRequest request, @MappingTarget ServiceFunction serviceFunction);
}

