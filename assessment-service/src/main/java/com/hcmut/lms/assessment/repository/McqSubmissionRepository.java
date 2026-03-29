package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.McqSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface McqSubmissionRepository extends JpaRepository<McqSubmission, UUID> {

    Optional<McqSubmission> findByAssessmentSubmission_IdAndQuestion_Id(UUID assessmentSubmissionId, UUID questionId);
}
