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
public class SubjectGradingId implements Serializable {
    
    @Column(name = "subject_id")
    private UUID subjectId;
    
    @Column(name = "grading_id")
    private UUID gradingId;
}

