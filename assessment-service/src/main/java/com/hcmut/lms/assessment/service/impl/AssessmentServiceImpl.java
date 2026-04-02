package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.exception.UnsupportedQuestionTypeException;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.mapper.AssessmentMapper;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.AssessmentQuestionRepository;
import com.hcmut.lms.assessment.repository.AssessmentRepository;
import com.hcmut.lms.assessment.repository.QuestionBankRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.AssessmentService;
import com.hcmut.lms.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AssessmentQuestionRepository assessmentQuestionRepository;
    private final AssessmentMapper assessmentMapper;
    private final QuestionMapper questionMapper;

    @Override
    public AssessmentResponse createAssessment(AssessmentRequest request) {
        Assessment assessment = assessmentMapper.toEntity(request);
        assessment.setAssessmentStatus(AssessmentStatus.DRAFT);
        return assessmentMapper.toResponse(assessmentRepository.save(assessment));
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentResponse getAssessment(UUID id) {
        return assessmentMapper.toResponse(findAssessmentById(id));
    }

    @Override
    public AssessmentResponse updateAssessment(UUID id, AssessmentRequest request) {
        Assessment assessment = findAssessmentById(id);
        assessmentMapper.updateEntity(request, assessment);
        return assessmentMapper.toResponse(assessmentRepository.save(assessment));
    }

    @Override
    public void deleteAssessment(UUID id) {
        findAssessmentById(id);
        assessmentRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AssessmentResponse> listByClass(UUID classId, Pageable pageable) {
        return PageResponse.fromPage(
                assessmentRepository.findAllByClassId(classId, pageable)
                        .map(assessmentMapper::toResponse));
    }

    @Override
    public void addQuestion(UUID assessmentId, List<AddQuestionRequest> request) {
        Assessment assessment = findAssessmentById(assessmentId);
        List<UUID> questionIds = request.stream()
                .map(AddQuestionRequest::getQuestionId)
                .toList();

        Map<UUID, Question> questionMap = questionRepository.findAllById(questionIds)
                .stream()
                .collect(Collectors.toMap(Question::getId, q -> q));
        request.forEach(requestItem -> {
            Question question = questionMap.get(requestItem.getQuestionId());
            if (question == null) {
                throw new ResourceNotFoundException("Question", requestItem.getQuestionId());
            }

            AssessmentQuestion aq = AssessmentQuestion.builder()
                    .assessment(assessment)
                    .question(question)
                    .orderIndex(requestItem.getOrderIndex())
                    .build();
            assessment.getAssessmentQuestions().add(aq);
        });

        assessmentRepository.save(assessment);
    }

    @Override
    public void removeQuestion(UUID assessmentId, UUID questionId) {
        findAssessmentById(assessmentId);
        assessmentQuestionRepository.deleteByAssessmentIdAndQuestionId(assessmentId, questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestions(UUID assessmentId) {
        findAssessmentById(assessmentId);
        return assessmentQuestionRepository.findByAssessmentIdOrderByIndex(assessmentId).stream()
                .map(aq -> questionMapper.toResponse(aq.getQuestion()))
                .toList();
    }

    @Override
    public void changeStatus(UUID id, AssessmentStatus status) {
        log.info("Changing status of assessment with id {} to {}", id, status.name());
        Assessment assessment = findAssessmentById(id);
        assessment.setAssessmentStatus(status);
        assessmentRepository.save(assessment);
    }

    private Assessment findAssessmentById(UUID id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", id));
    }
}
