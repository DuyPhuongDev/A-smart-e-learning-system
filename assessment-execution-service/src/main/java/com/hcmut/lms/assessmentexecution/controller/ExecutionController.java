package com.hcmut.lms.assessmentexecution.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/execution")
public class ExecutionController {

    @GetMapping("/quiz/{id}")
    public ResponseDto<String> getQuiz(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get quiz - To be implemented")
                .build();
    }

    @PostMapping("/quiz/{id}/submit")
    public ResponseDto<String> submitQuiz(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Submit quiz - To be implemented")
                .build();
    }

    @PostMapping("/assignment/{id}/submit")
    public ResponseDto<String> submitAssignment(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Submit assignment - To be implemented")
                .build();
    }

    @PostMapping("/code/{id}/execute")
    public ResponseDto<String> executeCode(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Execute code - To be implemented")
                .build();
    }

    @GetMapping("/results/{id}")
    public ResponseDto<String> getResults(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get results - To be implemented")
                .build();
    }
}

