package com.hcmut.lms.coursemanagement.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @GetMapping
    public String getAllCourses() {
        // TODO: Implement get all courses logic
        return "Get all courses - To be implemented";
    }

    @GetMapping("/{id}")
    public String getCourseById(@PathVariable String id) {
        // TODO: Implement get course by ID logic
        return "Get course by ID - To be implemented";
    }

    @PostMapping
    public String createCourse() {
        // TODO: Implement create course logic
        return "Create course - To be implemented";
    }

    @PutMapping("/{id}")
    public String updateCourse(@PathVariable String id) {
        // TODO: Implement update course logic
        return "Update course - To be implemented";
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable String id) {
        // TODO: Implement delete course logic
    }

    @PostMapping("/{id}/structure")
    public String buildCourseStructure(@PathVariable String id) {
        // TODO: Implement build course structure logic
        return "Build course structure - To be implemented";
    }

    @PostMapping("/{id}/materials")
    public String uploadMaterials(@PathVariable String id) {
        // TODO: Implement upload materials logic
        return "Upload materials - To be implemented";
    }
}

