package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.dto.response.QuestionImportResultResponse;
import com.hcmut.lms.assessment.dto.response.TestCaseImportResultResponse;
import com.hcmut.lms.assessment.service.QuestionImportService;
import com.hcmut.lms.assessment.service.TestCaseImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/questions")
@RequiredArgsConstructor
public class QuestionImportController {

    private final QuestionImportService questionImportService;
    private final TestCaseImportService testCaseImportService;

//    @PostMapping(value = "/import/mcq", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<QuestionImportResultResponse> importMcqQuestions(@RequestPart("file") MultipartFile file) {
//        QuestionImportResultResponse result = questionImportService.importMcqQuestions(file);
//        return ResponseEntity.ok(result);
//    }
//
//    @PostMapping(value = "/import/essay", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<QuestionImportResultResponse> importEssayQuestions(@RequestPart("file") MultipartFile file) {
//        QuestionImportResultResponse result = questionImportService.importEssayQuestions(file);
//        return ResponseEntity.ok(result);
//    }
//
//    /**
//     * Import coding test cases for one CODING question.
//     * CSV template columns: input, expected, hidden (no questionId in file).
//     */
//    @PostMapping(value = "/{questionId}/import/testcases", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<TestCaseImportResultResponse> importTestCases(
//            @PathVariable UUID questionId,
//            @RequestPart("file") MultipartFile file) {
//        TestCaseImportResultResponse result = testCaseImportService.importTestCases(questionId, file);
//        return ResponseEntity.ok(result);
//    }
}

