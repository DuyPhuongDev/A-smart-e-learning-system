package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, UUID> {

  Optional<LearningPath> findTopByStudentIdAndIsActiveTrueOrderByUpdatedAtDesc(UUID studentId);

  Optional<LearningPath> findByLearningPathIdAndStudentIdAndIsActiveTrue(UUID learningPathId, UUID studentId);

  @Modifying
  @Query("UPDATE LearningPath lp SET lp.isActive = false WHERE lp.studentId = :studentId AND lp.isActive = true")
  void deactivateActiveByStudentId(@Param("studentId") UUID studentId);

  @Modifying
  @Query("UPDATE LearningPath lp SET lp.isActive = false WHERE lp.studentId = :studentId AND lp.learningGoalId = " +
      ":learningGoalId AND lp.isActive = true")
  void deactivateActiveByStudentIdAndLearningGoalId(
      @Param("studentId") UUID studentId, @Param("learningGoalId") UUID learningGoalId);
}

