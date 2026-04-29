package com.hcmut.lms.learning.entity.subject;

import com.hcmut.lms.learning.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Pre-computed subject metrics per semester for grade prediction model.
 * Acts as both a performance cache and historical reference.
 * Table: learning.subject_semester_metrics
 */
@Entity
@Table(name = "subject_semester_metrics", schema = "learning", uniqueConstraints = @UniqueConstraint(name =
    "subject_semester_metrics_unique", columnNames = {"subject_id", "semester_id"}), indexes = {@Index(name =
    "idx_subject_semester_metrics_subject", columnList = "subject_id"), @Index(name =
    "idx_subject_semester_metrics_semester", columnList = "semester_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectSemesterMetrics extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  /**
   * FK → course_management.subjects
   */
  @Column(name = "subject_id", nullable = false)
  private UUID subjectId;

  /**
   * FK → course_management.semesters
   */
  @Column(name = "semester_id", nullable = false)
  private UUID semesterId;

  /**
   * Semester key for chronological ordering and temporal fallback lookups.
   * Computed via SemKeyUtil: (2000 + YY) * 10 + S from semester code, e.g. "HK231" → 20231.
   */
  @Column(name = "sem_key")
  private Integer semKey;

  /**
   * Mean grade on 10-point scale (expected performance value)
   */
  @Column(name = "mean_grade", nullable = false)
  private Double meanGrade;

  /**
   * Standard deviation of grades on 4-point scale (measure of dispersion)
   */
  @Column(name = "std_dev", nullable = false)
  private Double stdDev;

  /**
   * Number of data points used in this computation
   */
  @Column(name = "sample_count", nullable = false)
  private Integer sampleCount;

  /**
   * Whether this record was computed from real data or fallback values
   */
  @Column(name = "is_fallback", nullable = false)
  private Boolean isFallback;

  /**
   * Level of fallback used:
   * 0 = real data (from actual grades in 3-year window)
   * 1 = temporal carry-forward (inherited from previous semester's metrics)
   * 2 = global subject baseline (computed from all historical data for this subject)
   * 3 = system defaults (mean=6.0, std_dev=0.7)
   */
  @Column(name = "fallback_level", nullable = false)
  private Integer fallbackLevel;

  /**
   * Global semester median (all subjects in this semester) - for shrinkage smoothing
   */
  @Column(name = "semester_median_grade", nullable = false)
  private Double semesterMedianGrade;

  /**
   * Global semester mean (all subjects in this semester)
   */
  @Column(name = "semester_mean_grade", nullable = false)
  private Double semesterMeanGrade;

  /**
   * Total samples in this semester across all subjects
   */
  @Column(name = "semester_sample_count", nullable = false)
  private Integer semesterSampleCount;

  /**
   * Pre-computed smoothed median (4-point scale) with shrinkage applied
   */
  @Column(name = "smoothed_median_4pt", nullable = false)
  private Double smoothedMedian4pt;

  public static final Double DEFAULT_MEAN_GRADE = 6.0;
  public static final Double DEFAULT_STD_DEV = 0.7;
}
