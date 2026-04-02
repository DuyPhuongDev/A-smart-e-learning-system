package com.hcmut.lms.personalization.repository;

import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubjectOccupationValuationRepository extends JpaRepository<SubjectOccupationValuation, UUID> {
  List<SubjectOccupationValuation> findByTargetOccupationCode(String targetOccupationCode);

  List<SubjectOccupationValuation> findBySubjectIdIn(List<UUID> subjectIds);

  void deleteBySubjectIdAndTargetOccupationCode(UUID subjectId, String occupationCode);
}
