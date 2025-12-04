package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.intakeYear.IntakeYear;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IntakeYearRepository extends JpaRepository<IntakeYear, UUID> {
    Optional<IntakeYear> findByStartYear(Integer startYear);
    boolean existsByStartYear(Integer startYear);
}

