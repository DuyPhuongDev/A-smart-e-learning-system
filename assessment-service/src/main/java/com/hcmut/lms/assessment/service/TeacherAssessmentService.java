package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.request.teacher.TeacherEssayGradesRequest;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherAssessmentReportResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherAssessmentSummaryResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherEssayGradesResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherGradebookResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherSubmissionDetailResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherSubmissionSummaryResponse;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;

import java.util.UUID;

public interface TeacherAssessmentService {

    PageResponse<TeacherAssessmentSummaryResponse> listClassAssessments(
            UUID classId,
            CurrentUserInfo currentUser,
            int page,
            int size
    );

    TeacherAssessmentReportResponse getClassAssessmentReport(UUID classId, CurrentUserInfo currentUser);

    PageResponse<TeacherSubmissionSummaryResponse> listAssessmentSubmissions(
            UUID assessmentId,
            String status,
            CurrentUserInfo currentUser,
            int page,
            int size
    );

    TeacherSubmissionDetailResponse getSubmissionDetail(UUID attemptId, CurrentUserInfo currentUser);

    TeacherEssayGradesResponse gradeEssaySubmission(
            UUID attemptId,
            TeacherEssayGradesRequest request,
            CurrentUserInfo currentUser
    );

    TeacherGradebookResponse getClassGradebook(
            UUID classId,
            CurrentUserInfo currentUser,
            int page,
            int size
    );
}
