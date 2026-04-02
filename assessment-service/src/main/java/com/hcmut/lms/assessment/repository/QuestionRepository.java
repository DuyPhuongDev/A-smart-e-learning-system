package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {

    Page<Question> findAllByQuestionType(QuestionType questionType, Pageable pageable);

    @Query("""
            SELECT q FROM Question q
            JOIN q.banks b
            WHERE b.id = :bankId
            """)
    Page<Question> findAllByBankId(@Param("bankId") UUID bankId, Pageable pageable);

    @Query("""
            SELECT q FROM Question q
            JOIN q.banks b
            WHERE b.id = :bankId AND (:questionType IS NULL OR q.questionType = :questionType)
            """)
    Page<Question> findAllByBankIdAndType(
            @Param("bankId") UUID bankId,
            @Param("questionType") QuestionType questionType,
            Pageable pageable);

    @Query("""
    SELECT aq.question FROM AssessmentQuestion aq 
    WHERE aq.assessment.id = :assessmentId
    ORDER BY aq.orderIndex ASC
""")
    List<Question> findAllByAssessmentId(@Param("assessmentId") UUID assessmentId);
}
