package com.hcmut.lms.personalization.application.service.impl.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class HistoricalDataAnalyzerService {

    public Map<String, Object> analyze(
        String specializationId,
        BigDecimal targetGpa,
        int remainingSemesters
    ) {
        log.info("Analyzing historical data for specialization {}", specializationId);

        Map<String, Object> analysis = new HashMap<>();
        analysis.put("method", "historical_unimplemented");
        analysis.put("probabilityScore", null);
        analysis.put("sampleSize", 0);
        analysis.put("note", "Historical analysis is not yet implemented - probability score unavailable");

        return analysis;
    }
}
