package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.personalization.application.dto.request.BackfillRequirementEmbeddingsRequest;
import com.hcmut.lms.personalization.application.dto.request.FlattenOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.FlattenOccupationJobResponse;
import com.hcmut.lms.personalization.application.service.OnetFlattenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${prefix-api}/onet/flatten")
@RequiredArgsConstructor
public class OnetFlattenController {

    private final OnetFlattenService onetFlattenService;

    @PostMapping
    public ResponseEntity<List<FlattenOccupationJobResponse>> flatten(@RequestBody(required = false) FlattenOccupationRequest request) {
        return ResponseEntity.ok(onetFlattenService.flatten(request));
    }

    @PostMapping("/requirement-embeddings")
    public ResponseEntity<Map<String, String>> backfillRequirementEmbeddings(
            @RequestBody(required = false) BackfillRequirementEmbeddingsRequest request) {
        onetFlattenService.backfillRequirementEmbeddingsAsync(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of(
                        "status", "ACCEPTED",
                        "message", "Backfill started. Processing continues in background."));
    }
}
