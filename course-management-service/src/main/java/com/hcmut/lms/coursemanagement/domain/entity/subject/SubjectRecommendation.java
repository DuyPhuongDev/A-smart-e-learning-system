package com.hcmut.lms.coursemanagement.domain.entity.subject;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "subject_recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubjectRecommendation extends BaseEntity {
    
    @EmbeddedId
    private SubjectRecommendationId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "curriculum_subject_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "subject_id", referencedColumnName = "subject_id", insertable = false, updatable = false),
        @JoinColumn(name = "curriculum_section_id", referencedColumnName = "curriculum_section_id", insertable = false, updatable = false)
    })
    private CurriculumSubject curriculumSubject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "recommend_curriculum_subject_id", referencedColumnName = "id", insertable = false, updatable = false),
        @JoinColumn(name = "recommend_subject_id", referencedColumnName = "subject_id", insertable = false, updatable = false),
        @JoinColumn(name = "recommend_curriculum_section_id", referencedColumnName = "curriculum_section_id", insertable = false, updatable = false)
    })
    private CurriculumSubject recommendedCurriculumSubject;
}

