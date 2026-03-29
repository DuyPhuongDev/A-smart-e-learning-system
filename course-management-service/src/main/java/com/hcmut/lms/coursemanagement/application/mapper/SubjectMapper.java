package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SubjectMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classSections", ignore = true)
    @Mapping(target = "curriculumSubjects", ignore = true)
    @Mapping(target = "subjectGradings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Subject toEntity(SubjectRequest request);
    
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    @Mapping(target = "updatedAt", expression = "java(entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null)")
    @Mapping(target = "category",  expression = "java(entity.getCategory().name())")
    SubjectResponse toResponse(Subject entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classSections", ignore = true)
    @Mapping(target = "curriculumSubjects", ignore = true)
    @Mapping(target = "subjectGradings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SubjectRequest request, @MappingTarget Subject entity);
}

