package com.hcmut.lms.personalization.application.dto.record;

import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;

import java.util.Map;
import java.util.UUID;

public record CurriculumEnrichmentData(Map<UUID, CurriculumFullResponse.CurriculumSubjectFull> subjectMap,
                                       Integer earnedCredits, Integer requiredCredits) {
  public static CurriculumEnrichmentData empty() {
    return new CurriculumEnrichmentData(Map.of(), null, null);
  }
}
