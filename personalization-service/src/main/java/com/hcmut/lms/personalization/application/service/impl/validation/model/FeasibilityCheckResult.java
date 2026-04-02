package com.hcmut.lms.personalization.application.service.impl.validation.model;

/**
 * Aggregated result of all Step 1 preliminary checks.
 *
 * @param creditTimeResult          result of credit/time check
 * @param gpaCheckResult            result of GPA requirement check
 * @param prerequisiteChainResult   result of prerequisite chain check
 * @param graduationReqResult       result of graduation requirement check
 * @param overallPassed             true only if all hard-constraint checks passed
 */
public record FeasibilityCheckResult(
    CreditTimeCheckResult creditTimeResult,
    GpaCheckResult gpaCheckResult,
    PrerequisiteChainResult prerequisiteChainResult,
    GraduationRequirementCheckResult graduationReqResult,
    boolean overallPassed
) {}
