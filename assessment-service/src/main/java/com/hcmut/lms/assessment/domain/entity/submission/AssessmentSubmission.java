package com.hcmut.lms.assessment.domain.entity.submission;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "assessment_submissions")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AssessmentSubmission extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assessment_id", nullable = false)
    private Assessment assessment;

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "attempt_no", nullable = false)
    private Integer attemptNo;

    @Column(name = "submit_time")
    private Instant submitTime;

    @Column(precision = 8, scale = 3)
    private BigDecimal score;

    @Column(precision = 5, scale = 3)
    private BigDecimal actualScore;

    @Column(name = "taken_time")
    private Integer takenTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssessmentSubmissionStatus status;

    @OneToMany(mappedBy = "assessmentSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuestionSubmission> questionSubmissions = new ArrayList<>();

    public void addQuestionSubmission(QuestionSubmission questionSubmission) {
        questionSubmissions.add(questionSubmission);
        questionSubmission.setAssessmentSubmission(this);
    }
}
