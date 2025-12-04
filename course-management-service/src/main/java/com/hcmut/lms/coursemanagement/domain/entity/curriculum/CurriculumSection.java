package com.hcmut.lms.coursemanagement.domain.entity.curriculum;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "curriculum_sections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CurriculumSection extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "notes")
    private String notes;
    
    @Column(name = "required_credits")
    private Integer requiredCredits;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "description")
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "curriculum_code", referencedColumnName = "code"),
        @JoinColumn(name = "curriculum_specialization_id", referencedColumnName = "specialization_id"),
        @JoinColumn(name = "curriculum_intake_year_id", referencedColumnName = "intake_year_id")
    })
    private Curriculum curriculum;
    
    @OneToMany(mappedBy = "curriculumSection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CurriculumSubject> curriculumSubjects = new ArrayList<>();
}

