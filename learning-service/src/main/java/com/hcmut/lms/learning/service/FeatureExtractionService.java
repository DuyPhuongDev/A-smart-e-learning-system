package com.hcmut.lms.learning.service;

import java.util.Map;
import java.util.UUID;

public interface FeatureExtractionService {
    /**
     * Extract online features for a student and target subject.
     * The current semester is automatically resolved based on the current date.
     * Works even if the student has not enrolled in a concrete class section.
     *
     * @param studentId Student UUID
     * @param subjectId Target subject UUID
     * @param plannedSemesterCredits Optional planned credits in target semester (default: 17)
     * @return Map of feature name to value
     */
    Map<String, Object> extractFeatures(UUID studentId,
                                        UUID subjectId,
                                        Integer plannedSemesterCredits);
}
