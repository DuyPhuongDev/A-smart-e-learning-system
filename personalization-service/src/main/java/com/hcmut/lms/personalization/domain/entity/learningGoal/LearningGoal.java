package com.hcmut.lms.personalization.domain.entity.learningGoal;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "learning_goals", schema = "personalization")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class LearningGoal extends BaseEntity {

  @Id
  @Column(name = "learning_goal_id", nullable = false)
  private UUID learningGoalId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "specialization_id", length = 100)
  private String specializationId;

  @Column(name = "target_gpa", precision = 2, scale = 1)
  private BigDecimal targetGpa;

  @Column(name = "expected_completed_semester")
  private UUID expectedCompletedSemester;

  @Enumerated(EnumType.STRING)
  @Column(name = "pref_main_sem_learn_intensity", length = 20)
  private LearningIntensity prefMainSemLearnIntensity;

  @Column(name = "planned_summer_sem_count")
  private Integer plannedSummerSemCount;

  @Column(name = "target_occupation_code", length = 10)
  private String targetOccupationCode;

  @Column(name = "attempt_target_gpa_order")
  private Integer attemptTargetGpaOrder;

  @Column(name = "focus_on_target_occupation")
  private Integer focusOnTargetOccupation;

  @Column(name = "completed_on_time")
  private Integer completedOnTime;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Builder.Default
  @OneToMany(mappedBy = "learningGoal", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<PreferredSummerSemester> preferredSummerSemesters = new ArrayList<>();

}
