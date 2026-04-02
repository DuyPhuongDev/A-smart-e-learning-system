package com.hcmut.lms.usermanagement.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

  @Id
  @Column(name = "user_id")
  private UUID userId;

  @OneToOne(fetch = FetchType.LAZY)
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "student_code", unique = true, nullable = false, length = 255)
  private String studentCode;

  @Column(name = "intake_year_id", nullable = false)
  private UUID intakeYearId;

  @Column(name = "department_id", nullable = false)
  private UUID departmentId;
}

