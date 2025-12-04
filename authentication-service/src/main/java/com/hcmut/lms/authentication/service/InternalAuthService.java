package com.hcmut.lms.authentication.service;

import com.hcmut.lms.authentication.model.dto.request.CreateCredentialsRequest;
import com.hcmut.lms.authentication.model.dto.request.UpdateEmailRequest;

import java.util.UUID;

public interface InternalAuthService {
    void createCredentials(CreateCredentialsRequest request);
    void lockAccount(UUID userId);
    void unlockAccount(UUID userId);
    void updateEmail(UpdateEmailRequest request);
}

