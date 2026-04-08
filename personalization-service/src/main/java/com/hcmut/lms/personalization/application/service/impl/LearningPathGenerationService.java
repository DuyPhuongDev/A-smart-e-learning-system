package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SemesterSlot;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.validation.CreditTimeValidatorService;
import com.hcmut.lms.personalization.application.service.impl.validation.PrerequisiteChainValidatorService;
import com.hcmut.lms.personalization.application.service.impl.validation.SemesterCalculationService;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.PrerequisiteChainResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.repository.PreferredSummerSemesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Orchestrates learning path generation by delegating baseline selection,
 * scheduling, validation, and persistence to dedicated services.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LearningPathGenerationService {

  private final CourseManagementClient courseManagementClient;
  private final StudentProgressDataService studentProgressDataService;
  private final SemesterCalculationService semesterCalculationService;
  private final LearningPathSchedulingService learningPathSchedulingService;
  private final LearningPathBaselineSelectionService learningPathBaselineSelectionService;
  private final LearningPathPersistenceService learningPathPersistenceService;
  private final PrerequisiteChainValidatorService prerequisiteChainValidatorService;
  private final CreditTimeValidatorService creditTimeValidatorService;
  private final PreferredSummerSemesterRepository preferredSummerSemesterRepository;

  @Transactional
  public LearningPath generateLearningPath(
      UUID studentId, UUID learningGoalId, String curriculumCode,
      LearningGoal learningGoal) {
    log.info("Generating learning path for studentId={}, curriculumCode={}", studentId, curriculumCode);

    CurriculumFullResponse curriculum = courseManagementClient.getCurriculumFull(curriculumCode);
    StudentProgressDataService.StudentProgressData progressData = studentProgressDataService.getStudentProgressData(
        studentId);

    LearningPathBaselineSelectionService.BaselineSelectionResult baselineSelection =
        learningPathBaselineSelectionService.buildBaselinePath(
        curriculum, learningGoal, progressData);
    List<SubjectCandidate> candidates = baselineSelection.candidates();

    SemesterCalculationService.SemesterAvailability availability =
        semesterCalculationService.calculateAvailableSemesters(
        studentId, learningGoal.getExpectedCompletedSemester());

    List<SemesterResponse> remainingSemesters = courseManagementClient.getRemainingSemesters(studentId);

    int mainCreditCap = IntensityCreditCapSupport.mainSemesterCap(learningGoal.getPrefMainSemLearnIntensity());
    Map<UUID, Integer> preferredSummerCapBySemesterId = resolvePreferredSummerCaps(learningGoal);

    List<SemesterSlot> schedule = learningPathSchedulingService.scheduleSubjects(
        candidates, availability,
        remainingSemesters, mainCreditCap, preferredSummerCapBySemesterId,
        new HashSet<>(progressData.completedSubjectIds()));

    ValidationResult validationResult = validateSchedule(learningGoal, candidates, schedule, progressData);

    if (!validationResult.isFeasible()) {
      log.warn("Generated path is not feasible: {}", validationResult.getReason());
      throw new IllegalStateException("Cannot generate feasible learning path: " + validationResult.getReason());
    }

    return learningPathPersistenceService.buildAndPersistLearningPath(
        studentId, learningGoalId, curriculumCode, schedule, validationResult.getRiskLevel(), progressData,
        baselineSelection.effectiveCompletedCredits());
  }

  private Map<UUID, Integer> resolvePreferredSummerCaps(LearningGoal goal) {
    if (goal == null || goal.getLearningGoalId() == null) {
      return Collections.emptyMap();
    }

    List<PreferredSummerSemester> preferredSummerSemesters =
        preferredSummerSemesterRepository.findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(
        goal.getLearningGoalId());
    if (preferredSummerSemesters.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<UUID, Integer> caps = new HashMap<>();
    for (PreferredSummerSemester preferredSemester : preferredSummerSemesters) {
      if (preferredSemester.getSemesterId() == null) {
        continue;
      }
      SummerLearningIntensity intensity = preferredSemester.getLearningIntensity();
      int cap = IntensityCreditCapSupport.summerSemesterCap(intensity);
      caps.put(preferredSemester.getSemesterId(), cap);
    }
    return caps;
  }

  private ValidationResult validateSchedule(
      LearningGoal goal, List<SubjectCandidate> candidates,
      List<SemesterSlot> schedule, StudentProgressDataService.StudentProgressData progressData) {

    Set<UUID> scheduledIds = schedule.stream()
        .flatMap(slot -> slot.getSubjects().stream())
        .map(SubjectCandidate::getSubjectId)
        .collect(Collectors.toSet());

    List<UUID> unscheduledIds = candidates.stream()
        .map(SubjectCandidate::getSubjectId)
        .filter(id -> !scheduledIds.contains(id))
        .toList();

    if (!unscheduledIds.isEmpty()) {
      return new ValidationResult(
          false,
          "Không thể phân bổ hết học phần vào các học kỳ còn lại: " + unscheduledIds.size() + " môn chưa được xếp",
          "high");
    }

    List<UUID> remainingSubjectIds = candidates.stream().map(SubjectCandidate::getSubjectId).toList();
    int totalSemesters = schedule.size();

    PrerequisiteChainResult chainResult = prerequisiteChainValidatorService.validate(
        goal,
        progressData.completedSubjectIds(), remainingSubjectIds, totalSemesters);

    if (!chainResult.passed()) {
      return new ValidationResult(false, chainResult.reason(), "high");
    }

    int remainingCredits = candidates.stream().mapToInt(this::safeCredits).sum();

    CreditTimeCheckResult creditTimeResult = creditTimeValidatorService.validate(
        goal, remainingCredits,
        progressData.earnedCredits());

    if (!creditTimeResult.passed()) {
      return new ValidationResult(false, creditTimeResult.reason(), "high");
    }

    return new ValidationResult(true, "Lộ trình khả thi", calculateRiskLevel(schedule));
  }

  private String calculateRiskLevel(List<SemesterSlot> schedule) {
    int overloadedSemesters = (int) schedule.stream().filter(s -> s.getTotalCredits() > 19).count();
    double avgCredits = schedule.stream().mapToInt(SemesterSlot::getTotalCredits).average().orElse(0.0);

    if (overloadedSemesters > 2 || avgCredits > 18) {
      return "high";
    }
    if (overloadedSemesters > 0 || avgCredits > 16) {
      return "medium";
    }
    return "low";
  }

  private int safeCredits(SubjectCandidate candidate) {
    Integer credits = candidate.getCredits();
    if (credits == null) {
      return 0;
    }
    if (credits < 0) {
      throw new IllegalStateException(
          "Invalid subject credits while validating schedule: subjectId="
              + candidate.getSubjectId()
              + ", subjectCode="
              + candidate.getSubjectCode()
              + ", credits="
              + credits);
    }
    return credits;
  }


  private record ValidationResult(boolean isFeasible, String reason, String riskLevel) {
    public String getRiskLevel() {
      return riskLevel;
    }

    public String getReason() {
      return reason;
    }
  }
}
