package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.CodingSubmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CodingSubmissionRepository extends JpaRepository<CodingSubmission, UUID> {

    Optional<CodingSubmission> findByAssessmentSubmission_IdAndQuestion_Id(UUID assessmentSubmissionId, UUID questionId);
}
