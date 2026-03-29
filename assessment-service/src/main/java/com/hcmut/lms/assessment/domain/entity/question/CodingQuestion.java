package com.hcmut.lms.assessment.domain.entity.question;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "coding_questions")
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CodingQuestion extends Question {

    @Column(name = "problem_description", columnDefinition = "text")
    private String problemDescription;

    @Column(name = "execution_time_limit")
    private int executionTimeLimit;

    @Column(name = "execution_memory_limit")
    private int executionMemoryLimit;

    @Column(name = "language")
    private String language;

    @Column(name = "initial_code", columnDefinition = "text")
    private String initialCode;

    @OneToMany(mappedBy = "codingQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TestCase> testCases = new ArrayList<>();

    public void addTestCase(TestCase testCase) {
        testCases.add(testCase);
        testCase.setCodingQuestion(this);
    }
}
