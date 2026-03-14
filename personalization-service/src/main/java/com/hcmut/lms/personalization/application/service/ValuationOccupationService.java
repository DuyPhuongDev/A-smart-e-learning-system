package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.ValuationOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.ValuationSubjectPersistenceResponse;

import java.util.concurrent.CompletableFuture;

public interface ValuationOccupationService {
    CompletableFuture<ValuationSubjectPersistenceResponse> valuateAsync(ValuationOccupationRequest request);
}
