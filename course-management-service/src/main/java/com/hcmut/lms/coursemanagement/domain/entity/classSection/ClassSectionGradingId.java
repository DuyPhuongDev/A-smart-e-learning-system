package com.hcmut.lms.coursemanagement.domain.entity.classSection;

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
public class ClassSectionGradingId implements Serializable {
    
    @Column(name = "class_section_id")
    private UUID classSectionId;
    
    @Column(name = "grading_id")
    private UUID gradingId;
}

