package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {

    /**
     * Find all enrollments by student ID with pagination
     */
    Page<Enrollment> findByStudentId(UUID studentId, Pageable pageable);

    /**
     * Find all enrollments by student ID
     */
    List<Enrollment> findByStudentId(UUID studentId);

    /**
     * Check if student already enrolled in a class
     */
    boolean existsByStudentIdAndClassId(UUID studentId, UUID classId);

    /**
     * Find enrollment by student ID and class ID
     */
    Optional<Enrollment> findByStudentIdAndClassId(UUID studentId, UUID classId);

    /**
     * Count enrollments for a specific class
     */
    long countByClassId(UUID classId);

    /**
     * Find all enrollments that have a final grade assigned.
     * Used for grade prediction dataset computation.
     */
    List<Enrollment> findByFinalGradeIsNotNull();

    /**
     * Find graded enrollments for a set of class IDs.
     */
    List<Enrollment> findByClassIdInAndFinalGradeIsNotNull(List<UUID> classIds);
}
