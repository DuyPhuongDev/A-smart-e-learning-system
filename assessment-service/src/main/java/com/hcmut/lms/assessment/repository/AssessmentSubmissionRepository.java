package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, UUID> {

    Optional<AssessmentSubmission> findByIdAndStudentId(UUID id, UUID studentId);

    Optional<AssessmentSubmission> findFirstByAssessment_IdAndStudentIdAndStatusOrderByCreatedAtDesc(
            UUID assessmentId,
            UUID studentId,
            AssessmentSubmissionStatus status
    );

    List<AssessmentSubmission> findAllByAssessment_IdAndStudentIdOrderByAttemptNoDesc(UUID assessmentId, UUID studentId);

    List<AssessmentSubmission> findAllByStudentIdAndAssessment_IdIn(UUID studentId, List<UUID> assessmentIds);

    @Query("SELECT COALESCE(MAX(s.attemptNo), 0) FROM AssessmentSubmission s WHERE s.assessment.id = :assessmentId AND s.studentId = :studentId")
    int findMaxAttemptNo(@Param("assessmentId") UUID assessmentId, @Param("studentId") UUID studentId);
}
