package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.application.entity.SubjectOccupationValuation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubjectOccupationValuationRepository extends JpaRepository<SubjectOccupationValuation, UUID> {
    void deleteBySubjectIdAndTargetOccupationCode(UUID subjectId, String occupationCode);
}
