package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.dataset.GradePredictionDatasetVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradePredictionDatasetVersionRepository
        extends JpaRepository<GradePredictionDatasetVersion, UUID> {

    /** Get the latest version (highest version_number). */
    Optional<GradePredictionDatasetVersion> findTopByOrderByVersionNumberDesc();

    /** Get the latest COMPLETED version. */
    Optional<GradePredictionDatasetVersion> findTopByStatusOrderByVersionNumberDesc(
            GradePredictionDatasetVersion.DatasetVersionStatus status);

    /** List all versions ordered newest first. */
    List<GradePredictionDatasetVersion> findAllByOrderByVersionNumberDesc();

    /** Check whether a version number already exists. */
    boolean existsByVersionNumber(Integer versionNumber);

    /**
     * Compute the next version number (MAX + 1), or 1 if table is empty.
     */
    @Query("SELECT COALESCE(MAX(v.versionNumber), 0) + 1 FROM GradePredictionDatasetVersion v")
    int nextVersionNumber();
}

