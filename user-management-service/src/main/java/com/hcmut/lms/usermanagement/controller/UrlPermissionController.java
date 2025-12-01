package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUrlPermissionRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UrlPermissionResponse;
import com.hcmut.lms.usermanagement.service.UrlPermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/url-permissions")
@RequiredArgsConstructor
public class UrlPermissionController {
    
    private final UrlPermissionService urlPermissionService;
    
    @PostMapping
    public ResponseEntity<UrlPermissionResponse> create(@Valid @RequestBody CreateUrlPermissionRequest request) {
        UrlPermissionResponse response = urlPermissionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UrlPermissionResponse> getById(@PathVariable UUID id) {
        UrlPermissionResponse response = urlPermissionService.getById(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<UrlPermissionResponse>> getAll() {
        List<UrlPermissionResponse> responses = urlPermissionService.getAll();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/service-function/{serviceFunctionId}")
    public ResponseEntity<List<UrlPermissionResponse>> getByServiceFunctionId(@PathVariable UUID serviceFunctionId) {
        List<UrlPermissionResponse> responses = urlPermissionService.getByServiceFunctionId(serviceFunctionId);
        return ResponseEntity.ok(responses);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UrlPermissionResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateUrlPermissionRequest request) {
        UrlPermissionResponse response = urlPermissionService.update(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        urlPermissionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

