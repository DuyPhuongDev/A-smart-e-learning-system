package com.hcmut.lms.personalization.domain.entity.learningGoal;

import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "preferred_summer_semesters", schema = "personalization")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PreferredSummerSemester {

  @Id
  @Column(name = "preferred_summer_semester_id", nullable = false)
  private UUID preferredSummerSemesterId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "learning_goal_id", nullable = false)
  private LearningGoal learningGoal;

  @Column(name = "semester_id")
  private UUID semesterId;

  @Enumerated(EnumType.STRING)
  @Column(name = "learning_intensity", length = 20)
  private SummerLearningIntensity learningIntensity;

}
