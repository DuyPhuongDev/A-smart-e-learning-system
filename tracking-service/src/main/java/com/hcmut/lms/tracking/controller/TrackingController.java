package com.hcmut.lms.tracking.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tracking")
public class TrackingController {

    @GetMapping("/progress/{studentId}")
    public String getStudentProgress(@PathVariable String studentId) {
        // TODO: Implement get student progress logic
        return "Get student progress - To be implemented";
    }

    @GetMapping("/grades/{studentId}")
    public String getStudentGrades(@PathVariable String studentId) {
        // TODO: Implement get student grades logic
        return "Get student grades - To be implemented";
    }

    @PostMapping("/activity-log")
    public String logActivity() {
        // TODO: Implement log activity logic
        return "Log activity - To be implemented";
    }

    @GetMapping("/activity-log/{studentId}")
    public String getActivityLogs(@PathVariable String studentId) {
        // TODO: Implement get activity logs logic
        return "Get activity logs - To be implemented";
    }

    @GetMapping("/dashboard/{studentId}")
    public String getPersonalDashboard(@PathVariable String studentId) {
        // TODO: Implement get personal dashboard logic
        return "Get personal dashboard - To be implemented";
    }
}
