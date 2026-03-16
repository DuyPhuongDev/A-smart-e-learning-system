package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.*;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.service.LectureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/lectures")
@RequiredArgsConstructor
public class LectureController {
    
    private final LectureService lectureService;
    
    @PostMapping("/video")
    public ResponseEntity<LectureResponse> createLecture(@Valid @RequestBody VideoLectureRequest request) {
        LectureResponse response = lectureService.createLecture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/text")
    public ResponseEntity<LectureResponse> createLecture(@Valid @RequestBody TextLectureRequest request) {
        LectureResponse response = lectureService.createLecture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping(path = "/document")
    public ResponseEntity<LectureResponse> createLecture(
            @RequestBody @Valid DocumentLectureRequest request) {
        LectureResponse response = lectureService.createLecture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/video/{id}")
    public ResponseEntity<LectureResponse> updateVideoLecture(
            @PathVariable UUID id,
            @Valid @RequestBody VideoLectureRequest request
    ){
        LectureResponse response = lectureService.updateLecture(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/document/{id}")
    public ResponseEntity<LectureResponse> updateDocumentLecture(
            @PathVariable UUID id,
            @Valid @RequestBody DocumentLectureRequest request
    ){
        LectureResponse response = lectureService.updateLecture(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/text/{id}")
    public ResponseEntity<LectureResponse> updateTextLecture(
            @PathVariable UUID id,
            @Valid @RequestBody TextLectureRequest request
    ){
        LectureResponse response = lectureService.updateLecture(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<LectureResponse> getLectureById(@PathVariable UUID id) {
        LectureResponse response = lectureService.getLectureById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/chapter/{chapterId}")
    public ResponseEntity<List<LectureResponse>> getLecturesByChapterId(
            @PathVariable UUID chapterId) {
        List<LectureResponse> response = lectureService.getLecturesByChapterId(chapterId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLecture(@PathVariable UUID id) {
        lectureService.deleteLecture(id);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/reorder")
    public ResponseEntity<List<LectureResponse>> reorderLectures(
            @Valid @RequestBody ReorderRequest request) {
        List<LectureResponse> response = lectureService.reorderLectures(request);
        return ResponseEntity.ok(response);
    }
}

