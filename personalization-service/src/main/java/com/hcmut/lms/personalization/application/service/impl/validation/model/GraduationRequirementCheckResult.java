package com.hcmut.lms.personalization.application.service.impl.validation.model;

import com.hcmut.lms.personalization.application.dto.response.FeasibilityMissingRequirementResponse;

import java.util.List;

/**
 * Result of the graduation requirement check (Step 1, check 4).
 *
 * @param passed                    whether all graduation requirements are on track
 * @param missingRequirements       list of requirement IDs not yet completed
 * @param atRiskRequirements        list of requirement IDs that may not be completed in time
 * @param missingRequirementDetails structured details for all missing requirements
 * @param atRiskRequirementDetails  structured details for at-risk requirements
 * @param reason                    human-readable explanation
 */
public record GraduationRequirementCheckResult(boolean passed, List<String> missingRequirements,
                                               List<String> atRiskRequirements,
                                               List<FeasibilityMissingRequirementResponse> missingRequirementDetails,
                                               List<FeasibilityMissingRequirementResponse> atRiskRequirementDetails,
                                               String reason) {
}
