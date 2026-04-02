package com.hcmut.lms.assessment.domain.entity.submission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coding_submissions")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CodingSubmission extends QuestionSubmission {

    @Column(name = "input_code", columnDefinition = "text")
    private String inputCode;

    @Column(name = "execution_language")
    private String executionLanguage;

    @Column(name = "passed_testcases")
    private Integer passedTestcases;

    @Column(name = "total_testcases")
    private Integer totalTestcases;
}
