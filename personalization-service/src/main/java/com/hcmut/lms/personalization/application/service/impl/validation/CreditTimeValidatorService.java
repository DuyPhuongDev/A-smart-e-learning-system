package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditTimeValidatorService {

  private final SemesterCalculationService semesterCalculationService;

  public CreditTimeCheckResult validate(LearningGoal goal, int remainingCredits) {
    SemesterCalculationService.SemesterAvailability availability =
        semesterCalculationService.calculateAvailableSemesters(
        goal.getStudentId(), goal.getExpectedCompletedSemester());
    return validate(goal, remainingCredits, availability);
  }

  public CreditTimeCheckResult validate(LearningGoal goal, int remainingCredits, SemesterCalculationService.SemesterAvailability availability) {
    log.info("Validating credit/time feasibility for goal {}", goal.getLearningGoalId());

    if (goal.getPrefMainSemLearnIntensity() == null) {
      throw new IllegalArgumentException(
          "prefMainSemLearnIntensity is required for credit/time validation but was null");
    }
    if (goal.getPlannedSummerSemCount() == null) {
      throw new IllegalArgumentException(
          "plannedSummerSemCount is required for credit/time validation but was null");
    }

    int availableMainSemesters = availability.availableMainSemesters();
    int availableSummerSemesters = availability.availableSummerSemesters();

    int plannedSummerSemesters = goal.getPlannedSummerSemCount();
    int usableSummerSemesters = Math.min(plannedSummerSemesters, availableSummerSemesters);

    int maxCreditsPerMainSem = IntensityCreditCapSupport.mainSemesterCapStrict(goal.getPrefMainSemLearnIntensity());
    int maxCreditsFromSummer = calculateSummerCapacity(goal, usableSummerSemesters);

    int maxCreditsFromMain = availableMainSemesters * maxCreditsPerMainSem;
    int totalMaxCredits = maxCreditsFromMain + maxCreditsFromSummer;

    boolean passed = remainingCredits <= totalMaxCredits;

    String reason = passed ? String.format(
        "Số tín chỉ còn lại %d có thể hoàn thành trong %d học kỳ chính và %d học kỳ hè theo kế hoạch tốt nghiệp",
        remainingCredits, availableMainSemesters, usableSummerSemesters) : String.format(
        "Số tín chỉ còn lại (%d) vượt quá khả năng hoàn thành trong thời gian còn lại. Hãy giảm mục tiêu tín chỉ hoặc tăng cường độ học.",
        remainingCredits);

    log.info(
        "Credit/time check: passed={}, remaining={}, max={}, availableMain={}, availableSummer={}, usableSummer={}",
        passed, remainingCredits, totalMaxCredits, availableMainSemesters, availableSummerSemesters,
        usableSummerSemesters);

    return new CreditTimeCheckResult(
        passed, remainingCredits, totalMaxCredits, availableMainSemesters,
        usableSummerSemesters, reason);
  }

  private int calculateSummerCapacity(LearningGoal goal, int usableSummerSemesters) {
    if (usableSummerSemesters <= 0) {
      return 0;
    }

    List<PreferredSummerSemester> preferredSummerSemesters = goal.getPreferredSummerSemesters();

    if (preferredSummerSemesters.isEmpty()) {
      throw new IllegalArgumentException(
          "plannedSummerSemCount > 0 but no PreferredSummerSemester entries found. "
              + "Each planned summer semester must have a preferred intensity.");
    }

    if (preferredSummerSemesters.size() < usableSummerSemesters) {
      throw new IllegalArgumentException(
          "plannedSummerSemCount requires " + usableSummerSemesters
              + " summer semester(s) but only " + preferredSummerSemesters.size()
              + " PreferredSummerSemester entries exist.");
    }

    List<PreferredSummerSemester> sorted = preferredSummerSemesters.stream()
        .sorted(Comparator.comparing(
            PreferredSummerSemester::getSemesterId,
            Comparator.nullsLast(java.util.UUID::compareTo)))
        .toList();

    int total = 0;
    for (int i = 0; i < usableSummerSemesters; i++) {
      PreferredSummerSemester preferred = sorted.get(i);
      SummerLearningIntensity intensity = preferred.getLearningIntensity();
      total += IntensityCreditCapSupport.summerSemesterCapStrict(intensity);
    }

    return total;
  }
}
