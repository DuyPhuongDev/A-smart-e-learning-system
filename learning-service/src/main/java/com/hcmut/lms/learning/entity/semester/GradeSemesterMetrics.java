package com.hcmut.lms.learning.entity.semester;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Per-semester global grade statistics for shrinkage smoothing in grade prediction.
 * Persisted to avoid recomputation and provide real data for on-demand queries.
 * Table: learning.grade_semester_metrics
 */
@Entity
@Table(
    name = "grade_semester_metrics",
    schema = "learning",
    uniqueConstraints = @UniqueConstraint(
        name = "grade_semester_metrics_semester_unique",
        columnNames = {"semester_id"}
    ),
    indexes = {
        @Index(name = "idx_grade_semester_metrics_semkey", columnList = "sem_key")
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeSemesterMetrics extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** FK -> course_management.semesters. Unique: one row per semester. */
    @Column(name = "semester_id", nullable = false, unique = true)
    private UUID semesterId;

    /** Semester key for chronological ordering and proximity lookups */
    @Column(name = "sem_key", nullable = false)
    private Integer semKey;

    /** Median grade across all subjects in this semester (10-point scale) */
    @Column(name = "median_grade", nullable = false)
    private Double medianGrade;

    /** Mean grade across all subjects in this semester (10-point scale) */
    @Column(name = "mean_grade", nullable = false)
    private Double meanGrade;

    /** Total number of graded enrollments in this semester */
    @Column(name = "sample_count", nullable = false)
    private Integer sampleCount;
}