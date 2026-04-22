package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.client.CourseManagementInternalClient;
import com.hcmut.lms.assessment.client.dto.ClassGradingWeightDto;
import com.hcmut.lms.assessment.domain.entity.assessment.Assessment;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentQuestion;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentStatus;
import com.hcmut.lms.assessment.domain.entity.assessment.AssessmentType;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AddQuestionRequest;
import com.hcmut.lms.assessment.dto.request.assessment.AssessmentRequest;
import com.hcmut.lms.assessment.dto.response.AssessmentResponse;
import com.hcmut.lms.assessment.dto.response.GradingBreakdownResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.event.AssessmentEventPublisher;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.mapper.AssessmentMapper;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.AssessmentQuestionRepository;
import com.hcmut.lms.assessment.repository.AssessmentRepository;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.AssessmentService;
import com.hcmut.lms.assessment.service.QuestionService;
import com.hcmut.lms.common.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
    private final AssessmentEventPublisher assessmentEventPublisher;
    private final CourseManagementInternalClient courseManagementInternalClient;
    private final QuestionService questionService;

    @Override
    public AssessmentResponse createAssessment(AssessmentRequest request) {
        Assessment assessment = assessmentMapper.toEntity(request);
        assessment.setAssessmentStatus(AssessmentStatus.DRAFT);
        return assessmentMapper.toResponse(assessmentRepository.save(assessment));
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentResponse getAssessment(UUID id) {
        Assessment assessment = findAssessmentById(id);
        return assessmentMapper.toResponse(assessment);
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
                .map(aq -> {
                    QuestionResponse response = questionMapper.toResponse(aq.getQuestion());
                    response.setPoint(aq.getPoint());
                    return response;
                })
                .toList();
    }

    @Override
    public void changeStatus(UUID id, AssessmentStatus status) {
        log.info("Changing status of assessment with id {} to {}", id, status.name());
        Assessment assessment = findAssessmentById(id);
        assessment.setAssessmentStatus(status);
        Assessment saved = assessmentRepository.save(assessment);
        if (status == AssessmentStatus.PUBLISHED) {
            assessmentEventPublisher.publishAssignmentCreated(saved);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradingBreakdownResponse> getGradingBreakdownForClass(UUID classId) {
        log.info("Getting grading breakdown for class: {}", classId);

        // Build type → total-weight map from course-management
        Map<String, Float> weightByType = Map.of();
        try {
            List<ClassGradingWeightDto> weights = courseManagementInternalClient.getGradingWeights(classId);
            if (weights != null) {
                weightByType = weights.stream()
                        .filter(w -> w.getGradingType() != null)
                        .collect(Collectors.toMap(
                                w -> w.getGradingType().toUpperCase(),
                                ClassGradingWeightDto::getWeight,
                                (a, b) -> a));
            }
        } catch (Exception e) {
            log.warn("Failed to fetch grading weights for class {}: {}", classId, e.getMessage());
        }

        // Group assessments that contribute to grading (exclude PRACTICE and legacy QUIZ)
        Map<AssessmentType, List<Assessment>> byType = assessmentRepository
                .findByClassIdOrderByCreatedAtAsc(classId)
                .stream()
                .filter(a -> a.getAssessmentType() != null
                        && a.getAssessmentType() != AssessmentType.PRACTICE
                        && a.getAssessmentType() != AssessmentType.QUIZ)
                .collect(Collectors.groupingBy(Assessment::getAssessmentType));

        List<GradingBreakdownResponse> result = new ArrayList<>();

        // Include every grading component that has a defined weight, even with no assessments yet
        final Map<String, Float> finalWeightByType = weightByType;
        finalWeightByType.forEach((typeName, totalWeight) -> {
            AssessmentType type;
            try {
                type = AssessmentType.valueOf(typeName);
            } catch (IllegalArgumentException ex) {
                return; // unknown type — skip
            }

            List<Assessment> assessments = byType.getOrDefault(type, List.of());
            int count = assessments.size();

            List<GradingBreakdownResponse.AssessmentWeightItem> items = assessments.stream()
                    .map(a -> GradingBreakdownResponse.AssessmentWeightItem.builder()
                            .assessmentId(a.getId())
                            .title(a.getTitle())
                            .weight(count > 0 ? totalWeight / count : null)
                            .build())
                    .toList();

            result.add(GradingBreakdownResponse.builder()
                    .gradingType(typeName)
                    .totalWeight(totalWeight)
                    .assessments(items)
                    .build());
        });

        // Also include types that have assessments but no explicit grading weight yet
        byType.forEach((type, assessments) -> {
            String typeName = type.name();
            if (!finalWeightByType.containsKey(typeName)) {
                List<GradingBreakdownResponse.AssessmentWeightItem> items = assessments.stream()
                        .map(a -> GradingBreakdownResponse.AssessmentWeightItem.builder()
                                .assessmentId(a.getId())
                                .title(a.getTitle())
                                .weight(null)
                                .build())
                        .toList();

                result.add(GradingBreakdownResponse.builder()
                        .gradingType(typeName)
                        .totalWeight(null)
                        .assessments(items)
                        .build());
            }
        });

        return result;
    }

    @Override
    @Transactional
    public List<QuestionResponse> createQuestionsForAssessment(UUID assessmentId, AssessmentQuestionRequest request) {
        Assessment assessment = findAssessmentById(assessmentId);

        Question question = questionService.makeQuestion(request.getQuestion());

        questionRepository.save(question);

        AssessmentQuestion assessmentQuestion = AssessmentQuestion.builder()
                        .assessment(assessment)
                                .question(question)
                                        .orderIndex(request.getOrderIndex())
                                                .point(request.getPoint())
                                                        .build();

        assessment.getAssessmentQuestions().add(assessmentQuestion);
        return assessmentRepository.save(assessment).getAssessmentQuestions()
                .stream().map( i ->{
                   QuestionResponse questionResponse = questionMapper.toResponse((Question) Hibernate.unproxy(i.getQuestion()));
                   questionResponse.setOrderIndex(i.getOrderIndex());
                   questionResponse.setPoint(i.getPoint());
                   return questionResponse;
                }).toList();
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestionsForAssessment(UUID id, UUID questionId, AssessmentQuestionRequest request) {
        // update question
        Question updatedQuestion = questionService.updateQuestion(questionId, request.getQuestion());
        // update config
        AssessmentQuestion assessmentQuestion = assessmentQuestionRepository.findByQuestionIdAndAssessmentId(questionId, id).orElseThrow(() -> new ResourceNotFoundException("Assessment Question not found"));

        assessmentQuestion.setOrderIndex(request.getOrderIndex());
        assessmentQuestion.setPoint(request.getPoint());
        assessmentQuestionRepository.save(assessmentQuestion);

        QuestionResponse questionResponse = questionMapper.toResponse(updatedQuestion);
        questionResponse.setOrderIndex(request.getOrderIndex());
        questionResponse.setPoint(request.getPoint());
        return questionResponse;
    }

    private Assessment findAssessmentById(UUID id) {
        log.info("Finding assessment with id {}", id);
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assessment", id));
    }
}
