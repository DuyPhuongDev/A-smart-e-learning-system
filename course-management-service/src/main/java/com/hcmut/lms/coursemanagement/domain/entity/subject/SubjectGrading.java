package com.hcmut.lms.coursemanagement.domain.entity.subject;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "subjects_gradings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubjectGrading extends BaseEntity {
    
    @EmbeddedId
    private SubjectGradingId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("subjectId")
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gradingId")
    @JoinColumn(name = "grading_id", nullable = false)
    private Grading grading;
    
    @Column(name = "weight")
    private Float weight;
}

