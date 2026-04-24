package com.hcmut.lms.assessment.domain.entity.assessment;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@Entity
@Table(name = "assessments")
@NoArgsConstructor
@AllArgsConstructor
public class Assessment extends BaseEntity {

    @Column(name = "class_id", nullable = false)
    private UUID classId;

    @Column(name = "weight", precision = 6, scale = 2)
    private BigDecimal weight;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type")
    private AssessmentType assessmentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_status")
    private AssessmentStatus assessmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "grading_rule")
    private GradingRule gradingRule;

    @Column(name = "max_attempts")
    private int maxAttempts;

    @Column(name = "time_limit")
    private int timeLimit;

    @Column(name = "passing_score")
    private int passingScore;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "close_time")
    private Instant closeTime;

    @Column(name = "can_review")
    private boolean canReview;

    @Column(name = "show_correct_answers")
    private boolean showCorrectAnswers;

    @Column(name = "time_can_review")
    @Enumerated(EnumType.STRING)
    private TimeCanReview timeCanReview;

    @OneToMany(mappedBy = "assessment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<AssessmentQuestion> assessmentQuestions = new HashSet<>();
}
