package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.*;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.service.LectureService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lectures")
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

    @PostMapping(path = "/document", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LectureResponse> createLecture(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("lecture-data") @Valid DocumentLectureRequest request) {
        request.setFile(file);
        LectureResponse response = lectureService.createLecture(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<LectureResponse> updateLecture(
            @PathVariable UUID id,
            @Valid @RequestBody BaseLectureRequest request) {
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
            @Valid @RequestBody ReorderListRequest request) {
        List<LectureResponse> response = lectureService.reorderLectures(request);
        return ResponseEntity.ok(response);
    }
}

