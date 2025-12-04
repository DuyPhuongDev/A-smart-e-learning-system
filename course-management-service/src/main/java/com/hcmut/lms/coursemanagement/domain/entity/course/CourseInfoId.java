package com.hcmut.lms.coursemanagement.domain.entity.course;

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
public class CourseInfoId implements Serializable {
    
    @Column(name = "course_name")
    private String courseName;
    
    @Column(name = "class_section_id")
    private UUID classSectionId;
}

