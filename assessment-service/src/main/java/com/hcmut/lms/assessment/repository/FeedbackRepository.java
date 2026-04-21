package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByQuestionSubmission_IdOrderByFeedbackTimeDesc(UUID questionSubmissionId);

    List<Feedback> findByQuestionSubmission_IdInOrderByFeedbackTimeDesc(List<UUID> questionSubmissionIds);
}

