package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.application.dto.request.CreateLectureKnowledgeChunkRequest;
import com.hcmut.lms.learning.application.dto.response.LectureKnowledgeChunkResponse;
import com.hcmut.lms.learning.application.service.LectureKnowledgeChunkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lecture-knowledge-chunks")
@RequiredArgsConstructor
public class LectureKnowledgeChunkController {

    private final LectureKnowledgeChunkService chunkService;

    @PostMapping
    public ResponseEntity<LectureKnowledgeChunkResponse> createChunk(
            @RequestParam UUID lectureKnowledgeId,
            @RequestBody CreateLectureKnowledgeChunkRequest request) {
        LectureKnowledgeChunkResponse response = chunkService.createChunk(lectureKnowledgeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectureKnowledgeChunkResponse> getChunkById(
            @PathVariable UUID id) {
        LectureKnowledgeChunkResponse response = chunkService.getChunkById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/knowledge/{lectureKnowledgeId}")
    public ResponseEntity<List<LectureKnowledgeChunkResponse>> getChunksByLectureKnowledgeId(
            @PathVariable UUID lectureKnowledgeId) {
        List<LectureKnowledgeChunkResponse> response = chunkService.getChunksByLectureKnowledgeId(lectureKnowledgeId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/knowledge/{lectureKnowledgeId}/ordered")
    public ResponseEntity<List<LectureKnowledgeChunkResponse>> getChunksByLectureKnowledgeIdOrdered(
            @PathVariable UUID lectureKnowledgeId) {
        List<LectureKnowledgeChunkResponse> response = chunkService.getChunksByLectureKnowledgeIdOrderByIndex(lectureKnowledgeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LectureKnowledgeChunkResponse> updateChunk(
            @PathVariable UUID id,
            @RequestBody CreateLectureKnowledgeChunkRequest request) {
        LectureKnowledgeChunkResponse response = chunkService.updateChunk(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChunk(
            @PathVariable UUID id) {
        chunkService.deleteChunk(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/knowledge/{lectureKnowledgeId}")
    public ResponseEntity<Void> deleteAllChunksByLectureKnowledgeId(
            @PathVariable UUID lectureKnowledgeId) {
        chunkService.deleteAllChunksByLectureKnowledgeId(lectureKnowledgeId);
        return ResponseEntity.noContent().build();
    }
}
