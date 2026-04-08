package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.progress.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LearningProgressRepository extends JpaRepository<LearningProgress, UUID> {

    /**
     * Find learning progress by student ID and lecture ID
     */
    Optional<LearningProgress> findByStudentIdAndLectureId(UUID studentId, UUID lectureId);

    /**
     * Check if learning progress exists for student and lecture
     */
    boolean existsByStudentIdAndLectureId(UUID studentId, UUID lectureId);

    /**
     * Find all learning progress by student ID
     */
    List<LearningProgress> findByStudentId(UUID studentId);

    /**
     * Count completed lectures for a student in a class
     * Note: This requires lecture-class relationship, may need service layer implementation
     */
    @Query("SELECT COUNT(lp) FROM LearningProgress lp WHERE lp.studentId = :studentId AND lp.completedAt IS NOT NULL")
    Long countCompletedLecturesByStudentId(@Param("studentId") UUID studentId);
}
