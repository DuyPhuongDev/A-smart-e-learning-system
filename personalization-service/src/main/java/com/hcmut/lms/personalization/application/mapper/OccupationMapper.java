package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.entity.OccupationData;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OccupationMapper {

    OccupationResponse toResponse(OccupationData entity);
}
