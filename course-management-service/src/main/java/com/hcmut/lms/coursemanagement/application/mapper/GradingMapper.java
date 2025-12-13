package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.GradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.GradingResponse;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GradingMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjectGradings", ignore = true)
    @Mapping(target = "classSectionGradings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Grading toEntity(GradingRequest request);
    
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    GradingResponse toResponse(Grading entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subjectGradings", ignore = true)
    @Mapping(target = "classSectionGradings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(GradingRequest request, @MappingTarget Grading entity);
}

