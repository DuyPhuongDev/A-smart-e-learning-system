package com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.Curriculum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "graduation_requirements_curriculums")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GraduationRequirementCurriculum extends BaseEntity {
    
    @EmbeddedId
    private GraduationRequirementCurriculumId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("graduationRequirementId")
    @JoinColumn(name = "graduation_requirement_id")
    private GraduationRequirement graduationRequirement;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "curriculum_intake_year_id", referencedColumnName = "intake_year_id", insertable = false, updatable = false),
        @JoinColumn(name = "curriculum_code", referencedColumnName = "code", insertable = false, updatable = false),
        @JoinColumn(name = "curriculum_specialization_id", referencedColumnName = "specialization_id", insertable = false, updatable = false)
    })
    private Curriculum curriculum;
}

