package com.hcmut.lms.coursemanagement.domain.entity.curriculum;

import com.hcmut.lms.coursemanagement.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "curriculum_subject_priorities", schema = "course_management")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@IdClass(CurriculumSubjectPriorityId.class)
public class CurriculumSubjectPriority extends BaseEntity {

    @Id
    @Column(name = "curriculum_subject_id")
    private Integer curriculumSubjectId;

    @Id
    @Column(name = "curriculum_section_id")
    private UUID curriculumSectionId;

    @Id
    @Column(name = "subject_id")
    private UUID subjectId;

    @Column(name = "recommended_year", nullable = false)
    private Integer recommendedYear;

    @Column(name = "recommended_semester_in_year", nullable = false)
    private Integer recommendedSemesterInYear;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "curriculum_section_id", referencedColumnName = "curriculum_section_id", insertable = false, updatable = false),
        @JoinColumn(name = "subject_id", referencedColumnName = "subject_id", insertable = false, updatable = false),
        @JoinColumn(name = "curriculum_subject_id", referencedColumnName = "id", insertable = false, updatable = false)
    })
    private CurriculumSubject curriculumSubject;
}
