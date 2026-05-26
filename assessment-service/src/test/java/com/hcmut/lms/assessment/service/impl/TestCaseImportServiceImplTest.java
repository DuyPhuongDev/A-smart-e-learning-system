package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.TestCaseRequest;
import com.hcmut.lms.assessment.dto.response.TestCaseResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.repository.TestCaseRepository;
import org.springframework.test.util.ReflectionTestUtils;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestCaseImportServiceImplTest {

    @Mock
    private TestCaseRepository testCaseRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionMapper questionMapper;

    @InjectMocks
    private TestCaseImportServiceImpl testCaseImportService;

    // --- createTestCase ---

    @Test
    void createTestCase_shouldReturnResponse_whenValidRequest() {
        UUID questionId = UUID.randomUUID();
        CodingQuestion codingQuestion = createCodingQuestion(questionId);
        TestCaseRequest request = TestCaseRequest.builder()
                .input("1 2").expected("3").hidden(false).build();
        TestCase testCase = TestCase.builder()
                .input("1 2").expected("3").hidden(false).codingQuestion(codingQuestion).build();

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(codingQuestion));
        when(testCaseRepository.save(any(TestCase.class))).thenReturn(testCase);

        TestCaseResponse result = testCaseImportService.createTestCase(questionId, request);

        assertThat(result).isNotNull();
    }

    @Test
    void createTestCase_shouldThrowException_whenQuestionNotFound() {
        UUID questionId = UUID.randomUUID();
        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> testCaseImportService.createTestCase(questionId, TestCaseRequest.builder().build()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createTestCase_shouldThrowException_whenNotCodingQuestion() {
        UUID questionId = UUID.randomUUID();
        CodingQuestion question = createCodingQuestion(questionId);
        question.setQuestionType(null); // make it not CODING type
        when(questionRepository.findById(questionId)).thenReturn(Optional.of(question));

        assertThatThrownBy(() -> testCaseImportService.createTestCase(questionId, TestCaseRequest.builder().build()))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("not a CODING question");
    }

    // --- importTestCases ---

    @Test
    void importTestCases_shouldReturnResult_whenValidFile() {
        UUID questionId = UUID.randomUUID();
        CodingQuestion codingQuestion = createCodingQuestion(questionId);
        String csv = "input,expected,hidden\n1 2,3,false\n4 5,9,true";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(codingQuestion));
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = testCaseImportService.importTestCases(questionId, file);

        assertThat(result.getSuccessCount()).isEqualTo(2);
        assertThat(result.getFailedCount()).isEqualTo(0);
    }

    @Test
    void importTestCases_shouldThrowException_whenEmptyFile() {
        UUID questionId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("empty.csv", "empty.csv",
                "text/csv", new byte[0]);

        assertThatThrownBy(() -> testCaseImportService.importTestCases(questionId, file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void importTestCases_shouldThrowException_whenNotCsv() {
        UUID questionId = UUID.randomUUID();
        MultipartFile file = new MockMultipartFile("test.txt", "test.txt",
                "text/plain", "data".getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> testCaseImportService.importTestCases(questionId, file))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("csv");
    }

    // --- importTestCases (extended) ---

    @Test
    void importTestCases_shouldHandleEscapedFields() {
        UUID questionId = UUID.randomUUID();
        CodingQuestion codingQuestion = createCodingQuestion(questionId);
        String csv = "input,expected,hidden\n\"a,b\",\"x\\ny\",false";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(codingQuestion));
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(inv -> inv.getArgument(0));

        var result = testCaseImportService.importTestCases(questionId, file);

        assertThat(result.getSuccessCount()).isEqualTo(1);
    }

    @Test
    void importTestCases_shouldReportErrors_whenInvalidRow() {
        UUID questionId = UUID.randomUUID();
        CodingQuestion codingQuestion = createCodingQuestion(questionId);
        String csv = "input,expected,hidden\n,\"x\",invalid_bool";
        MultipartFile file = new MockMultipartFile("test.csv", "test.csv",
                "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        when(questionRepository.findById(questionId)).thenReturn(Optional.of(codingQuestion));

        var result = testCaseImportService.importTestCases(questionId, file);

        assertThat(result.getFailedCount()).isGreaterThanOrEqualTo(1);
    }

    // --- private methods via ReflectionTestUtils ---

    @Test
    void unescapeTestCaseField_shouldReturnNull_whenInputNull() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", (String) null);
        assertThat(result).isNull();
    }

    @Test
    void unescapeTestCaseField_shouldUnescapeNewlines() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "line1\\nline2");
        assertThat(result).isEqualTo("line1\nline2");
    }

    @Test
    void unescapeTestCaseField_shouldUnescapeTabs() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "col1\\tcol2");
        assertThat(result).isEqualTo("col1\tcol2");
    }

    @Test
    void unescapeTestCaseField_shouldUnescapeBackslash() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "path\\\\file");
        assertThat(result).isEqualTo("path\\file");
    }

    @Test
    void addError_shouldAddToList() {
        ArrayList<String> errors = new ArrayList<>();
        ReflectionTestUtils.invokeMethod(testCaseImportService,
                "addError", errors, 1, "field", "Error message");
        assertThat(errors).hasSize(1);
    }

    @Test
    void isRowEmpty_shouldReturnTrue_whenAllCellsBlank() {
        Map<String, Integer> headerIndex = Map.of("input", 0, "expected", 1);
        String[] row = {"", ""};
        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "isRowEmpty", row, headerIndex);
        assertThat(result).isTrue();
    }

    // --- private method: parseHidden ---

    @Test
    void parseHidden_shouldReturnTrue_whenTrueValue() {
        Map<String, Integer> headerIndex = Map.of("hidden", 0);
        String[] row = {"true"};
        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "parseHidden", row, headerIndex, 1, new ArrayList<>());
        assertThat(result).isTrue();
    }

    @Test
    void parseHidden_shouldReturnFalse_whenOtherValue() {
        Map<String, Integer> headerIndex = Map.of("hidden", 0);
        String[] row = {"false"};
        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "parseHidden", row, headerIndex, 1, new ArrayList<>());
        assertThat(result).isFalse();
    }

    @Test
    void parseHidden_shouldReturnFalse_whenBlank() {
        Map<String, Integer> headerIndex = Map.of("hidden", 0);
        String[] row = {""};
        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "parseHidden", row, headerIndex, 1, new ArrayList<>());
        assertThat(result).isFalse();
    }

    // --- private method: toHeaderIndex ---

    @Test
    void toHeaderIndex_shouldReturnMap_whenValidHeader() {
        Map<String, Integer> result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "toHeaderIndex", (Object) new String[]{"input", "expected", "hidden"});
        assertThat(result).containsKeys("input", "expected", "hidden");
    }

    // --- private method: requireColumns ---

    @Test
    void requireColumns_shouldThrowException_whenMissingColumn() {
        Map<String, Integer> headerIndex = Map.of("input", 0);
        assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(testCaseImportService,
                "requireColumns", headerIndex, List.of("input", "expected")))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("expected");
    }

    // --- getCell edge cases ---

    @Test
    void getCell_shouldReturnNull_whenIndexOutOfBounds() {
        Map<String, Integer> headerIndex = Map.of("col", 10);
        String[] row = {"value"};
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "getCell", row, headerIndex, "col");
        assertThat(result).isNull();
    }

    // --- isRowEmpty when has content ---

    @Test
    void isRowEmpty_shouldReturnFalse_whenHasContent() {
        Map<String, Integer> headerIndex = Map.of("input", 0);
        String[] row = {"1 2"};
        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "isRowEmpty", row, headerIndex);
        assertThat(result).isFalse();
    }

    // --- parseHidden with null column ---

    @Test
    void parseHidden_shouldReturnFalse_whenColumnMissing() {
        Map<String, Integer> headerIndex = Map.of();
        String[] row = {};
        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "parseHidden", row, headerIndex, 1, new ArrayList<>());
        assertThat(result).isFalse();
    }

    // --- unescapeTestCaseField extended ---

    @Test
    void unescapeTestCaseField_shouldHandleCarriageReturn() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "line1\\rline2");
        assertThat(result).isEqualTo("line1\rline2");
    }

    @Test
    void unescapeTestCaseField_shouldHandleEscapedQuote() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "he said \\\"hello\\\"");
        assertThat(result).isEqualTo("he said \"hello\"");
    }

    @Test
    void unescapeTestCaseField_shouldPassThroughUnknownEscape() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "path\\xfile");
        assertThat(result).isEqualTo("path\\xfile");
    }

    // --- toHeaderIndex with null entry ---

    @Test
    void toHeaderIndex_shouldSkipNullEntry() {
        Map<String, Integer> result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "toHeaderIndex", (Object) new String[]{"input", null, "expected"});
        assertThat(result).containsKeys("input", "expected");
        assertThat(result).doesNotContainKey((String) null);
    }

    // --- unescapeTestCaseField with empty ---

    @Test
    void unescapeTestCaseField_shouldReturnEmpty_whenEmpty() {
        String result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "unescapeTestCaseField", "");
        assertThat(result).isEmpty();
    }

    // --- importOneRow exception catch ---

    @Test
    void importOneRow_shouldCatchExceptionAndReportError() {
        UUID questionId = UUID.randomUUID();
        Map<String, Integer> headerIndex = Map.of("input", 0, "expected", 1, "hidden", 2);
        String[] row = {"1 2", "3", "true"};
        ArrayList<TestCaseResponse> created = new ArrayList<>();
        ArrayList<com.hcmut.lms.assessment.dto.response.TestCaseImportResultResponse.ValidationError> errors = new ArrayList<>();

        when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

        boolean result = ReflectionTestUtils.invokeMethod(testCaseImportService,
                "importOneRow", questionId, row, 2, headerIndex, errors, created);

        assertThat(result).isFalse();
    }

    // --- helper methods ---

    private CodingQuestion createCodingQuestion(UUID id) {
        CodingQuestion q = new CodingQuestion();
        q.setId(id);
        q.setQuestionType(QuestionType.CODING);
        q.setContent("Write code");
        q.setExecutionTimeLimit(1000);
        q.setExecutionMemoryLimit(64);
        return q;
    }
}
