package com.hcmut.lms.coursemanagement.domain.entity.curriculum;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectParallel;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectRecommendation;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "curriculum_subjects")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CurriculumSubject extends BaseEntity {
    
    @EmbeddedId
    private CurriculumSubjectId id;
    
    @Column(name = "name")
    private String name;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("curriculumSectionId")
    @JoinColumn(name = "curriculum_section_id", nullable = false)
    private CurriculumSection curriculumSection;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @Column(name = "display_order")
    private Integer displayOrder;
    
    @Column(name = "is_required")
    private Boolean isRequired;
    
    @Column(name = "category_name")
    private String categoryName;
    
    @OneToMany(mappedBy = "curriculumSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubjectPrerequisite> prerequisites = new ArrayList<>();
    
    @OneToMany(mappedBy = "curriculumSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubjectParallel> parallels = new ArrayList<>();
    
    @OneToMany(mappedBy = "curriculumSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubjectRecommendation> recommendations = new ArrayList<>();
}

