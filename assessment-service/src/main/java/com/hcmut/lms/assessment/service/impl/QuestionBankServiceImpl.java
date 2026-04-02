package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.questionBank.QuestionBank;
import com.hcmut.lms.assessment.dto.request.questionbank.QuestionBankRequest;
import com.hcmut.lms.assessment.dto.response.QuestionBankResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.mapper.QuestionBankMapper;
import com.hcmut.lms.assessment.repository.QuestionBankRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.QuestionBankService;
import com.hcmut.lms.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionBankServiceImpl implements QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final QuestionBankMapper questionBankMapper;

    @Override
    public QuestionBankResponse createBank(UUID ownerID, QuestionBankRequest request) {
        // validate duplicate
        if(questionBankRepository.existsByNameAndOwnerId(request.getName(), ownerID)) {
            throw new BadRequestException("Question Bank already exists");
        }

        QuestionBank bank = questionBankMapper.toEntity(request);
        bank.setOwnerId(ownerID);
        return questionBankMapper.toResponse(questionBankRepository.save(bank));
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionBankResponse getBank(UUID id) {
        return questionBankMapper.toResponse(findBankById(id));
    }

    @Override
    public QuestionBankResponse updateBank(UUID id, QuestionBankRequest request) {
        QuestionBank bank = findBankById(id);
        questionBankMapper.updateEntity(request, bank);
        return questionBankMapper.toResponse(questionBankRepository.save(bank));
    }

    @Override
    public void deleteBank(UUID id) {
        findBankById(id);
        questionBankRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<QuestionBankResponse> listBanks(UUID ownerId, Pageable pageable) {
        if (ownerId != null) {
            return PageResponse.fromPage(
                    questionBankRepository.findAllByOwnerId(ownerId, pageable)
                            .map(questionBankMapper::toResponse));
        }
        return PageResponse.fromPage(
                questionBankRepository.findAll(pageable)
                        .map(questionBankMapper::toResponse));
    }

    @Override
    public void addQuestion(UUID bankId, UUID questionId) {
        QuestionBank bank = findBankById(bankId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        bank.addQuestion(question);
        questionBankRepository.save(bank);
    }

    @Override
    public void removeQuestion(UUID bankId, UUID questionId) {
        QuestionBank bank = findBankById(bankId);
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        bank.removeQuestion(question);
        questionBankRepository.save(bank);
    }

    private QuestionBank findBankById(UUID id) {
        return questionBankRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("QuestionBank", id));
    }
}
