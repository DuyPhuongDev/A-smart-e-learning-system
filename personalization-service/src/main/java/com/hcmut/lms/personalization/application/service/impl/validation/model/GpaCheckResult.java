package com.hcmut.lms.personalization.application.service.impl.validation.model;

import java.math.BigDecimal;

/**
 * Result of the GPA requirement check (Step 1, check 2).
 *
 * @param passed         whether the required GPA for remaining courses is achievable (≤ 4.0)
 * @param requiredGpa    the average GPA needed across all remaining courses to reach target
 * @param targetGpa      the student's target GPA
 * @param currentGpa     the student's current GPA
 * @param remainingCredits number of credits still needed
 * @param reason         human-readable explanation
 */
public record GpaCheckResult(
    boolean passed,
    BigDecimal requiredGpa,
    BigDecimal targetGpa,
    BigDecimal currentGpa,
    int remainingCredits,
    String reason
) {}
