package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/personalization")
public class PersonalizationController {

    @PostMapping("/goals")
    public ResponseDto<String> setLearningGoals() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Set learning goals - To be implemented")
                .build();
    }

    @PutMapping("/goals/{id}")
    public ResponseDto<String> updateLearningGoals(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Update learning goals - To be implemented")
                .build();
    }

    @GetMapping("/learning-path")
    public ResponseDto<String> getLearningPath() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get learning path - To be implemented")
                .build();
    }

    @PostMapping("/learning-path/generate")
    public ResponseDto<String> generatePersonalizedPath() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Generate personalized learning path with AI - To be implemented")
                .build();
    }

    @PutMapping("/learning-path/adjust")
    public ResponseDto<String> adjustLearningPath() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Adjust learning path - To be implemented")
                .build();
    }

    @GetMapping("/schedule")
    public ResponseDto<String> getAcademicSchedule() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get academic schedule - To be implemented")
                .build();
    }
}

