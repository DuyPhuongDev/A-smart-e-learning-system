package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.questionBank.QuestionBank;
import com.hcmut.lms.assessment.dto.request.questionbank.QuestionBankRequest;
import com.hcmut.lms.assessment.dto.response.QuestionBankResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.mapper.QuestionBankMapper;
import com.hcmut.lms.assessment.repository.QuestionBankRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.common.dto.PageResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionBankServiceImplTest {

    @Mock
    private QuestionBankRepository questionBankRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionBankMapper questionBankMapper;

    @InjectMocks
    private QuestionBankServiceImpl questionBankService;

    // --- createBank ---

    @Test
    void createBank_shouldReturnResponse_whenValidRequest() {
        UUID ownerId = UUID.randomUUID();
        QuestionBankRequest request = createRequest("My Bank", "Description");
        QuestionBank entity = createEntity("My Bank");
        QuestionBank saved = createEntityWithId(UUID.randomUUID(), "My Bank", ownerId);
        QuestionBankResponse expected = createResponse(saved.getId(), "My Bank");

        when(questionBankRepository.existsByNameAndOwnerId("My Bank", ownerId)).thenReturn(false);
        when(questionBankMapper.toEntity(request)).thenReturn(entity);
        when(questionBankRepository.save(entity)).thenReturn(saved);
        when(questionBankMapper.toResponse(saved)).thenReturn(expected);

        QuestionBankResponse result = questionBankService.createBank(ownerId, request);

        assertThat(result).isEqualTo(expected);
        verify(questionBankRepository).save(entity);
    }

    @Test
    void createBank_shouldThrowException_whenDuplicateName() {
        UUID ownerId = UUID.randomUUID();
        QuestionBankRequest request = createRequest("My Bank", null);

        when(questionBankRepository.existsByNameAndOwnerId("My Bank", ownerId)).thenReturn(true);

        assertThatThrownBy(() -> questionBankService.createBank(ownerId, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Question Bank already exists");
        verify(questionBankRepository, never()).save(any());
    }

    // --- getBank ---

    @Test
    void getBank_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        QuestionBank entity = createEntityWithId(id, "My Bank", UUID.randomUUID());
        QuestionBankResponse expected = createResponse(id, "My Bank");

        when(questionBankRepository.findById(id)).thenReturn(Optional.of(entity));
        when(questionBankMapper.toResponse(entity)).thenReturn(expected);

        QuestionBankResponse result = questionBankService.getBank(id);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getBank_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(questionBankRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionBankService.getBank(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("QuestionBank");
    }

    // --- updateBank ---

    @Test
    void updateBank_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        QuestionBankRequest request = createRequest("Updated", null);
        QuestionBank entity = createEntityWithId(id, "Old", UUID.randomUUID());
        QuestionBank saved = createEntityWithId(id, "Updated", UUID.randomUUID());
        QuestionBankResponse expected = createResponse(id, "Updated");

        when(questionBankRepository.findById(id)).thenReturn(Optional.of(entity));
        when(questionBankRepository.save(entity)).thenReturn(saved);
        when(questionBankMapper.toResponse(saved)).thenReturn(expected);

        QuestionBankResponse result = questionBankService.updateBank(id, request);

        assertThat(result).isEqualTo(expected);
        verify(questionBankMapper).updateEntity(request, entity);
    }

    // --- deleteBank ---

    @Test
    void deleteBank_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        QuestionBank entity = createEntityWithId(id, "Bank", UUID.randomUUID());

        when(questionBankRepository.findById(id)).thenReturn(Optional.of(entity));

        questionBankService.deleteBank(id);

        verify(questionBankRepository).deleteById(id);
    }

    @Test
    void deleteBank_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(questionBankRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionBankService.deleteBank(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("QuestionBank");
    }

    // --- listBanks ---

    @Test
    void listBanks_shouldReturnList_whenOwnerIdProvided() {
        UUID ownerId = UUID.randomUUID();
        QuestionBank e1 = createEntityWithId(UUID.randomUUID(), "Bank1", ownerId);
        QuestionBank e2 = createEntityWithId(UUID.randomUUID(), "Bank2", ownerId);
        Page<QuestionBank> page = new PageImpl<>(List.of(e1, e2));
        when(questionBankRepository.findAllByOwnerId(eq(ownerId), any(Pageable.class))).thenReturn(page);
        when(questionBankMapper.toResponse(e1)).thenReturn(createResponse(e1.getId(), "Bank1"));
        when(questionBankMapper.toResponse(e2)).thenReturn(createResponse(e2.getId(), "Bank2"));

        PageResponse<QuestionBankResponse> result = questionBankService.listBanks(ownerId, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void listBanks_shouldReturnAll_whenOwnerIdNull() {
        QuestionBank e1 = createEntityWithId(UUID.randomUUID(), "Bank1", UUID.randomUUID());
        Page<QuestionBank> page = new PageImpl<>(List.of(e1));
        when(questionBankRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(questionBankMapper.toResponse(e1)).thenReturn(createResponse(e1.getId(), "Bank1"));

        PageResponse<QuestionBankResponse> result = questionBankService.listBanks(null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    // --- addQuestion ---

    @Test
    void addQuestion_shouldAddQuestionToBank() {
        UUID bankId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        QuestionBank bank = createEntityWithId(bankId, "Bank", UUID.randomUUID());
        McqQuestion question = new McqQuestion();
        question.setId(questionId);

        when(questionBankRepository.findById(bankId)).thenReturn(Optional.of(bank));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        questionBankService.addQuestion(bankId, questionId);

        verify(questionBankRepository).save(bank);
    }

    @Test
    void addQuestion_shouldThrowException_whenQuestionNotFound() {
        UUID bankId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        when(questionBankRepository.findById(bankId)).thenReturn(Optional.of(createEntityWithId(bankId, "Bank", UUID.randomUUID())));
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionBankService.addQuestion(bankId, questionId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Question");
    }

    // --- removeQuestion ---

    @Test
    void removeQuestion_shouldRemoveQuestionFromBank() {
        UUID bankId = UUID.randomUUID();
        UUID questionId = UUID.randomUUID();
        QuestionBank bank = createEntityWithId(bankId, "Bank", UUID.randomUUID());
        McqQuestion question = new McqQuestion();
        question.setId(questionId);

        when(questionBankRepository.findById(bankId)).thenReturn(Optional.of(bank));
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        questionBankService.removeQuestion(bankId, questionId);

        verify(questionBankRepository).save(bank);
    }

    // --- helper methods ---

    private QuestionBankRequest createRequest(String name, String description) {
        QuestionBankRequest request = new QuestionBankRequest();
        request.setName(name);
        request.setDescription(description);
        return request;
    }

    private QuestionBank createEntity(String name) {
        QuestionBank bank = new QuestionBank();
        bank.setName(name);
        return bank;
    }

    private QuestionBank createEntityWithId(UUID id, String name, UUID ownerId) {
        QuestionBank bank = createEntity(name);
        bank.setId(id);
        bank.setOwnerId(ownerId);
        return bank;
    }

    private QuestionBankResponse createResponse(UUID id, String name) {
        return QuestionBankResponse.builder()
                .id(id)
                .name(name)
                .build();
    }
}
