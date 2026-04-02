package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.EssaySubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EssaySubmissionRepository extends JpaRepository<EssaySubmission, UUID> {

    Optional<EssaySubmission> findByAssessmentSubmission_IdAndQuestion_Id(UUID assessmentSubmissionId, UUID questionId);
}
