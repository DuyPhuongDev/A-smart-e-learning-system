package com.hcmut.lms.coursemanagement.client;

import com.hcmut.lms.coursemanagement.client.dto.AssessmentGrade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
public class AssessmentServiceClientFallback implements AssessmentServiceClient {
    @Override
    public List<AssessmentGrade> getGradesByClass(UUID id) {
        log.error("FALLBACK FALLBACK FALLBACK FALLBACK");
        throw new RuntimeException("FALLBACK FALLBACK FALLBACK FALLBACK");
    }
}
