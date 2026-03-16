package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, UUID> {
    Optional<Semester> findBySemesterCode(String semesterCode);
    List<Semester> findByAcademicYearId(UUID academicYearId);
    Page<Semester> findByAcademicYearId(UUID academicYearId, Pageable pageable);
    Optional<Semester> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate today1, LocalDate today2);
}

