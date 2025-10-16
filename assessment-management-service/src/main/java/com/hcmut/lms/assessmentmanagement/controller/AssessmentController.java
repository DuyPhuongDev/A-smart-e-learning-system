package com.hcmut.lms.assessmentmanagement.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {

    @PostMapping
    public String createAssessment() {
        // TODO: Implement create assessment logic
        return "Create assessment - To be implemented";
    }

    @PutMapping("/{id}")
    public String updateAssessment(@PathVariable String id) {
        // TODO: Implement update assessment logic
        return "Update assessment - To be implemented";
    }

    @PostMapping("/{id}/grade")
    public String gradeAssessment(@PathVariable String id) {
        // TODO: Implement grade assessment logic
        return "Grade assessment - To be implemented";
    }

    @PostMapping("/{id}/feedback")
    public String provideFeedback(@PathVariable String id) {
        // TODO: Implement provide feedback logic
        return "Provide feedback - To be implemented";
    }

    @GetMapping("/{id}/submissions")
    public String getSubmissions(@PathVariable String id) {
        // TODO: Implement get submissions logic
        return "Get submissions - To be implemented";
    }
}

