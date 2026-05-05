package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationSubjectScoreResponse;

import java.util.List;
import java.util.UUID;

public interface OccupationService {
  PageResponse<OccupationResponse> getOccupations(int page, int size, String keyword);

  List<OccupationResponse> getAllOccupations();

  OccupationDetailResponse getOccupationDetail(String occupationCode);

  List<OccupationSubjectScoreResponse> getOccupationSubjects(UUID occupationId);
}
