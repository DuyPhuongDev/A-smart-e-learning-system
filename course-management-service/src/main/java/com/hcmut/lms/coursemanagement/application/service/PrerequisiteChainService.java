package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.PrerequisiteChainRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.PrerequisiteChainResponse;

public interface PrerequisiteChainService {
    PrerequisiteChainResponse calculatePrerequisiteChain(PrerequisiteChainRequest request);
}
