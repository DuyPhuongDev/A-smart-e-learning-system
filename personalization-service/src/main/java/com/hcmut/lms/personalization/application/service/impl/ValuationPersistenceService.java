package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectResultResponse;
import com.hcmut.lms.personalization.application.entity.OccupationData;
import com.hcmut.lms.personalization.application.entity.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ValuationPersistenceService {

    private final SubjectOccupationValuationRepository valuationRepository;

    @Transactional
    public void persistValuation(UUID subjectId,
                                 String subjectCode,
                                 String subjectName,
                                 OccupationData occupation,
                                 BigDecimal totalValue,
                                 double valueDensity,
                                 List<ValuationSubjectResultResponse.ValuationMatchSummary> topMatches) {
        valuationRepository.deleteBySubjectIdAndTargetOccupationCode(subjectId, occupation.getOnetsocCode());
        Map<String, Object> detailsMap = Map.of("matches", topMatches);

        SubjectOccupationValuation entity = SubjectOccupationValuation.builder()
                .subjectId(subjectId)
                .subjectCode(subjectCode)
                .subjectName(subjectName)
                .targetOccupationCode(occupation.getOnetsocCode())
                .targetOccupationTitle(occupation.getTitle())
                .totalValue(totalValue)
                .valueDensity(BigDecimal.valueOf(valueDensity))
                .details(detailsMap)
                .build();

        valuationRepository.save(entity);
    }
}

