package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.IntakeYearRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.IntakeYearResponse;
import com.hcmut.lms.coursemanagement.domain.entity.intakeYear.IntakeYear;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface IntakeYearMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculums", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    IntakeYear toEntity(IntakeYearRequest request);
    
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    IntakeYearResponse toResponse(IntakeYear entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculums", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(IntakeYearRequest request, @MappingTarget IntakeYear entity);
}

