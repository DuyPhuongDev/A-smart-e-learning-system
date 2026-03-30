package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.SubmissionTestCaseResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SubmissionTestCaseResultRepository extends JpaRepository<SubmissionTestCaseResult, UUID> {

    List<SubmissionTestCaseResult> findAllByCodingSubmission_Id(UUID codingSubmissionId);

    void deleteByCodingSubmission_Id(UUID codingSubmissionId);
}
