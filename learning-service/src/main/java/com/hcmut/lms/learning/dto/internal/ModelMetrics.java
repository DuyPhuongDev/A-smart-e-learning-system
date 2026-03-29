package com.hcmut.lms.learning.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ModelMetrics {
    private TestMetrics test;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TestMetrics {
        @JsonProperty("error_distribution")
        private ErrorDistribution errorDistribution;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ErrorDistribution {
        @JsonProperty("mu_error")
        private Double muError;    // Mean of residuals R = g_hat4 - g4 (bias correction)

        @JsonProperty("sigma_error")
        private Double sigmaError; // Std dev of residuals (uncertainty spread)
    }
}
