package com.hcmut.lms.learning.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Controller for Content Delivery operations
 * Handles course materials delivery, notes, and AI-assisted learning
 * Merged from course-delivery-service
 */
@RestController
@RequestMapping("/api/delivery")
public class ContentDeliveryController {

    @GetMapping("/course/{courseId}/materials")
    public String getCourseMaterials(@PathVariable String courseId) {
        // TODO: Implement get course materials logic
        return "Get course materials - To be implemented";
    }

    @PostMapping("/notes")
    public String createNote() {
        // TODO: Implement create note logic
        return "Create note - To be implemented";
    }

    @PostMapping("/ai/quiz-generation")
    public String generateQuizByAI() {
        // TODO: Implement generate quiz by AI logic
        return "Generate quiz by AI - To be implemented";
    }

    @PostMapping("/ai/qa")
    public String askQuestionToAI() {
        // TODO: Implement ask question to AI logic
        return "Ask question to AI - To be implemented";
    }

    @GetMapping("/course/{courseId}/progress")
    public String getCourseProgress(@PathVariable String courseId) {
        // TODO: Implement get course progress logic
        return "Get course progress - To be implemented";
    }
}


