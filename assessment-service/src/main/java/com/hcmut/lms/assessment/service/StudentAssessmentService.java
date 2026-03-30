package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.request.student.SaveAnswerRequest;
import com.hcmut.lms.assessment.dto.response.student.AttemptDetailResponse;
import com.hcmut.lms.assessment.dto.response.student.SaveAnswerResponse;
import com.hcmut.lms.assessment.dto.response.student.StartAttemptResponse;
import com.hcmut.lms.assessment.dto.response.student.StudentAttemptSummaryResponse;
import com.hcmut.lms.assessment.dto.response.student.StudentCourseAssessmentResponse;
import com.hcmut.lms.assessment.dto.response.student.SubmitAttemptResponse;
import com.hcmut.lms.common.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface StudentAssessmentService {

    PageResponse<StudentCourseAssessmentResponse> listCourseAssessments(
            UUID courseId,
            UUID studentId,
            String authorizationHeader,
            int page,
            int size
    );

    StartAttemptResponse startAttempt(UUID assessmentId, UUID studentId, String authorizationHeader);

    AttemptDetailResponse getAttemptDetail(UUID attemptId, UUID studentId);

    SaveAnswerResponse saveAnswer(UUID attemptId, UUID questionId, UUID studentId, SaveAnswerRequest request);

    SubmitAttemptResponse submitAttempt(UUID attemptId, UUID studentId);

    List<StudentAttemptSummaryResponse> getMyAttempts(UUID assessmentId, UUID studentId, String authorizationHeader);

    SubmitAttemptResponse getAttemptResult(UUID attemptId, UUID studentId);
}
