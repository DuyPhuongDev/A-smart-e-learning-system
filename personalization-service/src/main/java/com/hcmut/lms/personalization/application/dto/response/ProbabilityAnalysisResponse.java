package com.hcmut.lms.personalization.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hcmut.lms.personalization.application.dto.response.enums.ProbabilityMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProbabilityAnalysisResponse {

  private ProbabilityMethod method;

  private ProbabilityAnalysisDetailsResponse details;

  public static class ProbabilityAnalysisResponseBuilder {
    public ProbabilityAnalysisResponseBuilder details(ProbabilityAnalysisDetailsResponse details) {
      this.details = details;
      return this;
    }

    // Backward compatibility: supports old callers that still pass raw map details.
    public ProbabilityAnalysisResponseBuilder details(Map<String, Object> rawDetails) {
      if (rawDetails == null) {
        this.details = null;
        return this;
      }

      this.details = ProbabilityAnalysisDetailsResponse.builder()
          .probabilityScore(getDouble(rawDetails.get("probabilityScore")))
          .note(getString(rawDetails.get("note")))
          .sampleSize(getInteger(rawDetails.get("sampleSize")))
          .predictedFinalGpa(getDouble(rawDetails.get("predictedFinalGpa")))
          .standardDeviation(getDouble(rawDetails.get("standardDeviation")))
          .predictionsCount(getInteger(rawDetails.get("predictionsCount")))
          .build();
      return this;
    }

    private static Double getDouble(Object value) {
      return value instanceof Number number ? number.doubleValue() : null;
    }

    private static Integer getInteger(Object value) {
      return value instanceof Number number ? number.intValue() : null;
    }

    private static String getString(Object value) {
      return value != null ? value.toString() : null;
    }
  }
}
