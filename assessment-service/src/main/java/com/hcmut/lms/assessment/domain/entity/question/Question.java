package com.hcmut.lms.assessment.domain.entity.question;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.questionBank.QuestionBank;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@SuperBuilder
@Entity
@Table(name = "questions")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Question extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "difficult_level")
    private DifficultLevel difficultLevel;

    @Column(precision = 5, scale = 3)
    private BigDecimal point;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType questionType;

    @Column(columnDefinition = "text")
    private String content;

    private boolean required;

    @ManyToMany(mappedBy = "questions")
    @Builder.Default
    private Set<QuestionBank> banks = new HashSet<>();
}
