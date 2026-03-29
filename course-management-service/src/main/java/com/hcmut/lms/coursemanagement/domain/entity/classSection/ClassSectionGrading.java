package com.hcmut.lms.coursemanagement.domain.entity.classSection;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "class_sections_gradings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClassSectionGrading extends BaseEntity {
    
    @EmbeddedId
    private ClassSectionGradingId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("classSectionId")
    @JoinColumn(name = "class_section_id", nullable = false)
    private ClassSection classSection;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gradingId")
    @JoinColumn(name = "grading_id", nullable = false)
    private Grading grading;
    
    @Column(name = "weight")
    private Float weight;
}

