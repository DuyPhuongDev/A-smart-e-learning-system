package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentSubmissionRepository extends JpaRepository<AssessmentSubmission, UUID> {

    Optional<AssessmentSubmission> findByIdAndStudentId(UUID id, UUID studentId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM AssessmentSubmission s WHERE s.id = :id")
    Optional<AssessmentSubmission> findByIdForUpdate(@Param("id") UUID id);

    Optional<AssessmentSubmission> findFirstByAssessment_IdAndStudentIdAndStatusOrderByCreatedAtDesc(
            UUID assessmentId,
            UUID studentId,
            AssessmentSubmissionStatus status
    );

    List<AssessmentSubmission> findAllByAssessment_IdAndStudentIdOrderByAttemptNoDesc(UUID assessmentId, UUID studentId);

    List<AssessmentSubmission> findAllByStudentIdAndAssessment_IdIn(UUID studentId, List<UUID> assessmentIds);

    Page<AssessmentSubmission> findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
            UUID assessmentId,
            AssessmentSubmissionStatus status,
            Pageable pageable
    );

    List<AssessmentSubmission> findByAssessment_IdAndStatusOrderBySubmitTimeDesc(
            UUID assessmentId,
            AssessmentSubmissionStatus status
    );

    List<AssessmentSubmission> findByStudentIdInAndAssessment_IdInAndStatus(
            List<UUID> studentIds,
            List<UUID> assessmentIds,
            AssessmentSubmissionStatus status
    );

    List<AssessmentSubmission> findByAssessment_IdInAndStudentIdInAndStatus(
            List<UUID> assessmentIds,
            List<UUID> studentIds,
            AssessmentSubmissionStatus status
    );

    @Query("SELECT COALESCE(MAX(s.attemptNo), 0) FROM AssessmentSubmission s WHERE s.assessment.id = :assessmentId AND s.studentId = :studentId")
    int findMaxAttemptNo(@Param("assessmentId") UUID assessmentId, @Param("studentId") UUID studentId);
}
