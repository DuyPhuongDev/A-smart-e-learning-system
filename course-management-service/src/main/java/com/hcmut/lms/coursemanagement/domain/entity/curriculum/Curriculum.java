package com.hcmut.lms.coursemanagement.domain.entity.curriculum;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curriculums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Curriculum extends BaseEntity {
    
    @EmbeddedId
    private CurriculumId id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "total_credits")
    private Integer totalCredits;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "document")
    private String document;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("specializationId")
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("intakeYearId")
    @JoinColumn(name = "intake_year_id", nullable = false)
    private AcademicYear intakeYear;
    
    @OneToMany(mappedBy = "curriculum", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CurriculumSection> curriculumSections = new ArrayList<>();
}

