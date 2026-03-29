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
public class CurriculumId implements Serializable {
    
    @Column(name = "code")
    private String code;
    
    @Column(name = "specialization_id")
    private UUID specializationId;
    
    @Column(name = "intake_year_id")
    private UUID intakeYearId;
}

