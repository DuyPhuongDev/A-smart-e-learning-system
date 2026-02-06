package com.hcmut.lms.learning.mapper;

import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.StudyTimeResponse;
import com.hcmut.lms.learning.entity.studytime.StudyTime;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StudyTimeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "studentId", ignore = true)
    @Mapping(target = "endedAt", ignore = true)
    StudyTime toEntity(StudyTimeRequest request);

    StudyTimeResponse toResponse(StudyTime studyTime);
}
