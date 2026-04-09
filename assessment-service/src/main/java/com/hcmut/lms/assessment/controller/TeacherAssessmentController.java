package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.dto.request.teacher.TeacherEssayGradesRequest;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherAssessmentSummaryResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherEssayGradesResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherGradebookResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherSubmissionDetailResponse;
import com.hcmut.lms.assessment.dto.response.teacher.TeacherSubmissionSummaryResponse;
import com.hcmut.lms.assessment.service.TeacherAssessmentService;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/teacher")
@RequiredArgsConstructor
public class TeacherAssessmentController {

    private final TeacherAssessmentService teacherAssessmentService;

    @GetMapping("/classes/{classId}/assessments")
    public PageResponse<TeacherAssessmentSummaryResponse> listClassAssessments(
            @PathVariable UUID classId,
            @CurrentUser CurrentUserInfo currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return teacherAssessmentService.listClassAssessments(classId, currentUser, page, size);
    }

    @GetMapping("/assessments/{assessmentId}/submissions")
    public PageResponse<TeacherSubmissionSummaryResponse> listAssessmentSubmissions(
            @PathVariable UUID assessmentId,
            @CurrentUser CurrentUserInfo currentUser,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return teacherAssessmentService.listAssessmentSubmissions(assessmentId, status, currentUser, page, size);
    }

    @GetMapping("/submissions/{attemptId}")
    public TeacherSubmissionDetailResponse getSubmissionDetail(
            @PathVariable UUID attemptId,
            @CurrentUser CurrentUserInfo currentUser
    ) {
        return teacherAssessmentService.getSubmissionDetail(attemptId, currentUser);
    }

    @PutMapping("/submissions/{attemptId}/essay-grades")
    public TeacherEssayGradesResponse gradeEssaySubmission(
            @PathVariable UUID attemptId,
            @CurrentUser CurrentUserInfo currentUser,
            @Valid @RequestBody TeacherEssayGradesRequest request
    ) {
        return teacherAssessmentService.gradeEssaySubmission(attemptId, request, currentUser);
    }

    @GetMapping("/classes/{classId}/gradebook")
    public TeacherGradebookResponse getClassGradebook(
            @PathVariable UUID classId,
            @CurrentUser CurrentUserInfo currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return teacherAssessmentService.getClassGradebook(classId, currentUser, page, size);
    }
}
