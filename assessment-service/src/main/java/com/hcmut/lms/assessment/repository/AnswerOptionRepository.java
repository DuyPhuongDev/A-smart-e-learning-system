package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerOptionRepository extends JpaRepository<AnswerOption, UUID> {
    void deleteByQuestionId(UUID id);
}
