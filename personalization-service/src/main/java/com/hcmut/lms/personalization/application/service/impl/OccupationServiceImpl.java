package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.personalization.application.dto.request.ValuationOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationSubjectScoreResponse;
import com.hcmut.lms.personalization.application.mapper.OccupationMapper;
import com.hcmut.lms.personalization.application.service.OccupationService;
import com.hcmut.lms.personalization.application.service.ValuationOccupationService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.domain.entity.occupationData.SubjectOccupationValuation;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import com.hcmut.lms.personalization.repository.OccupationSummaryRepository;
import com.hcmut.lms.personalization.repository.SubjectOccupationValuationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OccupationServiceImpl implements OccupationService {

  private final OccupationDataRepository occupationDataRepository;
  private final OccupationSummaryRepository occupationSummaryRepository;
  private final SubjectOccupationValuationRepository subjectOccupationValuationRepository;
  private final OccupationMapper occupationMapper;
  private final ValuationOccupationService valuationOccupationService;
  private final CourseManagementClient courseManagementClient;

  @Override
  public PageResponse<OccupationResponse> getOccupations(int page, int size, String keyword) {
    Pageable pageable = PageRequest.of(page, size);
    Page<OccupationData> result = (keyword == null || keyword.isBlank()) ? occupationDataRepository.findAll(
        pageable) : occupationDataRepository.findByTitleContainingIgnoreCase(keyword, pageable);
    return PageResponse.fromPage(result.map(occupationMapper::toResponse));
  }

  @Override
  public List<OccupationResponse> getAllOccupations() {
    List<OccupationData> result = occupationDataRepository.findAll();
    return result.stream().map(occupationMapper::toResponse).toList();
  }

  @Override
  public OccupationDetailResponse getOccupationDetail(String occupationCode) {
    log.info("Fetching occupation detail: {}", occupationCode);
    OccupationData data = occupationDataRepository.findByOnetsocCode(occupationCode)
        .orElseThrow(() -> new IllegalArgumentException("Occupation not found: " + occupationCode));

    OccupationDetailResponse response = occupationMapper.toDetailResponse(data);
    response.setTasksCount(occupationSummaryRepository.countTasks(occupationCode));
    response.setDwaCount(occupationSummaryRepository.countDwa(occupationCode));
    return response;
  }

  @Override
  public List<OccupationSubjectScoreResponse> getOccupationSubjects(UUID occupationId) {
    log.info("Fetching occupation subjects for occupationId: {}", occupationId);
    OccupationData occupation = occupationDataRepository.findById(occupationId)
        .orElseThrow(() -> new IllegalArgumentException("Occupation not found: " + occupationId));

    List<SubjectOccupationValuation> valuations = subjectOccupationValuationRepository
        .findByTargetOccupationCodeOrderByTotalValueDesc(occupation.getOnetsocCode());

    if (valuations.isEmpty()) {
      log.info("No valuations found for occupation {}, triggering computation...", occupation.getOnetsocCode());
      List<UUID> allSubjectIds = courseManagementClient.getAllSubjectIds();
      if (allSubjectIds != null && !allSubjectIds.isEmpty()) {
        ValuationOccupationRequest request = ValuationOccupationRequest.builder()
            .subjectIds(allSubjectIds)
            .occupationCodes(List.of(occupation.getOnetsocCode()))
            .build();
        try {
          valuationOccupationService.valuateAsync(request).join();
          valuations = subjectOccupationValuationRepository
              .findByTargetOccupationCodeOrderByTotalValueDesc(occupation.getOnetsocCode());
        } catch (Exception e) {
          log.error("Failed to compute occupation valuations for occupationCode={}", occupation.getOnetsocCode(), e);
        }
      }
    }

    return valuations.stream()
        .map(v -> OccupationSubjectScoreResponse.builder()
            .subjectId(v.getSubjectId())
            .subjectCode(v.getSubjectCode())
            .subjectName(v.getSubjectName())
            .totalValue(v.getTotalValue())
            .valueDensity(v.getValueDensity())
            .build())
        .toList();
  }
}
