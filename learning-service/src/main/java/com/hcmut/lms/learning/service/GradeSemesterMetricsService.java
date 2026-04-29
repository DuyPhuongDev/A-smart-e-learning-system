package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing per-semester global grade statistics.
 * Used for shrinkage smoothing in the grade prediction fallback chain.
 */
public interface GradeSemesterMetricsService {

    /**
     * Delete all existing metrics and save new ones.
     * Used during initial ETL when no data exists yet.
     * Runs within a transaction so a failure rolls back both delete and inserts.
     */
    List<GradeSemesterMetrics> replaceAll(List<GradeSemesterMetrics> metrics);

    /**
     * Upsert (insert or update) all metrics using batch ON CONFLICT.
     * Preferred over replaceAll() for idempotent updates — no data loss risk.
     */
    void upsertAll(List<GradeSemesterMetrics> metrics);

    /**
     * Find semester metrics by semester ID (exact match).
     */
    Optional<GradeSemesterMetrics> findBySemesterId(UUID semesterId);

    /**
     * Find the semester with the closest semKey to the target.
     * Used when no exact match exists for a given semester.
     */
    Optional<GradeSemesterMetrics> findClosestBySemKey(int targetSemKey);
}