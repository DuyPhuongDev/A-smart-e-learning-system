package com.hcmut.lms.learning.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Controller for Enrollment operations
 * Handles course enrollment, discovery, search, and ratings
 * Merged from enrollment-service
 */
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    @PostMapping("/enroll")
    public String enrollInCourse() {
        // TODO: Implement enroll in course logic
        return "Enroll in course - To be implemented";
    }

    @GetMapping("/discover")
    public String discoverCourses() {
        // TODO: Implement discover courses logic
        return "Discover courses - To be implemented";
    }

    @GetMapping("/search")
    public String searchCourses(@RequestParam(required = false) String keyword) {
        // TODO: Implement search courses logic
        return "Search courses - To be implemented";
    }

    @GetMapping("/course/{id}")
    public String getCourseInfo(@PathVariable String id) {
        // TODO: Implement get course info logic
        return "Get course info - To be implemented";
    }

    @PostMapping("/course/{id}/rate")
    public String rateCourse(@PathVariable String id) {
        // TODO: Implement rate course logic
        return "Rate course - To be implemented";
    }

    @GetMapping("/my-courses/{studentId}")
    public String getEnrolledCourses(@PathVariable String studentId) {
        // TODO: Implement get enrolled courses logic
        return "Get enrolled courses - To be implemented";
    }
}


