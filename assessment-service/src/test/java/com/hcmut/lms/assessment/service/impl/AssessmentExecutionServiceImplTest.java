package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.McqQuestion;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.student.RunTestcaseRequest;
import com.hcmut.lms.assessment.dto.response.GradingResponse;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.exception.UnsupportedQuestionTypeException;
import com.hcmut.lms.assessment.handler.QuestionHandler;
import com.hcmut.lms.assessment.handler.dto.FeedbackDto;
import com.hcmut.lms.assessment.handler.dto.GradingResult;
import com.hcmut.lms.assessment.handler.dto.GradingStatus;
import com.hcmut.lms.assessment.handler.dto.McqSubmissionDto;
import com.hcmut.lms.assessment.handler.dto.SubmissionDto;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.service.judge.CppJudgeService;
import com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation;
import com.hcmut.lms.assessment.service.judge.dto.JudgeVerdict;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentExecutionServiceImplTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionHandler questionHandler;

    @Mock
    private CppJudgeService judgeService;

    private AssessmentExecutionServiceImpl assessmentExecutionService;

    private AssessmentExecutionServiceImpl createService() {
        return new AssessmentExecutionServiceImpl(List.of(questionHandler), judgeService, questionRepository);
    }

    // --- submitAnswer ---

    @Test
    void submitAnswer_shouldReturnGradingResponse_whenValidSubmission() {
        when(questionHandler.getSupportedType()).thenReturn(QuestionType.MCQ);
        assessmentExecutionService = createService();
        UUID questionId = UUID.randomUUID();
        McqQuestion question = createMcqQuestion();
        question.setId(questionId);
        McqSubmissionDto submission = new McqSubmissionDto();
        submission.setQuestionId(questionId);
        BigDecimal maxScore = BigDecimal.TEN;

        GradingResult gradingResult = GradingResult.builder()
                .status(GradingStatus.CORRECT)
                .earnedPoints(BigDecimal.TEN)
                .maxPoints(maxScore)
                .build();
        FeedbackDto feedback = FeedbackDto.builder().message("Correct").build();

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        doNothing().when(questionHandler).validate(question, submission);
        when(questionHandler.grade(question, submission, maxScore)).thenReturn(gradingResult);
        when(questionHandler.generateFeedback(question, gradingResult)).thenReturn(feedback);

        GradingResponse result = assessmentExecutionService.submitAnswer(questionId, submission, maxScore);

        assertThat(result.getStatus()).isEqualTo(GradingStatus.CORRECT);
        assertThat(result.getEarnedPoints()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    void submitAnswer_shouldThrowException_whenQuestionNotFound() {
        assessmentExecutionService = createService();
        UUID questionId = UUID.randomUUID();
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assessmentExecutionService.submitAnswer(questionId, new McqSubmissionDto(), BigDecimal.TEN))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Question");
    }

    @Test
    void submitAnswer_shouldThrowException_whenUnsupportedQuestionType() {
        assessmentExecutionService = new AssessmentExecutionServiceImpl(List.of(), judgeService, questionRepository);
        UUID questionId = UUID.randomUUID();
        McqQuestion question = createMcqQuestion();
        question.setId(questionId);

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        assertThatThrownBy(() -> assessmentExecutionService.submitAnswer(questionId, new McqSubmissionDto(), BigDecimal.TEN))
                .isInstanceOf(UnsupportedQuestionTypeException.class);
    }

    // --- preCheckTestcase ---

    @Test
    void preCheckTestcase_shouldReturnEvaluation_whenValidCode() {
        assessmentExecutionService = createService();
        UUID questionId = UUID.randomUUID();
        CodingQuestion question = createCodingQuestion();
        question.setId(questionId);
        RunTestcaseRequest request = new RunTestcaseRequest();
        request.setSourceCode("int main(){}");
        request.setLanguageCode("cpp");

        CodingJudgeEvaluation evaluation = CodingJudgeEvaluation.builder()
                .passedCount(1).totalCount(1).overallVerdict(JudgeVerdict.AC)
                .detail("ok").testCaseResults(List.of()).build();
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));
        when(judgeService.evaluate(question, request.getSourceCode(), request.getLanguageCode(), true))
                .thenReturn(evaluation);

        CodingJudgeEvaluation result = assessmentExecutionService.preCheckTestcase(questionId, request);

        assertThat(result.getPassedCount()).isEqualTo(1);
    }

    // --- helper methods ---

    private McqQuestion createMcqQuestion() {
        McqQuestion q = new McqQuestion();
        q.setQuestionType(QuestionType.MCQ);
        q.setContent("Sample question");
        return q;
    }

    private CodingQuestion createCodingQuestion() {
        CodingQuestion q = new CodingQuestion();
        q.setQuestionType(QuestionType.CODING);
        q.setContent("Write a function");
        q.setExecutionTimeLimit(1000);
        q.setExecutionMemoryLimit(64);
        return q;
    }
}
