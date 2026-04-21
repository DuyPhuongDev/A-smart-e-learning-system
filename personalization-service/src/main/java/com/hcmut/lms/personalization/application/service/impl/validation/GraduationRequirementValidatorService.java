package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.dto.request.WizardGradRequirementUpdate;
import com.hcmut.lms.personalization.application.dto.response.FeasibilityMissingRequirementResponse;
import com.hcmut.lms.personalization.application.service.impl.validation.model.GraduationRequirementCheckResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.GraduationRequirementResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GraduationRequirementStatus;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.repository.GraduationRequirementStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraduationRequirementValidatorService {

  private final GraduationRequirementStatusRepository graduationRequirementStatusRepository;
  private final CourseManagementClient courseManagementClient;

  public GraduationRequirementCheckResult validate(UUID studentId, LearningGoal goal) {
    return validate(studentId, goal, null);
  }

  public GraduationRequirementCheckResult validate(
      UUID studentId, LearningGoal goal,
      List<WizardGradRequirementUpdate> graduationRequirementUpdates) {
    log.info("Validating graduation requirements for student {}", studentId);

    List<GraduationRequirementStatus> statuses = graduationRequirementStatusRepository.findByStudentId(studentId);

    if (graduationRequirementUpdates != null && !graduationRequirementUpdates.isEmpty()) {
      Map<UUID, WizardGradRequirementUpdate> updateMap = graduationRequirementUpdates.stream()
          .collect(Collectors.toMap(
              WizardGradRequirementUpdate::getGraduationRequirementStatusId, Function.identity(),
              (existing, replacement) -> replacement));

      statuses = statuses.stream().map(status -> {
        WizardGradRequirementUpdate update = updateMap.get(status.getGraduationRequirementStatusId());
        if (update == null) {
          return status;
        }
        GraduationRequirementStatus copy = GraduationRequirementStatus.builder()
            .graduationRequirementStatusId(status.getGraduationRequirementStatusId())
            .studentId(status.getStudentId())
            .graduationRequirementId(status.getGraduationRequirementId())
            .isCompleted(update.getIsCompleted())
            .completionSemesterId(update.getCompletionSemesterId())
            .build();
        copy.setCreatedAt(status.getCreatedAt());
        copy.setUpdatedAt(status.getUpdatedAt());
        return copy;
      }).toList();
    }

    List<GraduationRequirementStatus> missing = statuses.stream()
        .filter(status -> Boolean.FALSE.equals(status.getIsCompleted()))
        .toList();

    Map<UUID, GraduationRequirementResponse> requirementMetadataById = fetchRequirementMetadata(studentId);

    List<FeasibilityMissingRequirementResponse> missingDetails = missing.stream()
        .map(status -> toMissingRequirementDetail(status, requirementMetadataById))
        .toList();

    List<String> missingNames = missing.stream()
        .map(status -> status.getGraduationRequirementId() != null ? status.getGraduationRequirementId()
            .toString() : null)
        .filter(Objects::nonNull)
        .toList();

    List<FeasibilityMissingRequirementResponse> atRiskDetails = missing.stream()
        .filter(status -> status.getCompletionSemesterId() == null)
        .map(status -> toMissingRequirementDetail(status, requirementMetadataById))
        .toList();

    List<String> atRisk = atRiskDetails.stream()
        .map(FeasibilityMissingRequirementResponse::getRequirementId)
        .filter(Objects::nonNull)
        .toList();

    boolean passed = atRisk.isEmpty();

    String reason = passed ? "Tất cả yêu cầu tốt nghiệp đã hoàn thành hoặc có kế hoạch hoàn thành" : String.format(
        "%d yêu cầu tốt nghiệp chưa có kế hoạch hoàn thành", atRisk.size());

    return new GraduationRequirementCheckResult(passed, missingNames, atRisk, missingDetails, atRiskDetails, reason);
  }

  private Map<UUID, GraduationRequirementResponse> fetchRequirementMetadata(UUID studentId) {
    try {
      List<GraduationRequirementResponse> requirements = courseManagementClient.getGraduationRequirementsByStudentId(
          studentId);
      return requirements == null ? Map.of() : requirements.stream()
          .filter(requirement -> requirement.getGraduationRequirementId() != null)
          .collect(Collectors.toMap(
              GraduationRequirementResponse::getGraduationRequirementId, Function.identity(),
              (existing, replacement) -> existing));
    } catch (Exception exception) {
      log.warn("Failed to fetch graduation requirement metadata for student {}", studentId, exception);
      return Map.of();
    }
  }

  private FeasibilityMissingRequirementResponse toMissingRequirementDetail(
      GraduationRequirementStatus status,
      Map<UUID, GraduationRequirementResponse> requirementMetadataById) {
    UUID requirementId = status.getGraduationRequirementId();
    GraduationRequirementResponse metadata = requirementId != null ? requirementMetadataById.get(requirementId) : null;

    return FeasibilityMissingRequirementResponse.builder()
        .requirementId(requirementId != null ? requirementId.toString() : null)
        .requirementStatusId(
            status.getGraduationRequirementStatusId() != null ? status.getGraduationRequirementStatusId()
                .toString() : null)
        .code(metadata != null ? metadata.getCode() : null)
        .name(metadata != null ? metadata.getName() : null)
        .description(metadata != null ? metadata.getDescription() : null)
        .build();
  }
}
