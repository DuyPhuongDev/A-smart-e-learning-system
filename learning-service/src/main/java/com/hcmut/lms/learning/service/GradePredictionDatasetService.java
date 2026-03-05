package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;

import java.util.List;
import java.util.UUID;

/**
 * Service for computing and persisting the grade prediction feature dataset.
 */
public interface GradePredictionDatasetService {

    /**
     * Compute all feature vectors, create a new dataset version, and persist the rows.
     * Processes all enrollments that have a non-null finalGrade.
     * Returns the newly created version.
     */
    GradePredictionDatasetVersion computeAndSaveDataset();

    /**
     * Get all dataset versions ordered by version number descending.
     */
    List<GradePredictionDatasetVersion> getAllVersions();

    /**
     * Get a specific dataset version by its ID.
     */
    GradePredictionDatasetVersion getVersionById(UUID versionId);

    /**
     * Get the latest COMPLETED version, ready for model training.
     */
    GradePredictionDatasetVersion getLatestCompletedVersion();
}
