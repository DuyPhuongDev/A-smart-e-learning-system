package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningGoal.GoalValidationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalValidationResultRepository extends JpaRepository<GoalValidationResult, UUID> {

  Optional<GoalValidationResult> findTopByLearningGoalIdOrderByValidationTimestampDesc(UUID learningGoalId);
}
