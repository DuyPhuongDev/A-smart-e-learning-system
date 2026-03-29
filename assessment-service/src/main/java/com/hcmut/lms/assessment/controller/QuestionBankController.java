package com.hcmut.lms.assessment.controller;

import com.hcmut.lms.assessment.dto.request.questionbank.QuestionBankRequest;
import com.hcmut.lms.assessment.dto.response.QuestionBankResponse;
import com.hcmut.lms.assessment.service.QuestionBankService;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/question-banks")
@RequiredArgsConstructor
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionBankResponse createBank(
            @CurrentUser CurrentUserInfo currentUser,
            @Valid @RequestBody QuestionBankRequest request) {
        return questionBankService.createBank(currentUser.getId(), request);
    }

    @GetMapping("/{id}")
    public QuestionBankResponse getBank(@PathVariable UUID id) {
        return questionBankService.getBank(id);
    }

    @PutMapping("/{id}")
    public QuestionBankResponse updateBank(@PathVariable UUID id,
                                           @Valid @RequestBody QuestionBankRequest request) {
        return questionBankService.updateBank(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteBank(@PathVariable UUID id) {
        questionBankService.deleteBank(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public PageResponse<QuestionBankResponse> listBanks(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return questionBankService.listBanks(ownerId,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));
    }

    @PostMapping("/{id}/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> addQuestion(@PathVariable UUID id,
                                            @PathVariable UUID questionId) {
        questionBankService.addQuestion(id, questionId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeQuestion(@PathVariable UUID id,
                                               @PathVariable UUID questionId) {
        questionBankService.removeQuestion(id, questionId);
        return ResponseEntity.noContent().build();
    }
}
