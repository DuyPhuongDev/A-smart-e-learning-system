package com.hcmut.lms.assessment.mapper;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AssessmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "assessmentStatus", ignore = true)
    @Mapping(target = "assessmentQuestions", ignore = true)
    Assessment toEntity(AssessmentRequest request);

    @Mapping(target = "numberQuestions", expression = "java(assessment.getAssessmentQuestions().size())")
    AssessmentResponse toResponse(Assessment assessment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "classId", ignore = true)
    @Mapping(target = "assessmentType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "assessmentQuestions", ignore = true)
    void updateEntity(AssessmentRequest request, @MappingTarget Assessment assessment);
}
