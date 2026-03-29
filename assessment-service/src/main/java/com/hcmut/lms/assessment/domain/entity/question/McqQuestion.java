package com.hcmut.lms.assessment.domain.entity.question;

import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mcq_questions")
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class McqQuestion extends Question {
    private boolean allowMultiAnswer;
    private boolean shuffleOption;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @Builder.Default
    private List<AnswerOption> answerOptions = new ArrayList<>();

    public void addOption(AnswerOption option) {
        answerOptions.add(option);
        option.setQuestion(this);
    }
}
