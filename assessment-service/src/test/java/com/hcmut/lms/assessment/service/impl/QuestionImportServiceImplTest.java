package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.dto.response.McqQuestionResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.service.AssessmentService;
import com.hcmut.lms.assessment.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class QuestionImportServiceImplTest {

    @Mock
    private QuestionService questionService;

    @Mock
    private AssessmentService assessmentService;

    @InjectMocks
    private QuestionImportServiceImpl questionImportService;

    // --- importMcqQuestions (non-bank) ---

    @Test
    void importMcqQuestions_shouldReturnResult_whenValidFile_nonBank() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,allowMultiAnswer,shuffleOption,"
                + "option1,option2,option3,option4,correctAnswers,"
                + "explanation1,explanation2,explanation3,explanation4\n"
                + "MCQ,EASY,10,What is 2+2?,true,false,true,"
                + "3,4,5,6,2,,,,";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        QuestionResponse created = new McqQuestionResponse();
        when(assessmentService.createQuestionsForAssessment(any(), any())).thenReturn(created);

        var result = questionImportService.importMcqQuestions(file, assessmentId, false);

        assertThat(result.getSuccessCount()).isEqualTo(1);
        assertThat(result.getFailedCount()).isEqualTo(0);
    }

    @Test
    void importMcqQuestions_shouldReturnResult_whenValidFile_isBank() {
        UUID bankId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,allowMultiAnswer,shuffleOption,"
                + "option1,option2,option3,option4,correctAnswers,"
                + "explanation1,explanation2,explanation3,explanation4\n"
                + "MCQ,EASY,10,What is 2+2?,true,false,true,"
                + "3,4,5,6,2,,,,";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importMcqQuestions(file, bankId, true);

        assertThat(result.getSuccessCount()).isEqualTo(1);
    }

    // --- importEssayQuestions (non-bank) ---

    @Test
    void importEssayQuestions_shouldReturnResult_whenValidFile_nonBank() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes\n"
                + "ESSAY,EASY,10,Explain polymorphism,false,"
                + "Answer text,5,pdf;docx";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        QuestionResponse created = new McqQuestionResponse();
        when(assessmentService.createQuestionsForAssessment(any(), any())).thenReturn(created);

        var result = questionImportService.importEssayQuestions(file, assessmentId, false);

        assertThat(result.getSuccessCount()).isEqualTo(1);
    }

    // --- error cases ---

    @Test
    void importMcqQuestions_shouldThrowException_whenEmptyFile() {
        MultipartFile file = new MockMultipartFile("empty.csv", "empty.csv",
                "text/csv", new byte[0]);

        assertThatThrownBy(() -> questionImportService.importMcqQuestions(file, UUID.randomUUID(), false))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void importMcqQuestions_shouldThrowException_whenNotCsv() {
        MultipartFile file = new MockMultipartFile("test.txt", "test.txt",
                "text/plain", "data".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> questionImportService.importMcqQuestions(file, UUID.randomUUID(), false))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("csv");
    }

    @Test
    void importMcqQuestions_shouldReturnValidationErrors_whenRowHasMissingFields() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,allowMultiAnswer,shuffleOption,"
                + "option1,option2,option3,option4,correctAnswers,"
                + "explanation1,explanation2,explanation3,explanation4\n"
                + ",EASY,10,What is 2+2?,true,false,true,"
                + "3,4,5,6,2,,,,";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importMcqQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
        assertThat(result.getErrors()).isNotNull();
    }

    // --- private method: parseUuidStrict ---

    @Test
    void parseUuidStrict_shouldReturnUuid_whenValid() {
        UUID uuid = UUID.randomUUID();
        UUID result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseUuidStrict", uuid.toString());
        assertThat(result).isEqualTo(uuid);
    }

    @Test
    void parseUuidStrict_shouldThrowException_whenInvalid() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(questionImportService,
                "parseUuidStrict", "not-a-uuid"))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid UUID");
    }

    // --- private method: parseEnumIgnoreCase ---

    @Test
    void parseEnumIgnoreCase_shouldReturnEnum_whenValid() {
        var result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseEnumIgnoreCase", com.hcmut.lms.assessment.domain.entity.question.QuestionType.class, "mcq");
        assertThat(result).isEqualTo(com.hcmut.lms.assessment.domain.entity.question.QuestionType.MCQ);
    }

    @Test
    void parseEnumIgnoreCase_shouldThrowException_whenInvalid() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(questionImportService,
                "parseEnumIgnoreCase", com.hcmut.lms.assessment.domain.entity.question.QuestionType.class, "INVALID"))
                .isInstanceOf(BadRequestException.class);
    }

    // --- private method: parseBooleanStrict ---

    @Test
    void parseBooleanStrict_shouldReturnTrue_whenTrue() {
        boolean result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseBooleanStrict", "true");
        assertThat(result).isTrue();
    }

    @Test
    void parseBooleanStrict_shouldReturnFalse_whenFalse() {
        boolean result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseBooleanStrict", "false");
        assertThat(result).isFalse();
    }

    @Test
    void parseBooleanStrict_shouldThrowException_whenInvalid() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(questionImportService,
                "parseBooleanStrict", "yes"))
                .isInstanceOf(BadRequestException.class);
    }

    // --- private method: isBlank ---

    @Test
    void isBlank_shouldReturnTrue_whenNull() {
        boolean result = ReflectionTestUtils.invokeMethod(questionImportService, "isBlank", (String) null);
        assertThat(result).isTrue();
    }

    @Test
    void isBlank_shouldReturnFalse_whenNonBlank() {
        boolean result = ReflectionTestUtils.invokeMethod(questionImportService, "isBlank", "hello");
        assertThat(result).isFalse();
    }

    // --- private method: toHeaderIndex ---

    @Test
    void toHeaderIndex_shouldReturnIndexMap() {
        Map<String, Integer> result = ReflectionTestUtils.invokeMethod(questionImportService,
                "toHeaderIndex", (Object) new String[]{"col1", "col2"});
        assertThat(result).containsKeys("col1", "col2");
        assertThat(result.get("col1")).isEqualTo(0);
        assertThat(result.get("col2")).isEqualTo(1);
    }

    // --- private method: requireColumns ---

    @Test
    void requireColumns_shouldThrowException_whenMissing() {
        Map<String, Integer> headerIndex = Map.of("col1", 0);
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(questionImportService,
                "requireColumns", headerIndex, List.of("col1", "col2")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("col2");
    }

    // --- private method: normalizeNullable ---

    @Test
    void normalizeNullable_shouldReturnNull_whenBlank() {
        String result = ReflectionTestUtils.invokeMethod(questionImportService,
                "normalizeNullable", "  ");
        assertThat(result).isNull();
    }

    // --- parseCorrectOptionNumbers edge cases ---

    @Test
    void parseCorrectOptionNumbers_shouldReportError_whenOutOfRange() {
        List<?> errors = new ArrayList<>();
        Set<Integer> result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseCorrectOptionNumbers", "5", 1, errors);
        assertThat(result).isEmpty();
    }

    @Test
    void parseCorrectOptionNumbers_shouldReportError_whenNotANumber() {
        List<?> errors = new ArrayList<>();
        Set<Integer> result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseCorrectOptionNumbers", "abc", 1, errors);
        assertThat(result).isEmpty();
    }

    // --- parseAcceptedFileTypes ---

    @Test
    void parseAcceptedFileTypes_shouldReturnNull_whenEmpty() {
        List<String> result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseAcceptedFileTypes", "");
        assertThat(result).isNull();
    }

    @Test
    void parseAcceptedFileTypes_shouldReturnList_whenSemicolonSeparated() {
        List<String> result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseAcceptedFileTypes", "pdf;docx");
        assertThat(result).containsExactly("pdf", "docx");
    }

    // --- import MCQ with wrong questionType ---

    @Test
    void importMcqQuestions_shouldReportError_whenWrongQuestionType() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,allowMultiAnswer,shuffleOption,"
                + "option1,option2,option3,option4,correctAnswers,"
                + "explanation1,explanation2,explanation3,explanation4\n"
                + "ESSAY,EASY,10,What is 2+2?,true,false,true,"
                + "3,4,5,6,2,,,,";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importMcqQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
    }

    // --- import MCQ with multi-answer but allowMultiAnswer=false ---

    @Test
    void importMcqQuestions_shouldReportError_whenMultiAnswerNotAllowed() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,allowMultiAnswer,shuffleOption,"
                + "option1,option2,option3,option4,correctAnswers,"
                + "explanation1,explanation2,explanation3,explanation4\n"
                + "MCQ,EASY,10,What is 2+2?,true,false,true,"
                + "3,4,5,6,1;2,,,,";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importMcqQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
    }

    // --- import essay with wrong questionType ---

    @Test
    void importEssayQuestions_shouldReportError_whenWrongQuestionType() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes\n"
                + "MCQ,EASY,10,Explain,false,Answer,5,pdf";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importEssayQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
    }

    // --- import essay with missing questionType ---

    @Test
    void importEssayQuestions_shouldReportError_whenMissingQuestionType() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes\n"
                + ",EASY,10,Explain,false,Answer,5,pdf";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importEssayQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
    }

    // --- getRequiredCell missing column ---

    @Test
    void getRequiredCell_shouldThrowException_whenBlank() {
        Map<String, Integer> headerIndex = Map.of("col", 0);
        String[] row = {""};
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(questionImportService,
                "getRequiredCell", row, headerIndex, "col"))
                .isInstanceOf(BadRequestException.class);
    }

    // --- importEssayQuestions isBank ---

    @Test
    void importEssayQuestions_shouldReturnResult_whenValidFile_isBank() {
        UUID bankId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes\n"
                + "ESSAY,EASY,10,Explain polymorphism,false,"
                + "Answer text,5,pdf;docx";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importEssayQuestions(file, bankId, true);

        assertThat(result.getSuccessCount()).isEqualTo(1);
    }

    // --- importEssayQuestions with invalid maxFileSize ---

    @Test
    void importEssayQuestions_shouldReportError_whenInvalidMaxFileSize() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes\n"
                + "ESSAY,EASY,10,Explain,false,Answer,not_a_number,pdf";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        var result = questionImportService.importEssayQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
    }

    // --- parseEnumIgnoreCase with null ---

    @Test
    void parseEnumIgnoreCase_shouldThrowException_whenNull() {
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(questionImportService,
                "parseEnumIgnoreCase", com.hcmut.lms.assessment.domain.entity.question.QuestionType.class, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Value is required");
    }

    // --- parseCorrectOptionNumbers with empty ---

    @Test
    void parseCorrectOptionNumbers_shouldReportError_whenEmpty() {
        List<?> errors = new ArrayList<>();
        Set<Integer> result = ReflectionTestUtils.invokeMethod(questionImportService,
                "parseCorrectOptionNumbers", "", 1, errors);
        assertThat(result).isEmpty();
    }

    // --- importEssayQuestions with questionBankId ---

    @Test
    void importEssayQuestions_shouldHandleQuestionBankId() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes,questionBankId\n"
                + "ESSAY,EASY,10,Explain,false,Answer,5,pdf," + UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        QuestionResponse created = new McqQuestionResponse();
        when(assessmentService.createQuestionsForAssessment(any(), any())).thenReturn(created);

        var result = questionImportService.importEssayQuestions(file, assessmentId, false);

        assertThat(result.getSuccessCount()).isEqualTo(1);
    }

    // --- importEssayQuestions when service throws ---

    @Test
    void importEssayQuestions_shouldReportError_whenServiceThrows() {
        UUID assessmentId = UUID.randomUUID();
        String csv = "questionType,difficultLevel,point,content,required,"
                + "sampleAnswer,maxFileSize,acceptedFileTypes\n"
                + "ESSAY,EASY,10,Explain,false,Answer,5,pdf";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(assessmentService.createQuestionsForAssessment(any(), any()))
                .thenThrow(new RuntimeException("Test exception"));

        var result = questionImportService.importEssayQuestions(file, assessmentId, false);

        assertThat(result.getFailedCount()).isEqualTo(1);
    }
}
