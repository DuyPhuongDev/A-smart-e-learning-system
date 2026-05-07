package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.personalization.application.dto.request.ValuationOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.AsyncSubmissionResponse;
import com.hcmut.lms.personalization.application.service.ValuationOccupationService;
import com.hcmut.lms.personalization.client.CourseManagementClient;
import com.hcmut.lms.personalization.domain.entity.occupationData.OccupationData;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/occupations/valuation")
@RequiredArgsConstructor
@Slf4j
public class ValuationOccupationController {

  private final ValuationOccupationService valuationService;
  private final CourseManagementClient courseManagementClient;
  private final OccupationDataRepository occupationDataRepository;

  @PostMapping
  public ResponseEntity<AsyncSubmissionResponse> valuate(@Valid @RequestBody ValuationOccupationRequest request) {
    List<UUID> subjectIds = request.getSubjectIds();
    if (subjectIds == null || subjectIds.isEmpty()) {
      log.info("No subjectIds provided; fetching all subjects from course-management-service.");
      subjectIds = courseManagementClient.getAllSubjectIds();
    }
    subjectIds = subjectIds.stream().filter(Objects::nonNull).distinct().toList();

    List<String> occupationCodes = request.getOccupationCodes();
    if (occupationCodes == null || occupationCodes.isEmpty()) {
      log.info("No occupationCodes provided; fetching all occupations.");
      occupationCodes = occupationDataRepository.findAll().stream()
          .map(OccupationData::getOnetsocCode)
          .filter(Objects::nonNull)
          .distinct()
          .toList();
    }
    occupationCodes = occupationCodes.stream().filter(Objects::nonNull).distinct().toList();

    ValuationOccupationRequest enriched = ValuationOccupationRequest.builder()
        .subjectIds(subjectIds)
        .occupationCodes(occupationCodes)
        .build();

    int subjectCount = subjectIds.size();
    int occupationCount = occupationCodes.size();

    valuationService.valuateAsync(enriched).whenComplete((result, throwable) -> {
      if (throwable != null) {
        log.error(
            "Background valuation failed for subjectCount={} occupationCount={}", subjectCount, occupationCount,
            throwable);
        return;
      }
      log.info(
          "Background valuation completed for subjectCount={} occupationCount={} persistedCount={}", subjectCount,
          occupationCount, result != null ? result.getPersistedCount() : null);
    });

    return ResponseEntity.status(HttpStatus.ACCEPTED)
        .body(AsyncSubmissionResponse.builder()
            .status("ACCEPTED")
            .message("Valuation started successfully. Processing continues in the background.")
            .subjectCount(subjectCount)
            .occupationCount(occupationCount)
            .build());
  }
}
