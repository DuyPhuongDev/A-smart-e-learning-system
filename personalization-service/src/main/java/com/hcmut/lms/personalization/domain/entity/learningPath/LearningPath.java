package com.hcmut.lms.personalization.domain.entity.learningPath;

import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "learning_paths", schema = "personalization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LearningPath extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "learning_path_id", nullable = false)
  private UUID learningPathId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "learning_goal_id", nullable = false)
  private UUID learningGoalId;

  @Column(name = "curriculum_code", length = 20)
  private String curriculumCode;

  @Column(name = "total_credits")
  private Integer totalCredits;

  @Column(name = "estimated_duration_semesters")
  private Integer estimatedDurationSemesters;

  @Column(name = "predicted_gpa", precision = 4, scale = 2)
  private BigDecimal predictedGpa;

  @Column(name = "completion_rate", precision = 5, scale = 2)
  private BigDecimal completionRate;

  @Column(name = "risk_level", length = 20)
  private String riskLevel;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;
}
