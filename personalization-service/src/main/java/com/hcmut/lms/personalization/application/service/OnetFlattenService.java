package com.hcmut.lms.personalization.application.service;

import com.hcmut.lms.personalization.application.dto.request.BackfillRequirementEmbeddingsRequest;
import com.hcmut.lms.personalization.application.dto.request.FlattenOccupationRequest;
import com.hcmut.lms.personalization.application.dto.response.BackfillRequirementEmbeddingsResponse;
import com.hcmut.lms.personalization.application.dto.response.FlattenOccupationJobResponse;

import java.util.List;

public interface OnetFlattenService {
    List<FlattenOccupationJobResponse> flatten(FlattenOccupationRequest request);

    BackfillRequirementEmbeddingsResponse backfillRequirementEmbeddings(BackfillRequirementEmbeddingsRequest request);

    void backfillRequirementEmbeddingsAsync(BackfillRequirementEmbeddingsRequest request);
}
