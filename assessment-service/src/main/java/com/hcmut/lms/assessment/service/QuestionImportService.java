package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.response.QuestionImportResultResponse;
import org.springframework.web.multipart.MultipartFile;

public interface QuestionImportService {
    QuestionImportResultResponse importMcqQuestions(MultipartFile file);

    QuestionImportResultResponse importEssayQuestions(MultipartFile file);
}

