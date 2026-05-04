package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.dto.request.student.RunTestcaseRequest;
import com.hcmut.lms.assessment.dto.request.student.SaveAnswerRequest;
import com.hcmut.lms.assessment.dto.response.student.*;
import com.hcmut.lms.assessment.service.AssessmentExecutionService;
import com.hcmut.lms.assessment.service.StudentAssessmentService;
import com.hcmut.lms.assessment.service.judge.CppJudgeService;
import com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/student")
@RequiredArgsConstructor
public class StudentAssessmentController {

    private final StudentAssessmentService studentAssessmentService;
    private final AssessmentExecutionService assessmentExecutionService;

    @GetMapping("/courses/{courseId}/assessments")
    public PageResponse<StudentCourseAssessmentResponse> listCourseAssessments(
            @PathVariable UUID courseId,
            @CurrentUser CurrentUserInfo currentUser,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return studentAssessmentService.listCourseAssessments(
                courseId,
                currentUser.getId(),
                authorizationHeader,
                page,
                size
        );
    }

    @PostMapping("/assessments/{assessmentId}/attempts")
    @ResponseStatus(HttpStatus.CREATED)
    public StartAttemptResponse startAttempt(
            @PathVariable UUID assessmentId,
            @CurrentUser CurrentUserInfo currentUser,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return studentAssessmentService.startAttempt(assessmentId, currentUser.getId(), authorizationHeader);
    }

    @GetMapping("/attempts/{attemptId}")
    public AttemptDetailResponse getAttemptDetail(
            @PathVariable UUID attemptId,
            @CurrentUser CurrentUserInfo currentUser
    ) {
        return studentAssessmentService.getAttemptDetail(attemptId, currentUser.getId());
    }

    @PutMapping("/attempts/{attemptId}/answers/{questionId}")
    public SaveAnswerResponse saveAnswer(
            @PathVariable UUID attemptId,
            @PathVariable UUID questionId,
            @CurrentUser CurrentUserInfo currentUser,
            @Valid @RequestBody SaveAnswerRequest request
    ) {
        return studentAssessmentService.saveAnswer(attemptId, questionId, currentUser.getId(), request);
    }

    @PostMapping("/attempts/{attemptId}/submit")
    public SubmitAttemptResponse submitAttempt(
            @PathVariable UUID attemptId,
            @CurrentUser CurrentUserInfo currentUser
    ) {
        return studentAssessmentService.submitAttempt(attemptId, currentUser.getId());
    }

    @GetMapping("/assessments/{assessmentId}/my-attempts")
    public List<StudentAttemptSummaryResponse> getMyAttempts(
            @PathVariable UUID assessmentId,
            @CurrentUser CurrentUserInfo currentUser,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return studentAssessmentService.getMyAttempts(assessmentId, currentUser.getId(), authorizationHeader);
    }

    @GetMapping("/attempts/{attemptId}/result")
    public SubmitAttemptResponse getAttemptResult(
            @PathVariable UUID attemptId,
            @CurrentUser CurrentUserInfo currentUser
    ) {
        return studentAssessmentService.getAttemptResult(attemptId, currentUser.getId());
    }

    @PostMapping("/coding-questions/{questionId}/run")
    public CodingJudgeEvaluation runTestcase(@PathVariable UUID questionId, @RequestBody RunTestcaseRequest request) {
        return assessmentExecutionService.preCheckTestcase(questionId,  request);
    }
}
