package com.hcmut.lms.assessment.domain.entity.answer;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "answer_options")
public class AnswerOption extends BaseEntity {
    private String content;
    private boolean correct;
    private int orderIndex;
    private String explanation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mcq_question_id")
    private Question question;

}
