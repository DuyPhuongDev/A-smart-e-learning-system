package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.subject.SubjectSemesterMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubjectSemesterMetricsRepository extends JpaRepository<SubjectSemesterMetrics, UUID> {

    /**
     * Find metrics for a specific subject and semester.
     */
    Optional<SubjectSemesterMetrics> findBySubjectIdAndSemesterId(UUID subjectId, UUID semesterId);

    /**
     * Find all metrics for a subject, ordered by creation time.
     */
    List<SubjectSemesterMetrics> findBySubjectIdOrderByCreatedAtAsc(UUID subjectId);

    /**
     * Find all metrics for given (subjectId, semesterId) pairs.
     * Used for batch loading to avoid N+1 queries.
     */
    @Query("""
        SELECT m FROM SubjectSemesterMetrics m
        WHERE m.subjectId IN :subjectIds AND m.semesterId IN :semesterIds
        """)
    List<SubjectSemesterMetrics> findBySubjectIdsAndSemesterIds(
            @Param("subjectIds") List<UUID> subjectIds,
            @Param("semesterIds") List<UUID> semesterIds);

    /**
     * Batch UPSERT: Insert or update subject semester metrics using PostgreSQL ON CONFLICT.
     * Updates all data columns if row exists (unique constraint: subject_id, semester_id).
     * Uses gen_random_uuid() for new rows; on conflict, keeps the existing id and updates all fields.
     */
    @Modifying
    @Query(value = """
        INSERT INTO learning.subject_semester_metrics (
            id, subject_id, semester_id, sem_key,
            mean_grade, std_dev, sample_count, is_fallback, fallback_level,
            semester_median_grade, semester_mean_grade, semester_sample_count,
            smoothed_median_4pt, created_at, updated_at
        )
        SELECT
            gen_random_uuid(),
            unnest(cast(:subjectIds as uuid[])),
            unnest(cast(:semesterIds as uuid[])),
            unnest(cast(:semKeys as int[])),
            unnest(cast(:meanGrades as float8[])),
            unnest(cast(:stdDevs as float8[])),
            unnest(cast(:sampleCounts as int[])),
            unnest(cast(:isFallbacks as boolean[])),
            unnest(cast(:fallbackLevels as int[])),
            unnest(cast(:semesterMedianGrades as float8[])),
            unnest(cast(:semesterMeanGrades as float8[])),
            unnest(cast(:semesterSampleCounts as int[])),
            unnest(cast(:smoothedMedian4pts as float8[])),
            NOW(), NOW()
        ON CONFLICT (subject_id, semester_id)
        DO UPDATE SET
            sem_key = EXCLUDED.sem_key,
            mean_grade = EXCLUDED.mean_grade,
            std_dev = EXCLUDED.std_dev,
            sample_count = EXCLUDED.sample_count,
            is_fallback = EXCLUDED.is_fallback,
            fallback_level = EXCLUDED.fallback_level,
            semester_median_grade = EXCLUDED.semester_median_grade,
            semester_mean_grade = EXCLUDED.semester_mean_grade,
            semester_sample_count = EXCLUDED.semester_sample_count,
            smoothed_median_4pt = EXCLUDED.smoothed_median_4pt,
            updated_at = NOW()
        """, nativeQuery = true)
    void batchUpsert(
            @Param("subjectIds") UUID[] subjectIds,
            @Param("semesterIds") UUID[] semesterIds,
            @Param("semKeys") Integer[] semKeys,
            @Param("meanGrades") Double[] meanGrades,
            @Param("stdDevs") Double[] stdDevs,
            @Param("sampleCounts") Integer[] sampleCounts,
            @Param("isFallbacks") Boolean[] isFallbacks,
            @Param("fallbackLevels") Integer[] fallbackLevels,
            @Param("semesterMedianGrades") Double[] semesterMedianGrades,
            @Param("semesterMeanGrades") Double[] semesterMeanGrades,
            @Param("semesterSampleCounts") Integer[] semesterSampleCounts,
            @Param("smoothedMedian4pts") Double[] smoothedMedian4pts);
}
