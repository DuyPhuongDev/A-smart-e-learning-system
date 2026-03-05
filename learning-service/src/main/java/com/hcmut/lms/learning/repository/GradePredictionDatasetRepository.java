package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.dataset.GradePredictionDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradePredictionDatasetRepository extends JpaRepository<GradePredictionDataset, UUID> {

    /**
     * Find an existing dataset row for upsert logic (within a specific version).
     */
    Optional<GradePredictionDataset> findByStudentIdAndSemesterIdAndCourseIdAndVersionId(
            UUID studentId, UUID semesterId, UUID courseId, UUID versionId);

    /**
     * Find an existing dataset row regardless of version (for migration / legacy upsert).
     */
    Optional<GradePredictionDataset> findByStudentIdAndSemesterIdAndCourseId(
            UUID studentId, UUID semesterId, UUID courseId);

    /** Count rows belonging to a specific version. */
    long countByVersionId(UUID versionId);

    /** Delete all rows belonging to a specific version (cleanup on failure). */
    @Modifying
    @Query("DELETE FROM GradePredictionDataset d WHERE d.versionId = :versionId")
    void deleteByVersionId(@Param("versionId") UUID versionId);

    /** Find all dataset rows for a specific version (for CSV export). */
    List<GradePredictionDataset> findByVersionId(UUID versionId);
}
