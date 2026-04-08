package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import com.hcmut.lms.personalization.application.service.impl.support.IntensityCreditCapSupport;
import com.hcmut.lms.personalization.application.service.impl.validation.model.CreditTimeCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditTimeValidatorService {

  private final SemesterCalculationService semesterCalculationService;

  public CreditTimeCheckResult validate(LearningGoal goal, int remainingCredits, int earnedCredits) {
    log.info("Validating credit/time feasibility for goal {}", goal.getLearningGoalId());

    SemesterCalculationService.SemesterAvailability availability =
        semesterCalculationService.calculateAvailableSemesters(
        goal.getStudentId(), goal.getExpectedCompletedSemester());

    int availableMainSemesters = availability.availableMainSemesters();
    int availableSummerSemesters = availability.availableSummerSemesters();

    int plannedSummerSemesters = goal.getPlannedSummerSemCount() != null ? goal.getPlannedSummerSemCount() : 0;
    int usableSummerSemesters = Math.min(plannedSummerSemesters, availableSummerSemesters);

    int maxCreditsPerMainSem = IntensityCreditCapSupport.mainSemesterCap(goal.getPrefMainSemLearnIntensity());
    int maxCreditsFromSummer = calculateSummerCapacity(goal, usableSummerSemesters);

    int maxCreditsFromMain = availableMainSemesters * maxCreditsPerMainSem;
    int totalMaxCredits = maxCreditsFromMain + maxCreditsFromSummer;

    boolean passed = remainingCredits <= totalMaxCredits;

    String reason = passed ? String.format(
        "Số tín chỉ còn lại %d có thể hoàn thành trong %d học kỳ chính và %d/%d học kỳ hè khả dụng theo kế hoạch tốt "
            + "nghiệp",
        remainingCredits, availableMainSemesters, usableSummerSemesters, availableSummerSemesters) : String.format(
        "Số tín chỉ còn lại %d vượt quá khả năng hoàn thành tối đa %d tín chỉ (%d từ học kỳ chính + %d từ học kỳ hè; "
            + "dùng %d/%d học kỳ hè khả dụng)",
        remainingCredits, totalMaxCredits, maxCreditsFromMain, maxCreditsFromSummer, usableSummerSemesters,
        availableSummerSemesters);

    log.info(
        "Credit/time check: passed={}, remaining={}, max={}, availableMain={}, availableSummer={}, usableSummer={}",
        passed, remainingCredits, totalMaxCredits, availableMainSemesters, availableSummerSemesters,
        usableSummerSemesters);

    return new CreditTimeCheckResult(
        passed, remainingCredits, totalMaxCredits, availableMainSemesters,
        availableSummerSemesters, reason);
  }

  private int calculateSummerCapacity(LearningGoal goal, int usableSummerSemesters) {
    if (usableSummerSemesters <= 0) {
      return 0;
    }

    java.util.List<PreferredSummerSemester> preferredSummerSemesters = goal.getPreferredSummerSemesters() != null ?
        goal.getPreferredSummerSemesters() : java.util.List.of();

    if (preferredSummerSemesters.isEmpty()) {
      return usableSummerSemesters * IntensityCreditCapSupport.defaultSummerSemesterCap();
    }

    java.util.List<PreferredSummerSemester> sorted = preferredSummerSemesters.stream()
        .sorted(java.util.Comparator.comparing(
            PreferredSummerSemester::getSemesterId,
            java.util.Comparator.nullsLast(java.util.UUID::compareTo)))
        .toList();

    int total = 0;
    int count = 0;
    for (PreferredSummerSemester preferred : sorted) {
      if (count >= usableSummerSemesters) {
        break;
      }
      SummerLearningIntensity intensity = preferred.getLearningIntensity();
      int cap = IntensityCreditCapSupport.summerSemesterCap(intensity);
      total += cap;
      count++;
    }

    if (count < usableSummerSemesters) {
      total += (usableSummerSemesters - count) * IntensityCreditCapSupport.defaultSummerSemesterCap();
    }

    return total;
  }
}
