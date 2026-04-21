package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.learningGoal.GraduationRequirementStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GraduationRequirementStatusRepository extends JpaRepository<GraduationRequirementStatus, UUID> {

  List<GraduationRequirementStatus> findByStudentId(UUID studentId);

  Optional<GraduationRequirementStatus> findByGraduationRequirementStatusIdAndStudentId(
      UUID graduationRequirementStatusId, UUID studentId);
}
