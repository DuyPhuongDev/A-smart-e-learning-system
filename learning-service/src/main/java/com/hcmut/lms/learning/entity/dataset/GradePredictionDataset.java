package com.hcmut.lms.learning.entity.dataset;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * Stores pre-computed feature vectors for the grade prediction model.
 * Each row represents one (student, semester, course) combination.
 * Table: learning.grade_prediction_features_dataset
 */
@Entity
@Table(
    name = "grade_prediction_features_dataset",
    uniqueConstraints = @UniqueConstraint(
        name = "grade_prediction_features_dataset_unique",
        columnNames = {"student_id", "semester_id", "course_id", "version_id"}
    )
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GradePredictionDataset extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    /** FK → course_management.semesters */
    @Column(name = "semester_id", nullable = false)
    private UUID semesterId;

    /** FK → course_management.subjects */
    @Column(name = "course_id", nullable = false)
    private UUID courseId;

    /** FK → learning.grade_prediction_dataset_version */
    @Column(name = "version_id", nullable = false)
    private UUID versionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_id", insertable = false, updatable = false)
    private GradePredictionDatasetVersion version;

    // ── Targets ─────────────────────────────────────────────────────────
    /** Final grade on 4-point scale converted from raw 10-point score */
    @Column(name = "course_grade")
    private Double courseGrade;

    /** course_grade − course_hist_median_smooth */
    @Column(name = "target_gap")
    private Double targetGap;

    // ── Enrollment context ────────────────────────────────────────────────
    @Column(name = "sem_credits", nullable = false)
    private Integer semCredits;

    @Column(name = "sem_credits_squared", nullable = false)
    private Integer semCreditsSquared;

    /** True if student has taken this course before (attempt_index > 0) */
    @Column(name = "retake_flg", nullable = false)
    private Boolean retakeFlg;

    // ── Student history flags ─────────────────────────────────────────────
    @Column(name = "num_semesters_prior", nullable = false)
    private Integer numSemestersPrior;

    @Column(name = "has_student_history", nullable = false)
    private Boolean hasStudentHistory;

    // ── Student cumulative performance ────────────────────────────────────
    @Column(name = "cumulative_grade_avg")
    private Double cumulativeGradeAvg;

    @Column(name = "previous_sem_grade_avg")
    private Double previousSemGradeAvg;

    @Column(name = "grade_trend", nullable = false)
    private Double gradeTrend;

    @Column(name = "grade_consistency")
    private Double gradeConsistency;

    @Column(name = "historic_fail_ratio", nullable = false)
    private Double historicFailRatio;

    // ── Student ranking within semester ───────────────────────────────────
    @Column(name = "sem_rank_percentile", nullable = false)
    private Double semRankPercentile;

    @Column(name = "gpa_rank_percentile", nullable = false)
    private Double gpaRankPercentile;

    // ── Student grade distribution rates ──────────────────────────────────
    @Column(name = "student_a_plus_grade_rate", nullable = false)
    private Double studentAPlusGradeRate;

    @Column(name = "student_a_grade_rate", nullable = false)
    private Double studentAGradeRate;

    @Column(name = "student_b_plus_grade_rate", nullable = false)
    private Double studentBPlusGradeRate;

    @Column(name = "student_b_grade_rate", nullable = false)
    private Double studentBGradeRate;

    @Column(name = "student_c_plus_grade_rate", nullable = false)
    private Double studentCPlusGradeRate;

    @Column(name = "student_c_grade_rate", nullable = false)
    private Double studentCGradeRate;

    @Column(name = "student_d_plus_grade_rate", nullable = false)
    private Double studentDPlusGradeRate;

    @Column(name = "student_d_grade_rate", nullable = false)
    private Double studentDGradeRate;

    // ── Course historical baseline ─────────────────────────────────────────
    /** Number of enrollments in 3-year window before this semester */
    @Column(name = "course_hist_count", nullable = false)
    private Integer courseHistCount;

    /** Shrinkage-smoothed median course grade (4-point scale) */
    @Column(name = "course_hist_median_smooth")
    private Double courseHistMedianSmooth;

    /** True when course_hist_count == 0 */
    @Column(name = "course_hist_missing", nullable = false)
    private Boolean courseHistMissing;

    // ── Course difficulty & failure ────────────────────────────────────────
    @Column(name = "rank_course_difficulty", nullable = false)
    private Double rankCourseDifficulty;

    @Column(name = "course_fail_rate", nullable = false)
    private Double courseFailRate;

    // ── Course grade distribution rates ───────────────────────────────────
    @Column(name = "course_a_plus_grade_rate", nullable = false)
    private Double courseAPlusGradeRate;

    @Column(name = "course_a_grade_rate", nullable = false)
    private Double courseAGradeRate;

    @Column(name = "course_b_plus_grade_rate", nullable = false)
    private Double courseBPlusGradeRate;

    @Column(name = "course_b_grade_rate", nullable = false)
    private Double courseBGradeRate;

    @Column(name = "course_c_plus_grade_rate", nullable = false)
    private Double courseCPlusGradeRate;

    @Column(name = "course_c_grade_rate", nullable = false)
    private Double courseCGradeRate;

    @Column(name = "course_d_plus_grade_rate", nullable = false)
    private Double courseDPlusGradeRate;

    @Column(name = "course_d_grade_rate", nullable = false)
    private Double courseDGradeRate;

    // ── Relative performance vs course peers ──────────────────────────────
    @Column(name = "has_relative_course", nullable = false)
    private Boolean hasRelativeCourse;

    @Column(name = "relative_avg_course_grade", nullable = false)
    private Double relativeAvgCourseGrade;

    @Column(name = "relative_avg_course_grade_rank_percentile", nullable = false)
    private Double relativeAvgCourseGradeRankPercentile;
}
