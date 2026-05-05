package com.hcmut.lms.personalization.application.service.impl.learning_path;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SemesterSlot;
import com.hcmut.lms.personalization.application.service.impl.LearningPathSchedulingService.SubjectCandidate;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.support.SemesterClassifier;
import com.hcmut.lms.personalization.application.service.impl.support.SubjectCreditUtil;
import com.hcmut.lms.personalization.application.service.impl.validation.CreditTimeValidatorService;
import com.hcmut.lms.personalization.application.service.impl.validation.PrerequisiteChainValidatorService;
import com.hcmut.lms.personalization.application.service.impl.validation.SemesterCalculationService;
import com.hcmut.lms.personalization.application.service.impl.validation.StudentProgressDataService;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.application.service.impl.validation.model.PrerequisiteChainResult;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import com.hcmut.lms.personalization.repository.PreferredSummerSemesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
      LearningGoal learningGoal,
      StudentLearningProgressResponse preFetchedProgress) {
    log.info("Generating learning path for studentId={}, curriculumCode={}", studentId, curriculumCode);

    CurriculumFullResponse curriculum = courseManagementClient.getCurriculumFull(curriculumCode);
    UUID specializationId = learningGoal.getSpecializationId() != null ? UUID.fromString(learningGoal.getSpecializationId()) : null;
    StudentProgressDataService.StudentProgressData progressData = studentProgressDataService.getStudentProgressData(
        studentId, specializationId, preFetchedProgress);

    LearningPathBaselineSelectionService.BaselineSelectionResult baselineSelection =
        learningPathBaselineSelectionService.buildBaselinePath(
        curriculum, learningGoal, progressData);
    List<SubjectCandidate> candidates = baselineSelection.candidates();

    SemesterCalculationService.SemesterAvailability availability =
        semesterCalculationService.calculateAvailableSemesters(
        studentId, learningGoal.getExpectedCompletedSemester());

    // Reuse sorted semesters from calculation service to avoid duplicate API call.
    List<SemesterResponse> remainingSemesters = availability.sortedRemainingSemesters();

    int mainCreditCap = IntensityCreditCapSupport.mainSemesterCapStrict(learningGoal.getPrefMainSemLearnIntensity());
    Map<UUID, Integer> preferredSummerCapBySemesterId = resolvePreferredSummerCaps(learningGoal);

    validateLearningGoalForScheduling(learningGoal, preferredSummerCapBySemesterId, remainingSemesters);

    int plannedSummerSemCount = learningGoal.getPlannedSummerSemCount();

    List<SemesterSlot> schedule = learningPathSchedulingService.scheduleSubjects(
        candidates, remainingSemesters,
        mainCreditCap, preferredSummerCapBySemesterId, plannedSummerSemCount,
        new HashSet<>(progressData.completedSubjectIds()));

    // Calculate risk level BEFORE filtering empty slots so that light-intensity
    // paths with many empty semesters are not penalised by inflated averages.
    String riskLevel = calculateRiskLevel(schedule);

    // Filter out empty trailing slots created by high-intensity scheduling.
    schedule = schedule.stream().filter(slot -> slot.getSubjects() != null && !slot.getSubjects().isEmpty()).toList();

    ValidationResult validationResult = validateSchedule(learningGoal, candidates, schedule, progressData, riskLevel);

    if (!validationResult.isFeasible()) {
      log.warn("Generated path is not feasible: {}", validationResult.getReason());
      throw new IllegalStateException("Cannot generate feasible learning path: " + validationResult.getReason());
    }

    // Compute past semesters for the full academic timeline
    List<SemesterResponse> allSemesters = courseManagementClient.getAllSemesters();
    List<SemesterResponse> pastSemesters = computePastSemesters(allSemesters, progressData.studentIntakeYear());

    // Use the pre-filtered risk level instead of recalculating on trimmed schedule
    return learningPathPersistenceService.buildAndPersistLearningPath(
        studentId, learningGoalId, curriculumCode,
        schedule, riskLevel, progressData, progressData.earnedCredits(),
        pastSemesters);
  }

  /**
   * Computes the list of past semesters from the student's intake year to today.
   * Semesters are sorted by semKey for chronological order.
   * Returns empty list if studentIntakeYear is null or no past semesters exist.
   */
  private List<SemesterResponse> computePastSemesters(List<SemesterResponse> allSemesters, Integer studentIntakeYear) {
    if (studentIntakeYear == null || allSemesters == null || allSemesters.isEmpty()) {
      return List.of();
    }
    LocalDate today = LocalDate.now();
    return allSemesters.stream()
        .filter(s -> s.getSemKey() != null)
        .filter(s -> s.getSemKey() / 10 >= studentIntakeYear)
        .filter(s -> {
          if (s.getEndDate() == null) {
            return false;
          }
          try {
            LocalDate endDate = LocalDate.parse(s.getEndDate());
            return endDate.isBefore(today);
          } catch (Exception e) {
            return false;
          }
        })
        .sorted(Comparator.comparing(SemesterResponse::getSemKey, Comparator.nullsLast(Integer::compareTo)))
        .toList();
  }

  private Map<UUID, Integer> resolvePreferredSummerCaps(LearningGoal goal) {
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
      int cap = IntensityCreditCapSupport.summerSemesterCapStrict(intensity);
      caps.put(preferredSemester.getSemesterId(), cap);
    }
    return caps;
  }

  /**
   * Validates that the learning goal has all required data for scheduling.
   * Throws IllegalArgumentException if any required field is missing or inconsistent.
   */
  private void validateLearningGoalForScheduling(
      LearningGoal goal, Map<UUID, Integer> preferredSummerCapBySemesterId,
      List<SemesterResponse> remainingSemesters) {

    if (goal.getPrefMainSemLearnIntensity() == null) {
      throw new IllegalArgumentException(
          "prefMainSemLearnIntensity is required for learning path generation but was null");
    }
    if (goal.getPlannedSummerSemCount() == null) {
      throw new IllegalArgumentException("plannedSummerSemCount is required for learning path generation but was null");
    }

    int plannedCount = goal.getPlannedSummerSemCount();

    if (plannedCount > 0) {
      if (preferredSummerCapBySemesterId.size() != plannedCount) {
        throw new IllegalArgumentException(
            "plannedSummerSemCount is " + plannedCount + " but " + preferredSummerCapBySemesterId.size() + " " +
                "preferred summer semester entries exist. Each planned summer semester must have a preferred " +
                "intensity.");
      }

      // Verify each preferred summer semester references a remaining summer semester
      Set<UUID> remainingSummerIds = remainingSemesters.stream()
          .filter(SemesterClassifier::isSummerSemester)
          .map(SemesterResponse::getId)
          .collect(Collectors.toSet());

      for (UUID semesterId : preferredSummerCapBySemesterId.keySet()) {
        if (!remainingSummerIds.contains(semesterId)) {
          throw new IllegalArgumentException(
              "Preferred summer semester " + semesterId + " does not correspond to a remaining summer semester");
        }
      }

      if (remainingSummerIds.size() < plannedCount) {
        throw new IllegalArgumentException(
            "plannedSummerSemCount is " + plannedCount + " but only " + remainingSummerIds.size() + " summer " +
                "semesters are available");
      }
    } else {
      if (!preferredSummerCapBySemesterId.isEmpty()) {
        throw new IllegalArgumentException(
            "plannedSummerSemCount is 0 but " + preferredSummerCapBySemesterId.size() + " preferred summer semester " +
                "entries exist. Remove them or set plannedSummerSemCount > 0.");
      }
    }
  }

  private ValidationResult validateSchedule(
      LearningGoal goal, List<SubjectCandidate> candidates, List<SemesterSlot> schedule,
      StudentProgressDataService.StudentProgressData progressData, String riskLevel) {

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
    // Use main semester count for chain validation since summer semesters
    // have lower credit caps and may not offer the required prerequisite subjects.
    long mainSemesters = schedule.stream().filter(s -> !Boolean.TRUE.equals(s.getIsSummer())).count();

    PrerequisiteChainResult chainResult = prerequisiteChainValidatorService.validate(
        goal,
        progressData.completedSubjectIds(), remainingSubjectIds, (int) mainSemesters);

    if (!chainResult.passed()) {
      return new ValidationResult(false, chainResult.reason(), "high");
    }

    int remainingCredits = schedule.stream()
        .flatMap(slot -> slot.getSubjects().stream())
        .mapToInt(SubjectCreditUtil::safeCredits)
        .sum();

    CreditTimeCheckResult creditTimeResult = creditTimeValidatorService.validate(
        goal, remainingCredits);

    if (!creditTimeResult.passed()) {
      return new ValidationResult(false, creditTimeResult.reason(), "high");
    }

    return new ValidationResult(true, "Lộ trình khả thi", riskLevel);
  }

  private String calculateRiskLevel(List<SemesterSlot> schedule) {
    // Only use main (non-summer) semesters for average credit calculation
    // to avoid summer semesters with few credits diluting the average.
    double avgMainCredits = schedule.stream()
        .filter(s -> !Boolean.TRUE.equals(s.getIsSummer()))
        .mapToInt(SemesterSlot::getTotalCredits)
        .average()
        .orElse(0.0);

    // Count overloaded semesters, using Heavy cap as the overload threshold
    int summerOverloadThreshold = IntensityCreditCapSupport.summerSemesterCapStrict(SummerLearningIntensity.Heavy);
    int mainOverloadThreshold = IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Heavy);
    int overloadedSemesters = (int) schedule.stream().filter(s -> {
      int threshold = Boolean.TRUE.equals(s.getIsSummer()) ? summerOverloadThreshold : mainOverloadThreshold;
      return s.getTotalCredits() > threshold;
    }).count();

    int standardCap = IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Standard);
    int heavyCap = IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Heavy);
    if (overloadedSemesters > 2 || avgMainCredits > (standardCap + heavyCap) / 2.0) {
      return "high";
    }
    if (overloadedSemesters > 0 || avgMainCredits > standardCap) {
      return "medium";
    }
    return "low";
  }

  private record ValidationResult(boolean isFeasible, String reason, String riskLevel) {
    public String getReason() {
      return reason;
    }
  }
}
