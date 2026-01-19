package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.ChapterRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderListRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;
import com.hcmut.lms.coursemanagement.application.service.ChapterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/chapters")
@RequiredArgsConstructor
public class ChapterController {
    
    private final ChapterService chapterService;
    
    @PostMapping
    public ResponseEntity<ChapterResponse> createChapter(@Valid @RequestBody ChapterRequest requestDTO) {
        ChapterResponse response = chapterService.createChapter(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ChapterResponse> updateChapter(
            @PathVariable UUID id,
            @Valid @RequestBody ChapterRequest requestDTO) {
        ChapterResponse response = chapterService.updateChapter(id, requestDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ChapterResponse> getChapterById(@PathVariable UUID id) {
        ChapterResponse response = chapterService.getChapterById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/class-section/{classSectionId}")
    public ResponseEntity<List<ChapterResponse>> getChaptersByClassSectionId(
            @PathVariable UUID classSectionId) {
        List<ChapterResponse> response = chapterService.getChaptersByClassSectionId(classSectionId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(@PathVariable UUID id) {
        chapterService.deleteChapter(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/reorder")
    public ResponseEntity<List<ChapterResponse>> reorderChapters(
            @Valid @RequestBody ReorderListRequest request) {
        List<ChapterResponse> response = chapterService.reorderChapters(request);
        return ResponseEntity.ok(response);
    }
}

