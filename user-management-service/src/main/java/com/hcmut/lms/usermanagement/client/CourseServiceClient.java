package com.hcmut.lms.usermanagement.client;

import com.hcmut.lms.usermanagement.client.dto.DepartmentResponse;
import com.hcmut.lms.usermanagement.client.dto.SemesterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "course-management-service")
public interface CourseServiceClient {

    @GetMapping("/api/courses/internal/departments")
    DepartmentResponse getDepartmentBySpecializationId(@RequestParam UUID specializationId);

    @GetMapping("/api/courses/internal/semesters/current")
    SemesterResponse getCurrentSemester();
}
