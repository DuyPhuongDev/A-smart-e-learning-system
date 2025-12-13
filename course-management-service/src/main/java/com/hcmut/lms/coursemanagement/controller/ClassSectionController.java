package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.TeacherAssignRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseMenuResponse;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/class-sections")
@RequiredArgsConstructor
public class ClassSectionController {
    
    private final ClassSectionService classSectionService;
    
    @PostMapping
    public ResponseEntity<ClassSectionResponse> createClassSection(
//            @CurrentUser CurrentUserInfo currentUser,
            @Valid @RequestBody ClassSectionRequest request) {
        // test
        CurrentUserInfo currentUser = CurrentUserInfo.builder()
                .id(UUID.fromString("811ba53a-0eb3-4cc3-a6a2-49214ca25b18"))
                .role("TEACHER")
                .build();
        ClassSectionResponse response = classSectionService.createClassSection(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ClassSectionResponse> updateClassSection(
            @PathVariable UUID id,
            @Valid @RequestBody ClassSectionRequest requestDTO) {
        ClassSectionResponse response = classSectionService.updateClassSection(id, requestDTO);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ClassSectionResponse> getClassSectionById(@PathVariable UUID id) {
        ClassSectionResponse response = classSectionService.getClassSectionById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllClassSections(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<ClassSectionResponse> response = classSectionService.getAllClassSections(page, size);
            return ResponseEntity.ok(response);
        }
        List<ClassSectionResponse> response = classSectionService.getAllClassSections();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getClassSectionsBySubjectId(
            @PathVariable UUID subjectId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<ClassSectionResponse> response = classSectionService.getClassSectionsBySubjectId(subjectId, page, size);
            return ResponseEntity.ok(response);
        }
        List<ClassSectionResponse> response = classSectionService.getClassSectionsBySubjectId(subjectId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/semester/{semesterId}")
    public ResponseEntity<?> getClassSectionsBySemesterId(
            @PathVariable UUID semesterId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<ClassSectionResponse> response = classSectionService.getClassSectionsBySemesterId(semesterId, page, size);
            return ResponseEntity.ok(response);
        }
        List<ClassSectionResponse> response = classSectionService.getClassSectionsBySemesterId(semesterId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<?> getClassSectionsByTeacherId(
            @PathVariable UUID teacherId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<ClassSectionResponse> response = classSectionService.getClassSectionsByTeacherId(teacherId, page, size);
            return ResponseEntity.ok(response);
        }
        List<ClassSectionResponse> response = classSectionService.getClassSectionsByTeacherId(teacherId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClassSection(@PathVariable UUID id) {
        classSectionService.deleteClassSection(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/assign-teacher/{id}")
    public ResponseEntity<ClassSectionResponse> assignTeacherToClassSection(@PathVariable UUID id, @RequestBody TeacherAssignRequest request) {
        ClassSectionResponse response = classSectionService.assignTeacherToClassSection(id, request.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-classes")
    public ResponseEntity<?> getMyClassSections(
                @CurrentUser CurrentUserInfo currentUserInfo,
                @RequestParam(required = false) Integer page,
                @RequestParam(required = false) Integer size) {

            if (page != null && size != null) {
                PageResponse<ClassSectionResponse> response = classSectionService.getClassSectionsByTeacherId(currentUserInfo.getId(), page, size);
                return ResponseEntity.ok(response);
            }
            List<ClassSectionResponse> response = classSectionService.getClassSectionsByTeacherId(currentUserInfo.getId());
            return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/menu")
    public ResponseEntity<CourseMenuResponse> getCourseMenu(@PathVariable UUID id) {
        CourseMenuResponse response = classSectionService.getCourseMenu(id);
        return ResponseEntity.ok(response);
    }
}

