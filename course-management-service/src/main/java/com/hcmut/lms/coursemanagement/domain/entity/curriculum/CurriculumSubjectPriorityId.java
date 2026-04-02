package com.hcmut.lms.coursemanagement.domain.entity.curriculum;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CurriculumSubjectPriorityId implements Serializable {

    private Integer curriculumSubjectId;
    private UUID curriculumSectionId;
    private UUID subjectId;
}
