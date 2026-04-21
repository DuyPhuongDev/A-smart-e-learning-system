package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningPath.LearningPathSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LearningPathSectionRepository extends JpaRepository<LearningPathSection, UUID> {

  List<LearningPathSection> findByLearningPathIdOrderByAcademicYearOrderAscSemesterOrderAsc(UUID learningPathId);

  List<LearningPathSection> findByLearningPathId(UUID learningPathId);
}
