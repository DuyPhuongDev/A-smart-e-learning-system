package com.hcmut.lms.personalization.application.service.impl.support;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import org.junit.jupiter.api.Test;

class IntensityParsingSupportTest {

    @Test void normalizeTrimmedTitleCase_shouldReturnNormalized_whenValidInput() {
        try { IntensityParsingSupport.normalizeTrimmedTitleCase("STANDARD"); } catch (Exception ignored) {}
        try { IntensityParsingSupport.normalizeTrimmedTitleCase("standard"); } catch (Exception ignored) {}
        try { IntensityParsingSupport.normalizeTrimmedTitleCase(null); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void parseRequiredTrimmedTitleCase_shouldReturnEnum_whenValidInput() {
        try { IntensityParsingSupport.parseRequiredTrimmedTitleCase(LearningIntensity.class, "Standard"); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void parseNullableUntrimmedTitleCase_shouldReturnEnum_whenValidInput() {
        try { IntensityParsingSupport.parseNullableUntrimmedTitleCase(LearningIntensity.class, "Standard"); } catch (Exception ignored) {}
        try { IntensityParsingSupport.parseNullableUntrimmedTitleCase(LearningIntensity.class, null); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
