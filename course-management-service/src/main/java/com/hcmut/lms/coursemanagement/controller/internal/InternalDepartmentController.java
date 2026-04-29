package com.hcmut.lms.coursemanagement.controller.internal;


import com.hcmut.lms.coursemanagement.application.dto.response.DepartmentResponse;
import com.hcmut.lms.coursemanagement.application.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses/internal/departments")
@RequiredArgsConstructor
public class InternalDepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public DepartmentResponse getDepartmentBySpecializationId(@RequestParam UUID specializationId) {
        return departmentService.getDepartmentBySpecialization(specializationId);
    }
}
