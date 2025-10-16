package com.hcmut.lms.enrollment.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    @PostMapping("/enroll")
    public ResponseDto<String> enrollInCourse() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Enroll in course - To be implemented")
                .build();
    }

    @GetMapping("/discover")
    public ResponseDto<String> discoverCourses() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Discover courses - To be implemented")
                .build();
    }

    @GetMapping("/search")
    public ResponseDto<String> searchCourses(@RequestParam(required = false) String keyword) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Search courses - To be implemented")
                .build();
    }

    @GetMapping("/course/{id}")
    public ResponseDto<String> getCourseInfo(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get course info - To be implemented")
                .build();
    }

    @PostMapping("/course/{id}/rate")
    public ResponseDto<String> rateCourse(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Rate course - To be implemented")
                .build();
    }

    @GetMapping("/my-courses/{studentId}")
    public ResponseDto<String> getEnrolledCourses(@PathVariable String studentId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get enrolled courses - To be implemented")
                .build();
    }
}

