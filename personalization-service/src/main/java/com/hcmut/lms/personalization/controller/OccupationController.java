package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${prefix-api}/occupations")
@RequiredArgsConstructor
public class OccupationController {

    private final OccupationService occupationService;

    @GetMapping
    public ResponseEntity<PageResponse<OccupationResponse>> getOccupations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(occupationService.getOccupations(page, size, keyword));
    }

    @GetMapping("/{occupationCode}")
    public ResponseEntity<OccupationDetailResponse> getOccupationDetail(@PathVariable String occupationCode) {
        return ResponseEntity.ok(occupationService.getOccupationDetail(occupationCode));
    }
}
