package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.response.QuestionImportResultResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface QuestionImportService {
    QuestionImportResultResponse importMcqQuestions(MultipartFile file, UUID id, boolean isBank);

    QuestionImportResultResponse importEssayQuestions(MultipartFile file,  UUID id, boolean isBank);
}

