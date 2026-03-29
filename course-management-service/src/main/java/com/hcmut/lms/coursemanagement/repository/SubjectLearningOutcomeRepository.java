package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectLearningOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectLearningOutcomeRepository extends JpaRepository<SubjectLearningOutcome, UUID> {
    List<SubjectLearningOutcome> findBySubjectIdOrderByDisplayOrderAsc(UUID subjectId);
}
