package com.hcmut.lms.assessment.controller;

import org.springframework.web.bind.annotation.*;

/**
 * Controller for Assessment Execution operations
 * Handles quiz/assignment submissions and code execution
 * Merged from assessment-execution-service
 */
@RestController
@RequestMapping("/api/execution")
public class AssessmentExecutionController {

    @GetMapping("/quiz/{id}")
    public String getQuiz(@PathVariable String id) {
        // TODO: Implement get quiz logic
        return "Get quiz - To be implemented";
    }

    @PostMapping("/quiz/{id}/submit")
    public String submitQuiz(@PathVariable String id) {
        // TODO: Implement submit quiz logic
        return "Submit quiz - To be implemented";
    }

    @PostMapping("/assignment/{id}/submit")
    public String submitAssignment(@PathVariable String id) {
        // TODO: Implement submit assignment logic
        return "Submit assignment - To be implemented";
    }

    @PostMapping("/code/{id}/execute")
    public String executeCode(@PathVariable String id) {
        // TODO: Implement execute code logic
        return "Execute code - To be implemented";
    }

    @GetMapping("/results/{id}")
    public String getResults(@PathVariable String id) {
        // TODO: Implement get results logic
        return "Get results - To be implemented";
    }
}


