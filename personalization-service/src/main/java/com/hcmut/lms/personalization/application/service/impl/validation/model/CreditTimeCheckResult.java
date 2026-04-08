package com.hcmut.lms.personalization.application.service.impl.validation.model;

/**
 * Result of the credit/time feasibility check (Step 1, check 1).
 *
 * @param passed            whether remaining credits can be completed within the planned timeline
 * @param remainingCredits  total credits still needed to graduate
 * @param maxCredits        maximum credits achievable given the planned semesters and intensity
 * @param mainSemesters     number of main semesters remaining
 * @param summerSemesters   number of summer semesters planned
 * @param reason            human-readable explanation
 */
public record CreditTimeCheckResult(
    boolean passed,
    int remainingCredits,
    int maxCredits,
    int mainSemesters,
    int summerSemesters,
    String reason
) {}
