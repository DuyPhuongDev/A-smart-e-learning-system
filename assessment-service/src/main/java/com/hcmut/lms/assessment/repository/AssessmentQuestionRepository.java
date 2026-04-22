package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentQuestionRepository extends JpaRepository<AssessmentQuestion, UUID> {

    @Query("SELECT aq FROM AssessmentQuestion aq WHERE aq.assessment.id = :assessmentId ORDER BY aq.orderIndex")
    List<AssessmentQuestion> findByAssessmentIdOrderByIndex(@Param("assessmentId") UUID assessmentId);

    Optional<AssessmentQuestion> findByQuestionIdAndAssessmentId(UUID questionId, UUID assessmentId);

    void deleteByAssessmentIdAndQuestionId(UUID assessmentId, UUID questionId);

    boolean existsByAssessment_IdAndQuestion_Id(UUID assessmentId, UUID questionId);
}
