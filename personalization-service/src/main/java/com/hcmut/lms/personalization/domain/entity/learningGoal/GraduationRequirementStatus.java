package com.hcmut.lms.personalization.domain.entity.learningGoal;

import com.hcmut.lms.personalization.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "graduation_requirement_status", schema = "personalization")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GraduationRequirementStatus extends BaseEntity {

  @Id
  @Column(name = "graduation_requirement_status_id", nullable = false)
  private UUID graduationRequirementStatusId;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "graduation_requirement_id")
  private UUID graduationRequirementId;

  @Column(name = "is_completed", nullable = false)
  private Boolean isCompleted;

  @Column(name = "completion_semester_id")
  private UUID completionSemesterId;
}
