package com.hcmut.lms.coursedelivery.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/delivery")
public class DeliveryController {

    @GetMapping("/course/{courseId}/materials")
    public ResponseDto<String> getCourseMaterials(@PathVariable String courseId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get course materials - To be implemented")
                .build();
    }

    @PostMapping("/notes")
    public ResponseDto<String> createNote() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create note - To be implemented")
                .build();
    }

    @PostMapping("/ai/quiz-generation")
    public ResponseDto<String> generateQuizByAI() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Generate quiz by AI - To be implemented")
                .build();
    }

    @PostMapping("/ai/qa")
    public ResponseDto<String> askQuestionToAI() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Ask question to AI - To be implemented")
                .build();
    }

    @GetMapping("/course/{courseId}/progress")
    public ResponseDto<String> getCourseProgress(@PathVariable String courseId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get course progress - To be implemented")
                .build();
    }
}

