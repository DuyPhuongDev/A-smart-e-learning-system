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

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/class-sections")
@RequiredArgsConstructor
public class ClassSectionController {
    
    private final ClassSectionService classSectionService;
    
    @PostMapping
    public ResponseEntity<ClassSectionResponse> createClassSection(
            @CurrentUser CurrentUserInfo currentUser,
            @Valid @RequestBody ClassSectionRequest request) {
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
    public ResponseEntity<PageResponse<ClassSectionResponse>> getAllClassSections(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) UUID teacherId) {
        PageResponse<ClassSectionResponse> response = classSectionService.getAllClassSections(
                page, size, semester, teacherId);
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
    public ResponseEntity<PageResponse<ClassSectionResponse>> getMyClassSections(
                @CurrentUser CurrentUserInfo currentUserInfo,
                @RequestParam(required = false, defaultValue = "0") Integer page,
                @RequestParam(required = false, defaultValue = "10") Integer size,
                @RequestParam(required = false) String semester) {
        PageResponse<ClassSectionResponse> response = classSectionService.getClassSectionsByTeacherId(
                currentUserInfo.getId(), page, size, semester);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/menu")
    public ResponseEntity<CourseMenuResponse> getCourseMenu(@PathVariable UUID id) {
        CourseMenuResponse response = classSectionService.getCourseMenu(id);
        return ResponseEntity.ok(response);
    }
}

