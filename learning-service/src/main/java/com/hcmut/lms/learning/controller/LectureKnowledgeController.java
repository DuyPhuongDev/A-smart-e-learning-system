package com.hcmut.lms.learning.controller;

import com.hcmut.lms.learning.application.dto.request.CreateLectureKnowledgeRequest;
import com.hcmut.lms.learning.application.dto.request.UpdateLectureKnowledgeRequest;
import com.hcmut.lms.learning.application.dto.response.LectureKnowledgeResponse;
import com.hcmut.lms.learning.application.service.LectureKnowledgeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lecture-knowledge")
@RequiredArgsConstructor
public class LectureKnowledgeController {

    private final LectureKnowledgeService lectureKnowledgeService;

    @PostMapping
    public ResponseEntity<LectureKnowledgeResponse> createLectureKnowledge(
            @RequestBody CreateLectureKnowledgeRequest request) {
        LectureKnowledgeResponse response = lectureKnowledgeService.createLectureKnowledge(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LectureKnowledgeResponse> getLectureKnowledgeById(
            @PathVariable UUID id) {
        LectureKnowledgeResponse response = lectureKnowledgeService.getLectureKnowledgeById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lecture/{lectureId}")
    public ResponseEntity<LectureKnowledgeResponse> getLectureKnowledgeByLectureId(
            @PathVariable UUID lectureId) {
        LectureKnowledgeResponse response = lectureKnowledgeService.getLectureKnowledgeByLectureId(lectureId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lecture/{lectureId}/all")
    public ResponseEntity<List<LectureKnowledgeResponse>> getAllLectureKnowledgeByLectureId(
            @PathVariable UUID lectureId) {
        List<LectureKnowledgeResponse> response = lectureKnowledgeService.getAllLectureKnowledgeByLectureId(lectureId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LectureKnowledgeResponse> updateLectureKnowledge(
            @PathVariable UUID id,
            @RequestBody UpdateLectureKnowledgeRequest request) {
        LectureKnowledgeResponse response = lectureKnowledgeService.updateLectureKnowledge(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLectureKnowledge(
            @PathVariable UUID id) {
        lectureKnowledgeService.deleteLectureKnowledge(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<LectureKnowledgeResponse>> getAllByStatus(
            @PathVariable String status) {
        List<LectureKnowledgeResponse> response = lectureKnowledgeService.getAllByStatus(status);
        return ResponseEntity.ok(response);
    }
}
