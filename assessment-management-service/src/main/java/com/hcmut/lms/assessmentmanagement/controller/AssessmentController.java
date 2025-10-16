package com.hcmut.lms.assessmentmanagement.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @PostMapping
    public ResponseDto<String> createAssessment() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create assessment - To be implemented")
                .build();
    }

    @PutMapping("/{id}")
    public ResponseDto<String> updateAssessment(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Update assessment - To be implemented")
                .build();
    }

    @PostMapping("/{id}/grade")
    public ResponseDto<String> gradeAssessment(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Grade assessment - To be implemented")
                .build();
    }

    @PostMapping("/{id}/feedback")
    public ResponseDto<String> provideFeedback(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Provide feedback - To be implemented")
                .build();
    }

    @GetMapping("/{id}/submissions")
    public ResponseDto<String> getSubmissions(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get submissions - To be implemented")
                .build();
    }
}

