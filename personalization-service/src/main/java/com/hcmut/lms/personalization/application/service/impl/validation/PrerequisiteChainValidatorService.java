package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.service.impl.validation.model.PrerequisiteChainResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.PrerequisiteChainRequest;
import com.hcmut.lms.personalization.client.dto.PrerequisiteChainResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrerequisiteChainValidatorService {

  private final CourseManagementClient courseManagementClient;

  public PrerequisiteChainResult validate(
      LearningGoal goal, List<UUID> completedSubjectIds,
      List<UUID> remainingSubjectIds, int totalSemestersAvailable) {
    log.info("Validating prerequisite chain for goal {}", goal.getLearningGoalId());

    try {
      PrerequisiteChainRequest request = PrerequisiteChainRequest.builder()
          .specializationId(goal.getSpecializationId())
          .completedSubjectIds(completedSubjectIds)
          .remainingSubjectIds(remainingSubjectIds)
          .build();

      PrerequisiteChainResponse response = courseManagementClient.getPrerequisiteChain(request);

      int longestChain = response.getLongestChainLength() != null ? response.getLongestChainLength() : 0;

      boolean passed = longestChain <= totalSemestersAvailable;

      String reason = passed ? String.format(
          "Chuỗi học phần tiên quyết dài nhất %d học kỳ nằm trong %d học kỳ khả dụng", longestChain,
          totalSemestersAvailable) : String.format(
          "Chuỗi học phần tiên quyết dài nhất %d học kỳ vượt quá %d học kỳ khả dụng", longestChain,
          totalSemestersAvailable);

      log.info(
          "Prerequisite chain check: passed={}, longest={}, available={}", passed, longestChain,
          totalSemestersAvailable);

      return new PrerequisiteChainResult(passed, longestChain, totalSemestersAvailable, reason);

    } catch (Exception e) {
      log.warn("Failed to fetch prerequisite chain from course-management-service", e);
      return new PrerequisiteChainResult(
          true, 0, totalSemestersAvailable,
          "Không thể xác minh chuỗi học phần tiên quyết (giả định khả thi)");
    }
  }
}
