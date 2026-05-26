package com.hcmut.lms.personalization.application.service.impl.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HistoricalDataAnalyzerServiceTest {

    private final HistoricalDataAnalyzerService service = new HistoricalDataAnalyzerService();

    @Test void analyze_shouldReturnResult_whenNullSpecialization() {
        Map<String, Object> result = service.analyze(null, BigDecimal.ZERO, 0);
        assertTrue(result.containsKey("method"));
        assertTrue(result.containsKey("probabilityScore"));
    }

    @Test void analyze_shouldReturnResult_whenValidInput() {
        Map<String, Object> result = service.analyze("CS", BigDecimal.valueOf(3.0), 4);
        assertTrue(result.containsKey("method"));
        assertTrue(result.get("method").equals("historical_unimplemented"));
    }
}
