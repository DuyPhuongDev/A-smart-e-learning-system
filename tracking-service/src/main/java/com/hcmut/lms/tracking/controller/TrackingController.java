package com.hcmut.lms.tracking.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @GetMapping("/progress/{studentId}")
    public ResponseDto<String> getStudentProgress(@PathVariable String studentId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get student progress - To be implemented")
                .build();
    }

    @GetMapping("/grades/{studentId}")
    public ResponseDto<String> getStudentGrades(@PathVariable String studentId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get student grades - To be implemented")
                .build();
    }

    @PostMapping("/activity-log")
    public ResponseDto<String> logActivity() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Log activity - To be implemented")
                .build();
    }

    @GetMapping("/activity-log/{studentId}")
    public ResponseDto<String> getActivityLogs(@PathVariable String studentId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get activity logs - To be implemented")
                .build();
    }

    @GetMapping("/dashboard/{studentId}")
    public ResponseDto<String> getPersonalDashboard(@PathVariable String studentId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get personal dashboard - To be implemented")
                .build();
    }
}

