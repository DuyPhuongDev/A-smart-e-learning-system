package com.hcmut.lms.assessment.domain.entity.submission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Table(name = "essay_submissions")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EssaySubmission extends QuestionSubmission {

    @Column(name = "answer_text", columnDefinition = "text")
    private String answerText;

    @Column(name = "submission_files", columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String submissionFiles;

}
