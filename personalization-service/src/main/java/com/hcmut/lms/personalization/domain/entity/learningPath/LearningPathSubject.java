package com.hcmut.lms.personalization.domain.entity.learningPath;

import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "learning_path_subjects", schema = "personalization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LearningPathSubject extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "learning_path_subject_id", nullable = false)
  private UUID learningPathSubjectId;

  @Column(name = "learning_path_id", nullable = false)
  private UUID learningPathId;

  @Column(name = "learning_path_section_id", nullable = false)
  private UUID learningPathSectionId;

  @Column(name = "curriculum_subject_id")
  private UUID curriculumSubjectId;

  @Column(name = "subject_id", nullable = false)
  private UUID subjectId;

  @Column(name = "subject_code", nullable = false)
  private String subjectCode;

  @Column(name = "subject_name", nullable = false)
  private String subjectName;

  @Column(name = "credits", nullable = false)
  private Integer credits;

  @Column(name = "difficulty_level", nullable = false)
  private String difficultyLevel;

  @Column(name = "avg_pass_rate", precision = 5, scale = 2)
  private BigDecimal avgPassRate;

  @Column(name = "avg_grade", precision = 4, scale = 2)
  private BigDecimal avgGrade;

  @Column(name = "importance_score", precision = 4, scale = 2)
  private BigDecimal importanceScore;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "prerequisites_graph")
  private List<PrerequisiteNode> prerequisitesGraph;

  @Column(name = "is_completed", nullable = false)
  private Boolean isCompleted;

  @Column(name = "study_order")
  private Integer studyOrder;

  @Column(name = "completion_date")
  private LocalDateTime completionDate;

  @Column(name = "completion_grade", precision = 4, scale = 2)
  private BigDecimal completionGrade;

  @Column(name = "predicted_grade", precision = 4, scale = 2)
  private BigDecimal predictedGrade;
}
