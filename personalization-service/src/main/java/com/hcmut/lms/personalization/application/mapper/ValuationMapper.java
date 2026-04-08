package com.hcmut.lms.personalization.application.mapper;

import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectPersistenceResponse;
import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectResultResponse;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.OnetFlattenedRequirementEmbeddingRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ValuationMapper {

  @Mapping(target = "importanceScore", source = "importanceScore")
  ValuationSubjectResultResponse.ValuationMatchSummary toTopMatchSummary(
      OnetFlattenedRequirementEmbeddingRepository.ValuationTopMatchRow row);

  default ValuationSubjectResultResponse toResultResponse(
      UUID subjectId, String occupationCode, BigDecimal totalValue, double valueDensity,
      List<ValuationSubjectResultResponse.ValuationMatchSummary> topMatches) {
    return ValuationSubjectResultResponse.builder()
        .subjectId(subjectId)
        .occupationCode(occupationCode)
        .totalValue(totalValue != null ? totalValue.doubleValue() : 0.0)
        .valueDensity(valueDensity)
        .topMatches(topMatches)
        .build();
  }

  default ValuationSubjectPersistenceResponse toPersistenceResponse(List<ValuationSubjectResultResponse> results) {
    return ValuationSubjectPersistenceResponse.builder()
        .results(results)
        .persistedCount(results != null ? results.size() : 0)
        .build();
  }

  default ValuationSubjectPersistenceResponse emptyPersistenceResponse() {
    return toPersistenceResponse(Collections.emptyList());
  }

  default SubjectOccupationValuation toEntity(
      UUID subjectId, String subjectCode, String subjectName, OccupationData occupation, BigDecimal totalValue,
      double valueDensity, List<ValuationSubjectResultResponse.ValuationMatchSummary> topMatches) {
    Map<String, Object> detailsMap = Map.of("matches", topMatches);
    return SubjectOccupationValuation.builder()
        .subjectId(subjectId)
        .subjectCode(subjectCode)
        .subjectName(subjectName)
        .targetOccupationCode(occupation.getOnetsocCode())
        .targetOccupationTitle(occupation.getTitle())
        .totalValue(totalValue)
        .valueDensity(BigDecimal.valueOf(valueDensity))
        .details(detailsMap)
        .build();
  }
}

