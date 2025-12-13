package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradingRepository extends JpaRepository<Grading, UUID> {
    List<Grading> findByGradingType(String gradingType);
}

