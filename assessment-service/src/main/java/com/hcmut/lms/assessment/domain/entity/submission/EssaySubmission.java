package com.hcmut.lms.assessment.domain.entity.submission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @Column(name = "answer_file_url")
    private String answerFileUrl;

    @Column(name = "file_format")
    private String fileFormat;

    @Column(name = "num_pages")
    private Integer numPages;

    @Column(name = "word_count")
    private Integer wordCount;
}
