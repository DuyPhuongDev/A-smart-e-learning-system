package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;

import java.util.List;

public interface OccupationService {
  PageResponse<OccupationResponse> getOccupations(int page, int size, String keyword);

  List<OccupationResponse> getAllOccupations();

  OccupationDetailResponse getOccupationDetail(String occupationCode);
}
