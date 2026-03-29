package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.CreateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateServiceFunctionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ServiceFunctionResponse;
import com.hcmut.lms.usermanagement.service.ServiceFunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-functions")
@RequiredArgsConstructor
public class ServiceFunctionController {
    
    private final ServiceFunctionService serviceFunctionService;
    
    @PostMapping
    public ResponseEntity<ServiceFunctionResponse> create(@Valid @RequestBody CreateServiceFunctionRequest request) {
        ServiceFunctionResponse response = serviceFunctionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ServiceFunctionResponse> getById(@PathVariable UUID id) {
        ServiceFunctionResponse response = serviceFunctionService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ServiceFunctionResponse>> getAll() {
        List<ServiceFunctionResponse> responses = serviceFunctionService.getAll();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/system-service/{systemServiceId}")
    public ResponseEntity<List<ServiceFunctionResponse>> getBySystemServiceId(@PathVariable UUID systemServiceId) {
        List<ServiceFunctionResponse> responses = serviceFunctionService.getBySystemServiceId(systemServiceId);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ServiceFunctionResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateServiceFunctionRequest request) {
        ServiceFunctionResponse response = serviceFunctionService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        serviceFunctionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

