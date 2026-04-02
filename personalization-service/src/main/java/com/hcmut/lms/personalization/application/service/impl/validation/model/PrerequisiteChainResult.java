package com.hcmut.lms.personalization.application.service.impl.validation.model;

/**
 * Result of the prerequisite chain check (Step 1, check 3).
 *
 * @param passed              whether the longest prerequisite chain fits within available semesters
 * @param longestChain        length of the longest sequential prerequisite chain among remaining courses
 * @param availableSemesters  total semesters available (main + summer)
 * @param reason              human-readable explanation
 */
public record PrerequisiteChainResult(
    boolean passed,
    int longestChain,
    int availableSemesters,
    String reason
) {}
