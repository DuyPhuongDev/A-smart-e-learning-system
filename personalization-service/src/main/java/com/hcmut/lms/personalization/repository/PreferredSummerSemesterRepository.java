package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningGoal.PreferredSummerSemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PreferredSummerSemesterRepository extends JpaRepository<PreferredSummerSemester, UUID> {

  List<PreferredSummerSemester> findByLearningGoalLearningGoalIdOrderBySemesterIdAsc(UUID learningGoalId);

  boolean existsByLearningGoalLearningGoalIdAndSemesterId(UUID learningGoalId, UUID semesterId);
}

