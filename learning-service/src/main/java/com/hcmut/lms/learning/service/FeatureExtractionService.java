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

    /**
     * Lightweight check: does this student have any graded enrollments?
     * Used to decide whether to run full feature extraction or fall back to
     * SubjectSemesterMetrics-based prediction for first-semester students.
     *
     * @param studentId Student UUID
     * @return true if the student has at least one enrollment with a final grade
     */
    boolean hasGradedHistory(UUID studentId);
}
