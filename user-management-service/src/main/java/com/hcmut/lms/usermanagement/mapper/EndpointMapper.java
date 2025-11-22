package com.hcmut.lms.usermanagement.mapper;

import com.hcmut.lms.usermanagement.model.dto.response.EndpointDto;
import com.hcmut.lms.usermanagement.model.entity.EndpointRegistry;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EndpointMapper {
    
    EndpointDto toDto(EndpointRegistry endpointRegistry);
    
    List<EndpointDto> toDtoList(List<EndpointRegistry> endpointRegistries);
}

