package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmission;
import com.hcmut.lms.assessment.domain.entity.submission.QuestionSubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionSubmissionRepository extends JpaRepository<QuestionSubmission, UUID> {

    Optional<QuestionSubmission> findByAssessmentSubmission_IdAndQuestion_Id(UUID assessmentSubmissionId, UUID questionId);

    @Query("SELECT qs FROM QuestionSubmission qs JOIN FETCH qs.question WHERE qs.assessmentSubmission.id = :attemptId")
    List<QuestionSubmission> findAllByAttemptIdWithQuestion(@Param("attemptId") UUID attemptId);

    @Query("""
            SELECT DISTINCT qs.assessmentSubmission.id
            FROM QuestionSubmission qs
            WHERE qs.assessmentSubmission.id IN :attemptIds
              AND qs.status = :status
            """)
    List<UUID> findAttemptIdsByAttemptIdsAndStatus(
            @Param("attemptIds") List<UUID> attemptIds,
            @Param("status") QuestionSubmissionStatus status
    );
}
