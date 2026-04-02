package com.hcmut.lms.personalization.domain.entity.learningPath;

import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "learning_path_sections", schema = "personalization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LearningPathSection extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "learning_path_section_id", nullable = false)
  private UUID learningPathSectionId;

  @Column(name = "learning_path_id", nullable = false)
  private UUID learningPathId;

  @Column(name = "academic_year_id", nullable = false)
  private UUID academicYearId;

  @Column(name = "academic_year_order", nullable = false)
  private Integer academicYearOrder;

  @Column(name = "semester_id", nullable = false)
  private UUID semesterId;

  @Column(name = "semester_order", nullable = false)
  private Integer semesterOrder;

  @Column(name = "total_credits")
  private Integer totalCredits;

  @Column(name = "difficulty_score", precision = 4, scale = 2)
  private BigDecimal difficultyScore;
}

