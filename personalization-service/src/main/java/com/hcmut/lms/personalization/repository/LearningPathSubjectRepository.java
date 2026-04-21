package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearningPathSubjectRepository extends JpaRepository<LearningPathSubject, UUID> {

  List<LearningPathSubject> findByLearningPathId(UUID learningPathId);

  List<LearningPathSubject> findByLearningPathIdOrderByLearningPathSectionIdAscSubjectCodeAsc(UUID learningPathId);

  List<LearningPathSubject> findByLearningPathIdAndSubjectId(UUID learningPathId, UUID subjectId);
}