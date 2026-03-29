package com.hcmut.lms.coursemanagement.domain.entity.curriculum;

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
public class CurriculumSubjectId implements Serializable {
    
    @Column(name = "curriculum_section_id")
    private UUID curriculumSectionId;
    
    @Column(name = "subject_id")
    private UUID subjectId;
    
    @Column(name = "id")
    private Integer id;
}

