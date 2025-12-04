package com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement;

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
public class GraduationRequirementCurriculumId implements Serializable {
    
    @Column(name = "graduation_requirement_id")
    private UUID graduationRequirementId;
    
    @Column(name = "curriculum_intake_year_id")
    private UUID curriculumIntakeYearId;
    
    @Column(name = "curriculum_code")
    private String curriculumCode;
    
    @Column(name = "curriculum_specialization_id")
    private UUID curriculumSpecializationId;
}

