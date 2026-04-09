package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for computing and retrieving subject semester metrics.
 * Acts as both a performance cache for grade prediction and historical reference.
 */
public interface SubjectSemesterMetricsService {

    /**
     * Compute and save metrics for all (subject, semester) pairs found in enrollment data.
     * This is the main ETL entry point that should be called before dataset computation.
     *
     * @return Number of metrics records computed and saved
     */
    int computeAndSaveAllMetrics();

    /**
     * Compute metrics for a specific subject and semester.
     * Uses 3-year window (30 semKey units) for real data.
     * Falls back through the priority chain if no real data exists.
     *
     * @param subjectId The subject UUID
     * @param semesterId The semester UUID
     * @return Computed or fallback metrics
     */
    SubjectSemesterMetrics computeMetricsForSubjectSemester(UUID subjectId, UUID semesterId);

    /**
     * Get metrics for a specific subject and semester.
     * Returns existing record if found, otherwise computes on-the-fly.
     *
     * @param subjectId The subject UUID
     * @param semesterId The semester UUID
     * @return Metrics record (existing or newly computed)
     */
    SubjectSemesterMetrics getOrComputeMetrics(UUID subjectId, UUID semesterId);

    /**
     * Find existing metrics for a subject and semester.
     *
     * @param subjectId The subject UUID
     * @param semesterId The semester UUID
     * @return Optional containing metrics if found
     */
    Optional<SubjectSemesterMetrics> findBySubjectIdAndSemesterId(UUID subjectId, UUID semesterId);

    /**
     * Get all metrics for a specific subject.
     *
     * @param subjectId The subject UUID
     * @return List of metrics for the subject
     */
    List<SubjectSemesterMetrics> getMetricsHistoryForSubject(UUID subjectId);

    /**
     * Batch load metrics for multiple (subjectId, semesterId) pairs.
     * Returns a Map keyed by composite key "subjectId|semesterId".
     *
     * @param subjectIds List of subject UUIDs
     * @param semesterIds List of semester UUIDs
     * @return Map of composite key to metrics
     */
    java.util.Map<String, SubjectSemesterMetrics> batchLoadMetrics(
            java.util.List<UUID> subjectIds,
            java.util.List<UUID> semesterIds);

}
