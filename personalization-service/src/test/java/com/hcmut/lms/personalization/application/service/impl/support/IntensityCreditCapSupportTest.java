package com.hcmut.lms.personalization.application.service.impl.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hcmut.lms.personalization.application.dto.response.enums.LearningIntensity;
import com.hcmut.lms.personalization.application.dto.response.enums.SummerLearningIntensity;
import org.junit.jupiter.api.Test;

class IntensityCreditCapSupportTest {

    @Test void mainSemesterCapStrict_shouldReturnCap_whenValidIntensity() {
        assertEquals(13, IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Low));
        assertEquals(15, IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Light));
        assertEquals(17, IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Standard));
        assertEquals(22, IntensityCreditCapSupport.mainSemesterCapStrict(LearningIntensity.Heavy));
    }

    @Test void mainSemesterCapStrict_shouldThrow_whenNullIntensity() {
        try {
            IntensityCreditCapSupport.mainSemesterCapStrict(null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("prefMainSemLearnIntensity"));
        }
    }

    @Test void summerSemesterCapStrict_shouldReturnCap_whenValidIntensity() {
        assertEquals(4, IntensityCreditCapSupport.summerSemesterCapStrict(SummerLearningIntensity.Light));
        assertEquals(6, IntensityCreditCapSupport.summerSemesterCapStrict(SummerLearningIntensity.Standard));
        assertEquals(8, IntensityCreditCapSupport.summerSemesterCapStrict(SummerLearningIntensity.Heavy));
    }

    @Test void summerSemesterCapStrict_shouldThrow_whenNullIntensity() {
        try {
            IntensityCreditCapSupport.summerSemesterCapStrict(null);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("learningIntensity"));
        }
    }
}
