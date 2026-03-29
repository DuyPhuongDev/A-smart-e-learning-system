package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.request.CreateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.response.SystemServiceResponse;
import com.hcmut.lms.usermanagement.model.entity.SystemService;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SystemServiceMapper {
    SystemService toEntity(CreateSystemServiceRequest request);
    SystemServiceResponse toResponse(SystemService systemService);
    void updateEntity(UpdateSystemServiceRequest request, @MappingTarget SystemService systemService);
}

