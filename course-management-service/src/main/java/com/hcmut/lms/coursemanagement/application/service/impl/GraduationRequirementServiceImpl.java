package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.InternalGraduationRequirementResponse;
import com.hcmut.lms.coursemanagement.application.service.GraduationRequirementService;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement.GraduationRequirement;
import com.hcmut.lms.coursemanagement.repository.GraduationRequirementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GraduationRequirementServiceImpl implements GraduationRequirementService {

  private final GraduationRequirementRepository graduationRequirementRepository;
  private final UserServiceClient userServiceClient;

  @Override
  @Transactional(readOnly = true)
  public List<InternalGraduationRequirementResponse> getActiveRequirementsByStudentId(UUID studentId) {
    log.info("Getting active graduation requirements for student id: {}", studentId);

    UserResponse student = userServiceClient.getUserById(studentId);
    if (student == null) {
      throw new EntityNotFoundException("Student not found with id: " + studentId);
    }

    if (student.getDepartmentId() == null || student.getIntakeYearId() == null) {
      throw new EntityNotFoundException("Student does not have department or intake year configured: " + studentId);
    }

    List<GraduationRequirement> requirements =
        graduationRequirementRepository.findByDepartmentIdAndIntakeYearIdAndIsActiveTrue(
        student.getDepartmentId(), student.getIntakeYearId());

    return requirements.stream()
        .map(requirement -> InternalGraduationRequirementResponse.builder()
            .graduationRequirementId(requirement.getGraduationRequirementId())
            .name(requirement.getName())
            .code(requirement.getCode())
            .description(requirement.getDescription())
            .thresholdValue(requirement.getThresholdValue())
            .unit(requirement.getUnit())
            .evaluationRule(requirement.getEvaluationRule())
            .build())
        .toList();
  }
}

