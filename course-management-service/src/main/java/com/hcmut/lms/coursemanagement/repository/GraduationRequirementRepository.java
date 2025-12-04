package com.hcmut.lms.coursemanagement.repository;

import com.hcmut.lms.coursemanagement.domain.entity.graduationRequirement.GraduationRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, UUID> {
    Optional<GraduationRequirement> findByCode(String code);
}

