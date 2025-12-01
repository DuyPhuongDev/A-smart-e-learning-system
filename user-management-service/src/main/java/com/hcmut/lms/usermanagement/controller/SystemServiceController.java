package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.CreateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateSystemServiceRequest;
import com.hcmut.lms.usermanagement.model.dto.response.SystemServiceResponse;
import com.hcmut.lms.usermanagement.service.SystemServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/system-services")
@RequiredArgsConstructor
public class SystemServiceController {
    
    private final SystemServiceService systemServiceService;
    
    @PostMapping
    public ResponseEntity<SystemServiceResponse> create(@Valid @RequestBody CreateSystemServiceRequest request) {
        SystemServiceResponse response = systemServiceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<SystemServiceResponse> getById(@PathVariable UUID id) {
        SystemServiceResponse response = systemServiceService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<SystemServiceResponse>> getAll() {
        List<SystemServiceResponse> responses = systemServiceService.getAll();
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<SystemServiceResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateSystemServiceRequest request) {
        SystemServiceResponse response = systemServiceService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        systemServiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

