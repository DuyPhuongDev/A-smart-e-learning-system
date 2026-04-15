package com.hcmut.lms.assessment.domain.entity.submission;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "feedbacks")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback extends BaseEntity {

    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "feedback_time", nullable = false)
    private Instant feedbackTime;

    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_submission_id", nullable = false)
    private QuestionSubmission questionSubmission;
}

