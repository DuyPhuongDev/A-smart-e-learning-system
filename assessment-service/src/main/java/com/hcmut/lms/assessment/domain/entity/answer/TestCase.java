package com.hcmut.lms.assessment.domain.entity.answer;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "testcases")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TestCase extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coding_question_id", nullable = false)
    private CodingQuestion codingQuestion;

    @Column(name = "input", columnDefinition = "text")
    private String input;

    @Column(name = "expected", columnDefinition = "text")
    private String expected;

    @Column(name = "is_hidden")
    private boolean hidden;
}
