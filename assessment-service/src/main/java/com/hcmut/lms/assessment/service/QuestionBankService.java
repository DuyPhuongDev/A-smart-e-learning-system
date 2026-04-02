package com.hcmut.lms.assessment.service;

import com.hcmut.lms.assessment.dto.request.questionbank.QuestionBankRequest;
import com.hcmut.lms.assessment.dto.response.QuestionBankResponse;
import com.hcmut.lms.common.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface QuestionBankService {

    QuestionBankResponse createBank(UUID ownerID, QuestionBankRequest request);

    QuestionBankResponse getBank(UUID id);

    QuestionBankResponse updateBank(UUID id, QuestionBankRequest request);

    void deleteBank(UUID id);

    PageResponse<QuestionBankResponse> listBanks(UUID ownerId, Pageable pageable);

    void addQuestion(UUID bankId, UUID questionId);

    void removeQuestion(UUID bankId, UUID questionId);
}
