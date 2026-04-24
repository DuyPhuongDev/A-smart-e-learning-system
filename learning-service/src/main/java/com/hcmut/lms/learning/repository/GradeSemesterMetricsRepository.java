package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.semester.GradeSemesterMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GradeSemesterMetricsRepository extends JpaRepository<GradeSemesterMetrics, UUID> {

    /**
     * Find semester metrics by semester ID (exact match).
     * Returns at most one result due to unique constraint on semester_id.
     */
    Optional<GradeSemesterMetrics> findBySemesterId(UUID semesterId);

    /**
     * Find the semester with the closest semKey to the target.
     * Used as a fallback when no exact match exists for a given semester.
     */
    @Query(value = """
        SELECT * FROM learning.grade_semester_metrics
        ORDER BY ABS(sem_key - :targetSemKey) ASC
        LIMIT 1
        """, nativeQuery = true)
    Optional<GradeSemesterMetrics> findClosestBySemKey(@Param("targetSemKey") int targetSemKey);

    /**
     * Find all metrics for a set of semester IDs.
     * Used for batch loading.
     */
    List<GradeSemesterMetrics> findBySemesterIdIn(Collection<UUID> semesterIds);

    /**
     * Batch UPSERT: Insert or update semester global metrics using PostgreSQL ON CONFLICT.
     * Updates all data columns if row exists (unique constraint: semester_id).
     */
    @Modifying
    @Query(value = """
        INSERT INTO learning.grade_semester_metrics (
            id, semester_id, sem_key,
            median_grade, mean_grade, sample_count,
            created_at, updated_at
        )
        SELECT
            gen_random_uuid(),
            unnest(cast(:semesterIds as uuid[])),
            unnest(cast(:semKeys as int[])),
            unnest(cast(:medianGrades as float8[])),
            unnest(cast(:meanGrades as float8[])),
            unnest(cast(:sampleCounts as int[])),
            NOW(), NOW()
        ON CONFLICT (semester_id)
        DO UPDATE SET
            sem_key = EXCLUDED.sem_key,
            median_grade = EXCLUDED.median_grade,
            mean_grade = EXCLUDED.mean_grade,
            sample_count = EXCLUDED.sample_count,
            updated_at = NOW()
        """, nativeQuery = true)
    void batchUpsert(
            @Param("semesterIds") UUID[] semesterIds,
            @Param("semKeys") Integer[] semKeys,
            @Param("medianGrades") Double[] medianGrades,
            @Param("meanGrades") Double[] meanGrades,
            @Param("sampleCounts") Integer[] sampleCounts);
}