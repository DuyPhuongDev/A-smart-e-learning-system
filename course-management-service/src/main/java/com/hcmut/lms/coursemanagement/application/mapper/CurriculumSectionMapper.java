package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumSectionResponse;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSection;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CurriculumSectionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    @Mapping(target = "curriculumSubjects", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    CurriculumSection toEntity(CurriculumSectionRequest request);
    
    @Mapping(target = "curriculumCode", source = "curriculum.id.code")
    @Mapping(target = "curriculumSpecializationId", source = "curriculum.id.specializationId")
    @Mapping(target = "curriculumIntakeYearId", source = "curriculum.id.intakeYearId")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    CurriculumSectionResponse toResponse(CurriculumSection entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "curriculum", ignore = true)
    @Mapping(target = "curriculumSubjects", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CurriculumSectionRequest request, @MappingTarget CurriculumSection entity);
}

