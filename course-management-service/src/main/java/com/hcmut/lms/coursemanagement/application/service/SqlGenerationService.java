package com.hcmut.lms.coursemanagement.application.service;

import com.hcmut.lms.coursemanagement.application.dto.request.GradeHistoryRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SqlGenerationResponse;

public interface SqlGenerationService {

    SqlGenerationResponse generateInitSqlFromGradeHistory(GradeHistoryRequest request);
}
