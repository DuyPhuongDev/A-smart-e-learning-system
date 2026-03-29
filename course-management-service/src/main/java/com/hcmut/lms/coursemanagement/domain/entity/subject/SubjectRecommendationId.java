package com.hcmut.lms.coursemanagement.domain.entity.subject;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class SubjectRecommendationId implements Serializable {
    
    @Column(name = "curriculum_subject_id")
    private Integer curriculumSubjectId;
    
    @Column(name = "curriculum_section_id")
    private UUID curriculumSectionId;
    
    @Column(name = "subject_id")
    private UUID subjectId;
    
    @Column(name = "recommend_curriculum_subject_id")
    private Integer recommendCurriculumSubjectId;
    
    @Column(name = "recommend_curriculum_section_id")
    private UUID recommendCurriculumSectionId;
    
    @Column(name = "recommend_subject_id")
    private UUID recommendSubjectId;
}

