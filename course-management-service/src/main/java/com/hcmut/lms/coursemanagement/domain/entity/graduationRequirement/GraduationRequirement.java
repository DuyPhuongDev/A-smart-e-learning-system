package com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "graduation_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraduationRequirement extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "graduation_requirement_id")
  private UUID graduationRequirementId;

  @Column(name = "department_id", nullable = false)
  private UUID departmentId;

  @Column(name = "intake_year_id", nullable = false)
  private UUID intakeYearId;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "code")
  private String code;

  @Column(name = "threshold_value", nullable = false)
  private BigDecimal thresholdValue;

  @Column(name = "unit", nullable = false)
  private String unit;

  @Column(name = "evaluation_rule", nullable = false)
  private String evaluationRule;

  @Column(name = "is_active")
  private Boolean isActive;

}

