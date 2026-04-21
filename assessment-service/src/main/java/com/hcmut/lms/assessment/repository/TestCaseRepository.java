package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {
}
