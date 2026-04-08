package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.GradeHistoryRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SqlGenerationResponse;
import com.hcmut.lms.coursemanagement.application.service.SqlGenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${prefix-api}/sql-data-generation")
@RequiredArgsConstructor
public class SqlGenerationController {

    private final SqlGenerationService sqlGenerationService;

    @PostMapping("/grade-history")
    public ResponseEntity<SqlGenerationResponse> generateSqlFromGradeHistory(
            @Valid @RequestBody GradeHistoryRequest request) {
        SqlGenerationResponse response = sqlGenerationService.generateInitSqlFromGradeHistory(request);
        return ResponseEntity.ok(response);
    }
}
