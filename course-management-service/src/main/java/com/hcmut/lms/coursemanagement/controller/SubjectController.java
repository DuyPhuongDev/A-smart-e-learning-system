package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectGradingWeightResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.ImportResultResponse;
import com.hcmut.lms.coursemanagement.application.service.SubjectService;
import com.hcmut.lms.coursemanagement.application.service.ImportService;
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
@RequestMapping("${prefix-api}/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;
    private final ImportService importService;

    @PostMapping
    public ResponseEntity<SubjectResponse> createSubject(
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubjectResponse> updateSubject(
            @PathVariable UUID id,
            @Valid @RequestBody SubjectRequest request) {
        SubjectResponse response = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponse> getSubjectById(@PathVariable UUID id) {
        SubjectResponse response = subjectService.getSubjectById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<SubjectResponse> getSubjectByCode(@PathVariable String code) {
        SubjectResponse response = subjectService.getSubjectByCode(code);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<SubjectResponse>> getAllSubjects(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "") String search) {

        PageResponse<SubjectResponse> response = subjectService.getAllSubjects(page, size, search);
        return ResponseEntity.ok(response);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable UUID id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportResultResponse> importClassSections(
            @CurrentUser CurrentUserInfo currentUser,
            @RequestPart("file") MultipartFile file,
            @RequestParam UUID semesterId) {
        ImportResultResponse result = importService
                .importClassSectionsFromExcel(file, semesterId, currentUser.getId());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/gradings")
    public ResponseEntity<List<SubjectGradingWeightResponse>> getGradingsForSubject(@PathVariable UUID id) {
        return ResponseEntity.ok(subjectService.getGradingsForSubject(id));
    }

    @PutMapping("/{id}/gradings")
    public ResponseEntity<List<SubjectGradingWeightResponse>> setGradingsForSubject(
            @PathVariable UUID id,
            @Valid @RequestBody List<SubjectGradingRequest> gradings) {
        return ResponseEntity.ok(subjectService.setGradingsForSubject(id, gradings));
    }
}
