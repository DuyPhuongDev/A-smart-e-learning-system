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

    BigDecimal targetGpa = goal.getTargetGpa() != null ? goal.getTargetGpa() : new BigDecimal("2.50");

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
        "GPA trung bình yêu cầu %.2f cho %d tín chỉ còn lại vượt quá mức tối đa 4.00", requiredGpa, remainingCredits);

    log.info("GPA check: passed={}, required={}, target={}, current={}", passed, requiredGpa, targetGpa, currentGpa);

    return new GpaCheckResult(passed, requiredGpa, targetGpa, currentGpa, remainingCredits, reason);
  }
}
