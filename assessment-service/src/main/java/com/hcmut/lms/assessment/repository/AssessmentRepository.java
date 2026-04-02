package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<Assessment, UUID> {

    org.springframework.data.domain.Page<Assessment> findAllByClassId(UUID classId, org.springframework.data.domain.Pageable pageable);
}
