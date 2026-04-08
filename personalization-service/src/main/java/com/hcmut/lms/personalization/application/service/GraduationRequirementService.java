package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.UpdateGraduationRequirementStatusRequest;
import com.hcmut.lms.personalization.application.dto.response.GraduationRequirementStatusResponse;

import java.util.List;
import java.util.UUID;

public interface GraduationRequirementService {

  List<GraduationRequirementStatusResponse> getMyGraduationRequirements(UUID studentId);

  GraduationRequirementStatusResponse updateGraduationRequirementStatus(
      UUID studentId,
      UUID graduationRequirementStatusId, UpdateGraduationRequirementStatusRequest request);
}
