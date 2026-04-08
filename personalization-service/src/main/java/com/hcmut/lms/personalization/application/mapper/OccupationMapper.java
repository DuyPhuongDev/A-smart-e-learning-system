package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OccupationMapper {

  @Mapping(target = "title", source = "titleVn")
  OccupationResponse toResponse(OccupationData entity);

  @Mapping(target = "title", source = "title")
  @Mapping(target = "skillsCount", ignore = true)
  @Mapping(target = "tasksCount", ignore = true)
  @Mapping(target = "dwaCount", ignore = true)
  OccupationDetailResponse toDetailResponse(OccupationData entity);
}
