package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.dto.response.enums.FeasibilityLevel;
import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalValidationResultRepository extends JpaRepository<GoalValidationResult, UUID> {

  Optional<GoalValidationResult> findTopByLearningGoalIdOrderByValidationTimestampDesc(UUID learningGoalId);

  List<GoalValidationResult> findByStudentIdOrderByValidationTimestampDesc(UUID studentId);

  @Query("SELECT v FROM GoalValidationResult v WHERE v.studentId = :studentId " + "AND v.feasibilityLevel = :level " + "ORDER BY v.validationTimestamp DESC")
  List<GoalValidationResult> findByStudentIdAndFeasibilityLevel(
      @Param("studentId") UUID studentId,
      @Param("level") FeasibilityLevel level);
}
