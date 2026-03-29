package com.hcmut.lms.assessment.domain.entity.answer;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.question.EssayQuestion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "essay_accepted_file_types",
        uniqueConstraints = @UniqueConstraint(columnNames = {"essay_question_id", "file_type"}))
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EssayAcceptedFileType extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "essay_question_id", nullable = false)
    private EssayQuestion essayQuestion;

    @Column(name = "file_type")
    private String fileType;
}
