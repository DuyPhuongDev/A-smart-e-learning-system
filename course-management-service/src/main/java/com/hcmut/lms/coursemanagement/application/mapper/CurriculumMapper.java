package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CurriculumMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "intakeYear", ignore = true)
    @Mapping(target = "curriculumSections", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Curriculum toEntity(CurriculumRequest request);
    
    @Mapping(target = "code", source = "id.code")
    @Mapping(target = "specializationId", source = "id.specializationId")
    @Mapping(target = "specializationName", source = "specialization.name")
    @Mapping(target = "intakeYearId", source = "id.intakeYearId")
    @Mapping(target = "intakeYearStartDate", source = "intakeYear.startDate")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    CurriculumResponse toResponse(Curriculum entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "intakeYear", ignore = true)
    @Mapping(target = "curriculumSections", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CurriculumRequest request, @MappingTarget Curriculum entity);
}

