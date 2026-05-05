package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.personalization.application.dto.request.ValuationOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectPersistenceResponse;
import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectResultResponse;
import com.hcmut.lms.personalization.application.mapper.ValuationMapper;
import com.hcmut.lms.personalization.application.service.EmbeddingService;
import com.hcmut.lms.personalization.application.service.ValuationOccupationService;
import com.hcmut.lms.personalization.application.service.impl.support.VectorLiteralFormatter;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.client.dto.SubjectLearningOutcomeResponse;
import com.hcmut.lms.personalization.client.dto.SubjectResponse;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.OnetFlattenedRequirement;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import com.hcmut.lms.personalization.repository.OnetFlattenedRequirementEmbeddingRepository;
import com.hcmut.lms.personalization.repository.OnetFlattenedRequirementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ValuationOccupationServiceImpl implements ValuationOccupationService {

  private static final double DEFAULT_THRESHOLD = 0.7;
  private static final int TOP_MATCHES = 5;

  private final OnetFlattenedRequirementRepository flattenedRequirementRepository;
  private final OnetFlattenedRequirementEmbeddingRepository requirementEmbeddingRepository;
  private final OccupationDataRepository occupationDataRepository;
  private final ValuationPersistenceService valuationPersistenceService;
  private final ValuationMapper valuationMapper;
  private final CourseManagementClient courseManagementClient;
  private final EmbeddingService embeddingService;
  private final Executor taskExecutor;

  @Override
  @Async("taskExecutor")
  public CompletableFuture<ValuationSubjectPersistenceResponse> valuateAsync(ValuationOccupationRequest request) {
    log.info("Starting valuation: subjectIds={} defaultThreshold={}", request.getSubjectIds(), DEFAULT_THRESHOLD);

    List<UUID> subjectIds = Optional.ofNullable(request.getSubjectIds())
        .orElse(List.of())
        .stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    if (subjectIds.isEmpty()) {
      return CompletableFuture.completedFuture(valuationMapper.emptyPersistenceResponse());
    }

    List<String> requestedOccupationCodes = Optional.ofNullable(request.getOccupationCodes())
        .orElse(List.of())
        .stream()
        .filter(Objects::nonNull)
        .distinct()
        .toList();

    List<OccupationData> occupations = requestedOccupationCodes.isEmpty() ? occupationDataRepository.findAll() :
        occupationDataRepository.findByOnetsocCodeIn(
        requestedOccupationCodes);

    if (occupations.isEmpty()) {
      log.info("No occupations found for requested codes={}, skipping valuation", requestedOccupationCodes);
      return CompletableFuture.completedFuture(valuationMapper.emptyPersistenceResponse());
    }

    Map<UUID, SubjectMeta> subjectMetaById = new HashMap<>();
    Map<UUID, List<String>> subjectSloTextsById = new HashMap<>();
    Map<UUID, CompletableFuture<SubjectData>> subjectDataFutures = new HashMap<>();

    for (UUID subjectId : subjectIds) {
      subjectDataFutures.put(subjectId, CompletableFuture.supplyAsync(() -> fetchSubjectData(subjectId), taskExecutor));
    }

    CompletableFuture.allOf(subjectDataFutures.values().toArray(CompletableFuture[]::new)).join();
    for (UUID subjectId : subjectIds) {
      SubjectData subjectData = subjectDataFutures.get(subjectId).join();
      subjectMetaById.put(subjectId, subjectData.meta());
      subjectSloTextsById.put(subjectId, subjectData.sloTexts());
    }

    List<String> occupationCodes = occupations.stream()
        .map(OccupationData::getOnetsocCode)
        .filter(Objects::nonNull)
        .toList();

    Map<String, List<OnetFlattenedRequirement>> requirementsByOccupation =
        flattenedRequirementRepository.findByOccupation_OnetsocCodeIn(
            occupationCodes)
        .stream()
        .filter(requirement -> requirement.getContentText() != null && !requirement.getContentText().isBlank())
        .collect(Collectors.groupingBy(requirement -> requirement.getOccupation().getOnetsocCode()));

    ensureRequirementEmbeddings(requirementsByOccupation);

    List<String> uniqueSloTexts = subjectSloTextsById.values()
        .stream()
        .flatMap(List::stream)
        .filter(s -> s != null && !s.isBlank())
        .distinct()
        .toList();

    log.info("Preparing query embeddings: uniqueSloTexts={}", uniqueSloTexts.size());
    Map<String, float[]> sloEmbeddingCache = buildEmbeddingCache(uniqueSloTexts, "retrieval_query");

    Map<UUID, List<float[]>> subjectSloVectors = new HashMap<>();
    for (UUID subjectId : subjectIds) {
      List<float[]> sloVecs = subjectSloTextsById.getOrDefault(subjectId, List.of())
          .stream()
          .map(sloEmbeddingCache::get)
          .filter(Objects::nonNull)
          .toList();
      subjectSloVectors.put(subjectId, sloVecs);
      log.info(
          "Prepared SLO vectors once for subject {} (texts={} vectors={})", subjectId,
          subjectSloTextsById.getOrDefault(subjectId, List.of()).size(), sloVecs.size());
    }

    List<ValuationSubjectResultResponse> results = new ArrayList<>();
    for (OccupationData occupation : occupations) {
      String occCode = occupation.getOnetsocCode();
      int requirementCount = requirementsByOccupation.getOrDefault(occCode, List.of()).size();
      log.info(
          "Processing occupation {} (title={}) with {} requirements", occCode, occupation.getTitle(), requirementCount);

      for (UUID subjectId : subjectIds) {
        List<float[]> sloVecs = subjectSloVectors.getOrDefault(subjectId, List.of());
        String[] sloVectorLiterals = toVectorLiterals(sloVecs);

        BigDecimal totalValue = BigDecimal.ZERO;
        List<ValuationSubjectResultResponse.ValuationMatchSummary> topMatches = List.of();

        if (sloVectorLiterals.length > 0) {
          OnetFlattenedRequirementEmbeddingRepository.ValuationSummaryRow summary =
              requirementEmbeddingRepository.computeValuationSummary(
              sloVectorLiterals, occCode, DEFAULT_THRESHOLD);
          if (summary != null && summary.getTotalScore() != null) {
            totalValue = summary.getTotalScore();
          }

          topMatches = requirementEmbeddingRepository.findTopMatches(
                  sloVectorLiterals, occCode, DEFAULT_THRESHOLD, TOP_MATCHES)
              .stream()
              .map(valuationMapper::toTopMatchSummary)
              .toList();
        }

        double valueDensity = sloVecs.isEmpty() ? 0.0 : totalValue.divide(
            BigDecimal.valueOf(sloVecs.size()), 6, RoundingMode.HALF_UP).doubleValue();

        SubjectMeta subjectMeta = subjectMetaById.get(subjectId);
        valuationPersistenceService.persistValuation(
            subjectId, subjectMeta != null ? subjectMeta.code() : null, subjectMeta != null ? subjectMeta.name() : null,
            occupation, totalValue, valueDensity, topMatches);

        results.add(valuationMapper.toResultResponse(subjectId, occCode, totalValue, valueDensity, topMatches));
      }
    }

    log.info("Completed valuation: totalResults={}", results.size());
    return CompletableFuture.completedFuture(valuationMapper.toPersistenceResponse(results));
  }

  private void ensureRequirementEmbeddings(Map<String, List<OnetFlattenedRequirement>> requirementsByOccupation) {
    List<OnetFlattenedRequirement> allRequirements = requirementsByOccupation.values()
        .stream()
        .flatMap(List::stream)
        .filter(requirement -> requirement.getOnetFlattenedRequirementsId() != null)
        .toList();

    if (allRequirements.isEmpty()) {
      return;
    }

    Set<UUID> existingIds = new HashSet<>(requirementEmbeddingRepository.findExistingRequirementIds(
        allRequirements.stream().map(OnetFlattenedRequirement::getOnetFlattenedRequirementsId).toList()));

    List<OnetFlattenedRequirement> missing = allRequirements.stream()
        .filter(requirement -> !existingIds.contains(requirement.getOnetFlattenedRequirementsId()))
        .toList();

    if (missing.isEmpty()) {
      return;
    }

    List<String> uniqueMissingTexts = missing.stream()
        .map(OnetFlattenedRequirement::getContentText)
        .filter(text -> text != null && !text.isBlank())
        .distinct()
        .toList();

    Map<String, float[]> vectorByText = buildEmbeddingCache(uniqueMissingTexts, "retrieval_document");
    long upserted = 0;

    for (OnetFlattenedRequirement requirement : missing) {
      float[] vector = vectorByText.get(requirement.getContentText());
      if (vector == null || vector.length == 0) {
        continue;
      }
      requirementEmbeddingRepository.upsertEmbedding(
          requirement.getOnetFlattenedRequirementsId(),
          requirement.getOccupation().getOnetsocCode(), toVectorLiteral(vector));
      upserted++;
    }

    log.info("Requirement embedding upsert completed: missing={} upserted={}", missing.size(), upserted);
  }

  private record SubjectMeta(String code, String name) {
    private static SubjectMeta from(SubjectResponse subject) {
      if (subject == null) {
        return new SubjectMeta(null, null);
      }
      return new SubjectMeta(subject.getCode(), subject.getName());
    }
  }

  private record SubjectData(SubjectMeta meta, List<String> sloTexts) {
  }

  private SubjectData fetchSubjectData(UUID subjectId) {
    SubjectResponse subject = courseManagementClient.getSubjectById(subjectId);
    List<SubjectLearningOutcomeResponse> slos = courseManagementClient.getLearningOutcomes(subjectId);
    return new SubjectData(SubjectMeta.from(subject), extractSloTexts(slos));
  }

  private Map<String, float[]> buildEmbeddingCache(List<String> uniqueTexts, String taskType) {
    if (uniqueTexts.isEmpty()) {
      return Map.of();
    }

    List<float[]> vectors = embeddingService.embedTexts(uniqueTexts, taskType);
    Map<String, float[]> cache = new HashMap<>(uniqueTexts.size());
    for (int i = 0; i < uniqueTexts.size(); i++) {
      if (i < vectors.size() && vectors.get(i) != null && vectors.get(i).length > 0) {
        cache.put(uniqueTexts.get(i), vectors.get(i));
      }
    }
    return cache;
  }

  private List<String> extractSloTexts(List<SubjectLearningOutcomeResponse> slos) {
    return slos.stream()
        .map(slo -> slo.getDescriptionEn() != null && !slo.getDescriptionEn()
            .isBlank() ? slo.getDescriptionEn() : slo.getDescription())
        .filter(s -> s != null && !s.isBlank())
        .toList();
  }

  private String[] toVectorLiterals(List<float[]> vectors) {
    return vectors.stream()
        .filter(Objects::nonNull)
        .filter(vector -> vector.length > 0)
        .map(this::toVectorLiteral)
        .toArray(String[]::new);
  }

  private String toVectorLiteral(float[] vector) {
    return VectorLiteralFormatter.toVectorLiteral(vector);
  }

}
