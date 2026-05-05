package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.submission.AssessmentSubmissionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    org.springframework.data.domain.Page<Assessment> findAllByClassId(UUID classId, org.springframework.data.domain.Pageable pageable);

    List<Assessment> findByClassIdOrderByCreatedAtAsc(UUID classId);

    List<Assessment> findByClassIdInOrderByCloseTimeAsc(List<UUID> classIds);

    long countByClassIdAndAssessmentType(UUID classId, AssessmentType assessmentType);

    List<Assessment> findByAssessmentStatusAndCloseTimeBetween(
            AssessmentStatus status,
            Instant from,
            Instant to
    );

    @Query("SELECT a.classId, COUNT(a) FROM Assessment a " +
           "WHERE a.classId IN :classIds " +
           "AND a.assessmentStatus = :publishedStatus " +
           "AND a.id NOT IN (" +
           "  SELECT s.assessment.id FROM AssessmentSubmission s " +
           "  WHERE s.studentId = :studentId AND s.status = :submittedStatus" +
           ") " +
           "GROUP BY a.classId")
    List<Object[]> countPendingByClassIds(
            @Param("classIds") List<UUID> classIds,
            @Param("studentId") UUID studentId,
            @Param("publishedStatus") AssessmentStatus publishedStatus,
            @Param("submittedStatus") AssessmentSubmissionStatus submittedStatus);

    @Query("SELECT a.classId, " +
           "  COUNT(CASE WHEN s.status = :submittedStatus THEN 1 END), " +
           "  COUNT(CASE WHEN (s.id IS NULL OR s.status <> :submittedStatus) THEN 1 END) " +
           "FROM Assessment a " +
           "LEFT JOIN AssessmentSubmission s " +
           "  ON s.assessment.id = a.id AND s.studentId = :studentId " +
           "WHERE a.classId IN :classIds " +
           "  AND a.assessmentStatus = :publishedStatus " +
           "GROUP BY a.classId")
    List<Object[]> countSubmittedAndPendingByClassIds(
            @Param("classIds") List<UUID> classIds,
            @Param("studentId") UUID studentId,
            @Param("publishedStatus") AssessmentStatus publishedStatus,
            @Param("submittedStatus") AssessmentSubmissionStatus submittedStatus);
}
