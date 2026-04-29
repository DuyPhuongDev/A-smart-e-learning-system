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

    /** Find all dataset rows for a specific version (for CSV export). */
    List<GradePredictionDataset> findByVersionId(UUID versionId);


    /**
     * Native UPSERT: Insert or update dataset rows using PostgreSQL ON CONFLICT.
     * Updates all columns if row exists (unique constraint: student_id, semester_id, subject_id, version_id).
     */
    @Modifying
    @Query(value = """
        INSERT INTO learning.grade_prediction_features_dataset (
            id, student_id, semester_id, subject_id, version_id,
            course_grade, sem_credits, sem_credits_squared, retake_no,
            num_semesters_prior, cumulative_grade_avg, previous_sem_grade_avg,
            course_hist_median_smooth, relative_avg_course_grade,
            created_at, updated_at
        ) VALUES (
            gen_random_uuid(), :#{#entity.studentId}, :#{#entity.semesterId}, :#{#entity.subjectId}, :versionId,
            :#{#entity.courseGrade}, :#{#entity.semCredits}, :#{#entity.semCreditsSquared}, :#{#entity.retakeNo},
            :#{#entity.numSemestersPrior}, :#{#entity.cumulativeGradeAvg}, :#{#entity.previousSemGradeAvg},
            :#{#entity.subjectHistMedianSmooth}, :#{#entity.relativeAvgCourseGrade},
            NOW(), NOW()
        )
        ON CONFLICT (student_id, semester_id, subject_id, version_id)
        DO UPDATE SET
            course_grade = EXCLUDED.course_grade,
            sem_credits = EXCLUDED.sem_credits,
            sem_credits_squared = EXCLUDED.sem_credits_squared,
            retake_no = EXCLUDED.retake_no,
            num_semesters_prior = EXCLUDED.num_semesters_prior,
            cumulative_grade_avg = EXCLUDED.cumulative_grade_avg,
            previous_sem_grade_avg = EXCLUDED.previous_sem_grade_avg,
            course_hist_median_smooth = EXCLUDED.course_hist_median_smooth,
            relative_avg_course_grade = EXCLUDED.relative_avg_course_grade,
            updated_at = NOW()
        """, nativeQuery = true)
    void upsert(@Param("entity") GradePredictionDataset entity, @Param("versionId") UUID versionId);

    /**
     * TRUE Batch UPSERT: Multi-row insert using PostgreSQL unnest for high-performance batching.
     * Reduces network round trips from O(N) to O(N/BATCH_SIZE).
     * Uses gen_random_uuid() on conflict for existing rows (keeps existing id).
     */
    @Modifying
    @Query(value = """
        INSERT INTO learning.grade_prediction_features_dataset (
            id, student_id, semester_id, subject_id, version_id,
            course_grade, sem_credits, sem_credits_squared, retake_no,
            num_semesters_prior, cumulative_grade_avg, previous_sem_grade_avg,
            course_hist_median_smooth, relative_avg_course_grade,
            created_at, updated_at
        )
        SELECT
            gen_random_uuid(),
            unnest(cast(:studentIds as uuid[])),
            unnest(cast(:semesterIds as uuid[])),
            unnest(cast(:subjectIds as uuid[])),
            :versionId,
            unnest(cast(:courseGrades as float8[])),
            unnest(cast(:semCredits as int[])),
            unnest(cast(:semCreditsSq as int[])),
            unnest(cast(:retakeNos as int[])),
            unnest(cast(:numSems as int[])),
            unnest(cast(:cumGrades as float8[])),
            unnest(cast(:prevSemGrades as float8[])),
            unnest(cast(:histMedians as float8[])),
            unnest(cast(:relAvgs as float8[])),
            NOW(), NOW()
        ON CONFLICT (student_id, semester_id, subject_id, version_id)
        DO UPDATE SET
            course_grade = EXCLUDED.course_grade,
            sem_credits = EXCLUDED.sem_credits,
            sem_credits_squared = EXCLUDED.sem_credits_squared,
            retake_no = EXCLUDED.retake_no,
            num_semesters_prior = EXCLUDED.num_semesters_prior,
            cumulative_grade_avg = EXCLUDED.cumulative_grade_avg,
            previous_sem_grade_avg = EXCLUDED.previous_sem_grade_avg,
            course_hist_median_smooth = EXCLUDED.course_hist_median_smooth,
            relative_avg_course_grade = EXCLUDED.relative_avg_course_grade,
            updated_at = NOW()
        """, nativeQuery = true)
    void batchUpsert(
            @Param("versionId") UUID versionId,
            @Param("studentIds") String[] studentIds,
            @Param("semesterIds") String[] semesterIds,
            @Param("subjectIds") String[] subjectIds,
            @Param("courseGrades") Double[] courseGrades,
            @Param("semCredits") Integer[] semCredits,
            @Param("semCreditsSq") Integer[] semCreditsSq,
            @Param("retakeNos") Integer[] retakeNos,
            @Param("numSems") Integer[] numSems,
            @Param("cumGrades") Double[] cumGrades,
            @Param("prevSemGrades") Double[] prevSemGrades,
            @Param("histMedians") Double[] histMedians,
            @Param("relAvgs") Double[] relAvgs);
}
