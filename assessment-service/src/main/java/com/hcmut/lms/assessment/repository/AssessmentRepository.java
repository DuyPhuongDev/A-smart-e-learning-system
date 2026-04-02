package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    org.springframework.data.domain.Page<Assessment> findAllByClassId(UUID classId, org.springframework.data.domain.Pageable pageable);

    List<Assessment> findByAssessmentStatusAndCloseTimeBetween(
            AssessmentStatus status,
            LocalDateTime from,
            LocalDateTime to
    );
}
