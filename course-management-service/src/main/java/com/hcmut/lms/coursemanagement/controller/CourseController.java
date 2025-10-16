package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @GetMapping
    public ResponseDto<String> getAllCourses() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get all courses - To be implemented")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDto<String> getCourseById(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get course by ID - To be implemented")
                .build();
    }

    @PostMapping
    public ResponseDto<String> createCourse() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create course - To be implemented")
                .build();
    }

    @PutMapping("/{id}")
    public ResponseDto<String> updateCourse(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Update course - To be implemented")
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDto<String> deleteCourse(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Delete course - To be implemented")
                .build();
    }

    @PostMapping("/{id}/structure")
    public ResponseDto<String> buildCourseStructure(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Build course structure - To be implemented")
                .build();
    }

    @PostMapping("/{id}/materials")
    public ResponseDto<String> uploadMaterials(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Upload materials - To be implemented")
                .build();
    }
}

