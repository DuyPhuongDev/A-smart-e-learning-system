package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;
import com.hcmut.lms.assessment.service.AssessmentExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/execution")
@RequiredArgsConstructor
public class AssessmentExecutionController {

    private final AssessmentExecutionService executionService;

    /**
     * Submit an answer for a single question. Jackson deserializes the payload
     * into the correct SubmissionDto subtype using the "questionType" discriminator.
     *
     * MCQ:    { "questionType":"MCQ",    "questionId":"...", "studentId":"...", "selectedOptionIds":["..."] }
     * CODING: { "questionType":"CODING", "questionId":"...", "studentId":"...", "code":"...", "language":"python" }
     * ESSAY:  { "questionType":"ESSAY",  "questionId":"...", "studentId":"...", "textContent":"..." }
     */
    @PostMapping("/questions/{questionId}/submit")
    public GradingResponse submitAnswer(@PathVariable UUID questionId,
                                        @RequestBody SubmissionDto submission) {
        return executionService.submitAnswer(questionId, submission);
    }
}
