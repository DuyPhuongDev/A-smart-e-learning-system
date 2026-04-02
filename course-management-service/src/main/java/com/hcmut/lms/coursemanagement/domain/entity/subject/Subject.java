package com.hcmut.lms.coursemanagement.domain.entity.subject;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Subject extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "name")
  private String name;

  @Column(name = "category")
  @Enumerated(EnumType.STRING)
  private SubjectCategory category;

  @Column(name = "code")
  private String code;

  @Column(name = "description")
  private String description;

  @Column(name = "credits")
  private Integer credits;

  @Column(name = "subject_group_id")
  private UUID subjectGroupId;

  // default value is graded
  @Column(name = "grading_type")
  @Enumerated(EnumType.STRING)
  private SubjectGradingType gradingType;


  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<ClassSection> classSections = new ArrayList<>();

  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<CurriculumSubject> curriculumSubjects = new ArrayList<>();

  @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<SubjectGrading> subjectGradings = new ArrayList<>();
}

