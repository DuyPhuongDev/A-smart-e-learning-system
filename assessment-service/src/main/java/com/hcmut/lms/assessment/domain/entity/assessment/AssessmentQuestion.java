package com.hcmut.lms.assessment.domain.entity.assessment;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "assessment_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssessmentQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id")
    private Assessment assessment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @Column(name = "order_index")
    private int orderIndex;

    @Column(name = "point", precision = 8, scale = 5)
    private BigDecimal point;
}
