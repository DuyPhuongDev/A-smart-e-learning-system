package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    org.springframework.data.domain.Page<Assessment> findAllByClassId(UUID classId, org.springframework.data.domain.Pageable pageable);

    List<Assessment> findByClassIdOrderByCreatedAtAsc(UUID classId);

    long countByClassIdAndAssessmentType(UUID classId, AssessmentType assessmentType);

    List<Assessment> findByAssessmentStatusAndCloseTimeBetween(
            AssessmentStatus status,
            Instant from,
            Instant to
    );
}
