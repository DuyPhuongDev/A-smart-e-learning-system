package com.hcmut.lms.coursemanagement.domain.entity.subject;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSectionGrading;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "gradings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Grading extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "grading_type")
    @Enumerated(EnumType.STRING)
    private GradingType gradingType;
    
    @OneToMany(mappedBy = "grading", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubjectGrading> subjectGradings = new ArrayList<>();
    
    @OneToMany(mappedBy = "grading", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ClassSectionGrading> classSectionGradings = new ArrayList<>();
}

