package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;
import com.hcmut.lms.assessment.service.AssessmentExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/execution")
@RequiredArgsConstructor
public class AssessmentExecutionController {

    private final AssessmentExecutionService executionService;

    // pre-check for coding question
//    @PostMapping("/questions/{codingId}/pre-check")
//    public GradingResponse submitAnswer(@PathVariable UUID codingId,
//                                        @RequestBody SubmissionDto submission) {
//        return executionService.submitAnswer(codingId, submission);
//    }
}