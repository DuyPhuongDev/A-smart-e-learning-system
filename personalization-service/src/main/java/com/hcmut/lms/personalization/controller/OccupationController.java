package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationSubjectScoreResponse;
import com.hcmut.lms.personalization.application.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/occupations")
@RequiredArgsConstructor
public class OccupationController {

  private final OccupationService occupationService;

  @GetMapping
  public ResponseEntity<PageResponse<OccupationResponse>> getOccupations(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) String keyword) {
    return ResponseEntity.ok(occupationService.getOccupations(page, size, keyword));
  }

  @GetMapping("/all")
  public ResponseEntity<List<OccupationResponse>> getAllOccupations() {
    return ResponseEntity.ok(occupationService.getAllOccupations());
  }

  @GetMapping("/{occupationCode}")
  public ResponseEntity<OccupationDetailResponse> getOccupationDetail(@PathVariable String occupationCode) {
    return ResponseEntity.ok(occupationService.getOccupationDetail(occupationCode));
  }

    @GetMapping("/{occupationId}/subjects")
  public ResponseEntity<List<OccupationSubjectScoreResponse>> getOccupationSubjects(@PathVariable UUID occupationId) {
    return ResponseEntity.ok(occupationService.getOccupationSubjects(occupationId));
  }
}
