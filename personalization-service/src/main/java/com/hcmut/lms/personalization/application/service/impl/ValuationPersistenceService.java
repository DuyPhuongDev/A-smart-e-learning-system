package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectResultResponse;
import com.hcmut.lms.personalization.application.mapper.ValuationMapper;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValuationPersistenceService {

  private final SubjectOccupationValuationRepository valuationRepository;
  private final ValuationMapper valuationMapper;

  @Transactional
  public void persistValuation(
      UUID subjectId, String subjectCode, String subjectName, OccupationData occupation, BigDecimal totalValue,
      double valueDensity, List<ValuationSubjectResultResponse.ValuationMatchSummary> topMatches) {
    valuationRepository.deleteBySubjectIdAndTargetOccupationCode(subjectId, occupation.getOnetsocCode());

    SubjectOccupationValuation entity = valuationMapper.toEntity(
        subjectId, subjectCode, subjectName, occupation,
        totalValue, valueDensity, topMatches);

    valuationRepository.save(entity);
  }
}

