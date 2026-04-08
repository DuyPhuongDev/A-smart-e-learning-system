package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentLearningProgressResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.StudentSubjectDetailResponse;
import com.hcmut.lms.coursemanagement.application.service.StudentProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/student-progress")
@RequiredArgsConstructor
public class StudentProgressController {

    private final StudentProgressService studentProgressService;

    @GetMapping("/me")
    public ResponseEntity<StudentLearningProgressResponse> getMyLearningProgress(
            @CurrentUser CurrentUserInfo currentUser) {
        StudentLearningProgressResponse response = studentProgressService.getStudentLearningProgress(currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/subjects/{subjectId}/detail")
    public ResponseEntity<StudentSubjectDetailResponse> getSubjectDetail(
            @PathVariable UUID subjectId, @CurrentUser CurrentUserInfo currentUser) {
        StudentSubjectDetailResponse response = studentProgressService.getStudentSubjectDetail(subjectId, currentUser.getId());
        return ResponseEntity.ok(response);
    }
}
