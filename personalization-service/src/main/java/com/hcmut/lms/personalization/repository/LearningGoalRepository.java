package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningGoal.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LearningGoalRepository extends JpaRepository<LearningGoal, UUID> {

  Optional<LearningGoal> findByLearningGoalIdAndStudentIdAndIsActiveTrue(UUID learningGoalId, UUID studentId);

  Optional<LearningGoal> findTopByStudentIdAndIsActiveTrueOrderByCreatedAtDesc(UUID studentId);

  @Modifying
  @Query("UPDATE LearningGoal lg SET lg.isActive = false WHERE lg.studentId = :studentId AND lg.isActive = true")
  void deactivateActiveByStudentId(@Param("studentId") UUID studentId);
}
