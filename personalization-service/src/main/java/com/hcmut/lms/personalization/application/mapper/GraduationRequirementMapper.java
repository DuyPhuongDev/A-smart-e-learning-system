package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.GraduationRequirementStatusResponse;
import com.hcmut.lms.personalization.client.dto.GraduationRequirementResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GraduationRequirementStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GraduationRequirementMapper {

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "graduationRequirementId", source = "graduationRequirementId")
  @Mapping(target = "name", source = "name")
  @Mapping(target = "code", source = "code")
  @Mapping(target = "description", source = "description")
  @Mapping(target = "thresholdValue", source = "thresholdValue")
  @Mapping(target = "unit", source = "unit")
  @Mapping(target = "evaluationRule", source = "evaluationRule")
  void mergeRequirement(
      GraduationRequirementResponse source, @MappingTarget GraduationRequirementStatusResponse target);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "graduationRequirementStatusId", source = "graduationRequirementStatusId")
  @Mapping(target = "graduationRequirementId", source = "graduationRequirementId")
  @Mapping(target = "isCompleted", source = "isCompleted")
  @Mapping(target = "completionSemesterId", source = "completionSemesterId")
  void mergeStatus(GraduationRequirementStatus source, @MappingTarget GraduationRequirementStatusResponse target);

  default GraduationRequirementStatusResponse toResponse(
      GraduationRequirementResponse requirement,
      GraduationRequirementStatus status) {
    GraduationRequirementStatusResponse response = new GraduationRequirementStatusResponse();
    if (requirement != null) {
      mergeRequirement(requirement, response);
    }
    if (status != null) {
      mergeStatus(status, response);
    }
    if (response.getGraduationRequirementId() == null && status != null) {
      response.setGraduationRequirementId(status.getGraduationRequirementId());
    }
    if (response.getIsCompleted() == null) {
      response.setIsCompleted(Boolean.FALSE);
    }
    return response;
  }
}
