package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.UpdateGraduationRequirementStatusRequest;
import com.hcmut.lms.personalization.application.dto.response.GraduationRequirementStatusResponse;
import com.hcmut.lms.personalization.application.mapper.GraduationRequirementMapper;
import com.hcmut.lms.personalization.application.service.GraduationRequirementService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.GraduationRequirementResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GraduationRequirementStatus;
import com.hcmut.lms.personalization.repository.GraduationRequirementStatusRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GraduationRequirementServiceImpl implements GraduationRequirementService {

  private final CourseManagementClient courseManagementClient;
  private final GraduationRequirementStatusRepository graduationRequirementStatusRepository;
  private final GraduationRequirementMapper graduationRequirementMapper;

  @Override
  public List<GraduationRequirementStatusResponse> getMyGraduationRequirements(UUID studentId) {
    log.info("Getting graduation requirements for student id: {}", studentId);

    List<GraduationRequirementResponse> requirements = courseManagementClient.getGraduationRequirementsByStudentId(
        studentId);
    Map<UUID, GraduationRequirementStatus> statusByRequirementId = loadStatusByRequirementId(studentId);
    saveMissingStatuses(studentId, requirements, statusByRequirementId);
    return requirements.stream().map(requirement -> toStatusResponse(requirement, statusByRequirementId)).toList();
  }

  @Override
  public GraduationRequirementStatusResponse updateGraduationRequirementStatus(
      UUID studentId,
      UUID graduationRequirementStatusId, UpdateGraduationRequirementStatusRequest request) {

    GraduationRequirementStatus status =
        graduationRequirementStatusRepository.findByGraduationRequirementStatusIdAndStudentId(
            graduationRequirementStatusId, studentId)
        .orElseThrow(() -> new EntityNotFoundException(
            "Graduation requirement status not found with id: " + graduationRequirementStatusId));

    List<GraduationRequirementResponse> activeRequirements =
        courseManagementClient.getGraduationRequirementsByStudentId(
        studentId);
    GraduationRequirementResponse requirement = findActiveRequirement(activeRequirements, status.getGraduationRequirementId());

    status.setIsCompleted(request.getIsCompleted());
    status.setCompletionSemesterId(
        Boolean.TRUE.equals(request.getIsCompleted()) ? request.getCompletionSemesterId() : null);

    GraduationRequirementStatus savedStatus = graduationRequirementStatusRepository.save(status);

    return graduationRequirementMapper.toResponse(requirement, savedStatus);
  }

  private Map<UUID, GraduationRequirementStatus> loadStatusByRequirementId(UUID studentId) {
    return graduationRequirementStatusRepository.findByStudentId(studentId)
        .stream()
        .collect(Collectors.toMap(
            GraduationRequirementStatus::getGraduationRequirementId,
            status -> status,
            (left, right) -> left));
  }

  private void saveMissingStatuses(
      UUID studentId,
      List<GraduationRequirementResponse> requirements,
      Map<UUID, GraduationRequirementStatus> statusByRequirementId) {
    List<GraduationRequirementStatus> missingStatuses = new ArrayList<>();
    for (GraduationRequirementResponse requirement : requirements) {
      UUID requirementId = requirement.getGraduationRequirementId();
      if (statusByRequirementId.containsKey(requirementId)) {
        continue;
      }
      missingStatuses.add(GraduationRequirementStatus.builder()
          .graduationRequirementStatusId(UUID.randomUUID())
          .studentId(studentId)
          .graduationRequirementId(requirementId)
          .isCompleted(Boolean.FALSE)
          .build());
    }

    if (missingStatuses.isEmpty()) {
      return;
    }

    graduationRequirementStatusRepository.saveAll(missingStatuses)
        .forEach(status -> statusByRequirementId.put(status.getGraduationRequirementId(), status));
  }

  private GraduationRequirementStatusResponse toStatusResponse(
      GraduationRequirementResponse requirement,
      Map<UUID, GraduationRequirementStatus> statusByRequirementId) {
    GraduationRequirementStatus status = statusByRequirementId.get(requirement.getGraduationRequirementId());
    if (status == null) {
      throw new IllegalStateException(
          "Graduation requirement status missing for requirementId=" + requirement.getGraduationRequirementId());
    }
    return graduationRequirementMapper.toResponse(requirement, status);
  }

  private GraduationRequirementResponse findActiveRequirement(
      List<GraduationRequirementResponse> activeRequirements,
      UUID graduationRequirementId) {
    return activeRequirements.stream()
        .filter(item -> graduationRequirementId.equals(item.getGraduationRequirementId()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Graduation requirement is not active for this student"));
  }
}

