package com.hcmut.lms.personalization.application.service.impl.validation;

import com.hcmut.lms.personalization.application.service.impl.validation.model.GpaCheckResult;
import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpaRequirementValidatorService {

  private static final BigDecimal MAX_GPA = new BigDecimal("4.00");

  public GpaCheckResult validate(LearningGoal goal, BigDecimal currentGpa, int earnedCredits, int remainingCredits) {
    log.info("Validating GPA requirement for goal {}", goal.getLearningGoalId());

    if (goal.getTargetGpa() == null) {
      throw new IllegalArgumentException("Target GPA is required for goal validation");
    }
    BigDecimal targetGpa = goal.getTargetGpa();

    if (currentGpa == null) {
      log.info("Student has no current GPA (no graded courses yet), computing GPA requirement from zero baseline");
      BigDecimal requiredGpa;
      boolean passed;
      String reason;

      if (remainingCredits == 0) {
        passed = targetGpa.compareTo(MAX_GPA) <= 0;
        requiredGpa = targetGpa;
        reason = passed
            ? String.format("Không còn tín chỉ. GPA mục tiêu %.2f nằm trong giới hạn 4.00", targetGpa)
            : String.format("GPA mục tiêu %.2f vượt quá mức tối đa 4.00. Hãy điều chỉnh mục tiêu GPA xuống.", targetGpa);
      } else {
        int totalCredits = earnedCredits + remainingCredits;
        requiredGpa = targetGpa.multiply(BigDecimal.valueOf(totalCredits))
            .divide(BigDecimal.valueOf(remainingCredits), 2, RoundingMode.HALF_UP);
        passed = requiredGpa.compareTo(MAX_GPA) <= 0;
        reason = passed
            ? String.format("Chưa có GPA hiện tại. GPA trung bình yêu cầu %.2f cho %d tín chỉ còn lại là khả thi (≤ 4.00)",
                requiredGpa, remainingCredits)
            : String.format("Chưa có GPA hiện tại. GPA trung bình yêu cầu %.2f cho các học phần còn lại vượt quá 4.00. Hãy điều chỉnh mục tiêu GPA xuống.",
                requiredGpa);
      }

      log.info("GPA check (null currentGpa): passed={}, required={}, target={}", passed, requiredGpa, targetGpa);
      return new GpaCheckResult(passed, requiredGpa, targetGpa, null, remainingCredits, reason);
    }

    if (remainingCredits == 0) {
      boolean passed = currentGpa.compareTo(targetGpa) >= 0;
      String reason = passed ? String.format(
          "Không còn tín chỉ. GPA hiện tại %.2f đạt GPA mục tiêu %.2f", currentGpa,
          targetGpa) : String.format(
          "Không còn tín chỉ. GPA hiện tại %.2f thấp hơn GPA mục tiêu %.2f", currentGpa,
          targetGpa);

      return new GpaCheckResult(passed, currentGpa, targetGpa, currentGpa, 0, reason);
    }

    int totalCredits = earnedCredits + remainingCredits;

    BigDecimal requiredGpa = targetGpa.multiply(BigDecimal.valueOf(totalCredits))
        .subtract(currentGpa.multiply(BigDecimal.valueOf(earnedCredits)))
        .divide(BigDecimal.valueOf(remainingCredits), 2, RoundingMode.HALF_UP);

    boolean passed = requiredGpa.compareTo(MAX_GPA) <= 0;

    String reason = passed ? String.format(
        "GPA trung bình yêu cầu %.2f cho %d tín chỉ còn lại là khả thi (≤ 4.00)",
        requiredGpa, remainingCredits) : String.format(
        "GPA trung bình yêu cầu %.2f cho các học phần còn lại vượt quá 4.00. Hãy điều chỉnh mục tiêu GPA xuống.", requiredGpa);

    log.info("GPA check: passed={}, required={}, target={}, current={}", passed, requiredGpa, targetGpa, currentGpa);

    return new GpaCheckResult(passed, requiredGpa, targetGpa, currentGpa, remainingCredits, reason);
  }
}
