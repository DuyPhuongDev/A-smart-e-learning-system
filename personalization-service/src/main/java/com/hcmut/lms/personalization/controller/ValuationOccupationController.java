package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.personalization.application.dto.request.ValuationOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.AsyncSubmissionResponse;
import com.hcmut.lms.personalization.application.service.ValuationOccupationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("${prefix-api}/occupations/valuation")
@RequiredArgsConstructor
@Slf4j
public class ValuationOccupationController {

    private final ValuationOccupationService valuationService;

    @PostMapping
    public ResponseEntity<AsyncSubmissionResponse> valuate(@Valid @RequestBody ValuationOccupationRequest request) {
        int subjectCount = (int) request.getSubjectIds().stream().filter(Objects::nonNull).distinct().count();
        int occupationCount = (int) request.getOccupationCodes().stream().filter(Objects::nonNull).distinct().count();

        valuationService.valuateAsync(request)
                .whenComplete((result, throwable) -> {
                    if (throwable != null) {
                        log.error("Background valuation failed for subjectCount={} occupationCount={}", subjectCount, occupationCount, throwable);
                        return;
                    }
                    log.info("Background valuation completed for subjectCount={} occupationCount={} persistedCount={}",
                            subjectCount, occupationCount, result != null ? result.getPersistedCount() : null);
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
