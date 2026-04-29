package com.hcmut.lms.personalization.domain.entity.learningGoal;

import com.hcmut.lms.personalization.application.dto.response.*;
import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import com.hcmut.lms.personalization.infrastructure.converter.PreliminaryChecksConverter;
import com.hcmut.lms.personalization.infrastructure.converter.RecommendationsConverter;
import com.hcmut.lms.personalization.infrastructure.converter.WarningsConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "goal_validation_results", schema = "personalization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GoalValidationResult extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "validation_id", nullable = false)
  private UUID validationId;

  @Column(name = "learning_goal_id", nullable = false)
  private UUID learningGoalId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "feasibility_level", length = 20, nullable = false)
  @Enumerated(EnumType.STRING)
  private FeasibilityLevel feasibilityLevel;

  @Column(name = "probability_score", precision = 5, scale = 4)
  private BigDecimal probabilityScore;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "metrics")
  private FeasibilityMetricsResponse metrics;

  @JdbcTypeCode(SqlTypes.JSON)
  @Convert(converter = PreliminaryChecksConverter.class)
  @Column(name = "preliminary_checks", columnDefinition = "jsonb")
  private List<ValidationCheckResponse> preliminaryChecks;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "probability_analysis")
  private ProbabilityAnalysisResponse probabilityAnalysis;

  @JdbcTypeCode(SqlTypes.JSON)
  @Convert(converter = RecommendationsConverter.class)
  @Column(name = "recommendations", columnDefinition = "jsonb")
  private List<RecommendationResponse> recommendations;

  @JdbcTypeCode(SqlTypes.JSON)
  @Convert(converter = WarningsConverter.class)
  @Column(name = "warnings", columnDefinition = "jsonb")
  private List<WarningResponse> warnings;

  @Column(name = "validation_timestamp", nullable = false)
  private Instant validationTimestamp;
}
