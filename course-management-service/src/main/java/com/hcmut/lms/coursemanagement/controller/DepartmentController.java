package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.DepartmentRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.DepartmentResponse;
import com.hcmut.lms.coursemanagement.application.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    
    private final DepartmentService departmentService;
    
    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable UUID id,
            @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable UUID id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<?> getAllDepartments(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<DepartmentResponse> response = departmentService.getAllDepartments(page, size);
            return ResponseEntity.ok(response);
        }
        List<DepartmentResponse> response = departmentService.getAllDepartments();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/faculty/{facultyId}")
    public ResponseEntity<?> getDepartmentsByFacultyId(
            @PathVariable UUID facultyId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null && size != null) {
            PageResponse<DepartmentResponse> response = departmentService.getDepartmentsByFacultyId(facultyId, page, size);
            return ResponseEntity.ok(response);
        }
        List<DepartmentResponse> response = departmentService.getDepartmentsByFacultyId(facultyId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}

