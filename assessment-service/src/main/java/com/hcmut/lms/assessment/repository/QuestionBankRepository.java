package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.questionBank.QuestionBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuestionBankRepository extends JpaRepository<QuestionBank, UUID> {

    Page<QuestionBank> findAllByOwnerId(UUID ownerId, Pageable pageable);

    boolean existsByNameAndOwnerId(String name, UUID ownerId);
}
