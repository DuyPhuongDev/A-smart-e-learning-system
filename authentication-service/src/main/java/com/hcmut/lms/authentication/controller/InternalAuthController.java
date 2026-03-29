package com.hcmut.lms.authentication.controller;

import com.hcmut.lms.authentication.model.dto.request.CreateCredentialsRequest;
import com.hcmut.lms.authentication.model.dto.request.UpdateEmailRequest;
import com.hcmut.lms.authentication.model.dto.request.UserIdRequest;
import com.hcmut.lms.authentication.service.InternalAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * Internal Authentication Controller
 * Handles internal service-to-service communication
 * These endpoints should NOT be exposed through API Gateway
 */
@RestController
@RequestMapping("/api/auth/internal")
@RequiredArgsConstructor
public class InternalAuthController {

    private final InternalAuthService internalAuthService;

    @PostMapping("/create-credentials")
    @ResponseStatus(HttpStatus.CREATED)
    public void createCredentials(@Valid @RequestBody CreateCredentialsRequest request) {
        internalAuthService.createCredentials(request);
    }

    @PostMapping("/lock-account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void lockAccount(@Valid @RequestBody UserIdRequest request) {
        internalAuthService.lockAccount(request.getUserId());
    }

    @PostMapping("/unlock-account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unlockAccount(@Valid @RequestBody UserIdRequest request) {
        internalAuthService.unlockAccount(request.getUserId());
    }
    
    @PostMapping("/update-email")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEmail(@Valid @RequestBody UpdateEmailRequest request) {
        internalAuthService.updateEmail(request);
    }
}


