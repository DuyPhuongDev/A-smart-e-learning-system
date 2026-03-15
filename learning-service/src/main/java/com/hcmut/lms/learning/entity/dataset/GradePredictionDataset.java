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
    schema = "learning",
    uniqueConstraints = @UniqueConstraint(
        name = "grade_prediction_features_dataset_unique",
        columnNames = {"student_id", "semester_id", "subject_id", "version_id"}
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
    @Column(name = "subject_id", nullable = false)
    private UUID subjectId;

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

    // ── Enrollment context ────────────────────────────────────────────────
    @Column(name = "sem_credits", nullable = false)
    private Integer semCredits;

    @Column(name = "sem_credits_squared", nullable = false)
    private Integer semCreditsSquared;

    /** Attempt count for this subject (0 = first attempt) */
    @Column(name = "retake_no", nullable = false)
    private Integer retakeNo;

    // ── Student history ───────────────────────────────────────────────────
    @Column(name = "num_semesters_prior", nullable = false)
    private Integer numSemestersPrior;

    // ── Student cumulative performance ────────────────────────────────────
    @Column(name = "cumulative_grade_avg")
    private Double cumulativeGradeAvg;

    @Column(name = "previous_sem_grade_avg")
    private Double previousSemGradeAvg;

    // ── Subject historical baseline ────────────────────────────────────────
    /** Shrinkage-smoothed median subject grade (4-point scale) */
    @Column(name = "course_hist_median_smooth")
    private Double subjectHistMedianSmooth;

    // ── Relative performance vs course peers ──────────────────────────────
    @Column(name = "relative_avg_course_grade", nullable = false)
    private Double relativeAvgCourseGrade;
}
