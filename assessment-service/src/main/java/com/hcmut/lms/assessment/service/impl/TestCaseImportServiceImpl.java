package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.TestCaseRequest;
import com.hcmut.lms.assessment.dto.response.TestCaseImportResultResponse;
import com.hcmut.lms.assessment.dto.response.TestCaseResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.exception.ResourceNotFoundException;
import com.hcmut.lms.assessment.mapper.QuestionMapper;
import com.hcmut.lms.assessment.repository.QuestionRepository;
import com.hcmut.lms.assessment.repository.TestCaseRepository;
import com.hcmut.lms.assessment.service.TestCaseImportService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestCaseImportServiceImpl implements TestCaseImportService {

    private static final List<String> REQUIRED_COLUMNS = List.of(
            "input", "expected", "hidden"
    );

    private final TestCaseRepository testCaseRepository;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public TestCaseImportResultResponse importTestCases(UUID questionId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("CSV file is empty");
        }

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        if (!filename.toLowerCase().endsWith(".csv")) {
            throw new BadRequestException("Only .csv files are supported");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        if (question.getQuestionType() != QuestionType.CODING || !(question instanceof CodingQuestion)) {
            throw new BadRequestException("Question is not a CODING question: " + questionId);
        }
        CodingQuestion coding = (CodingQuestion) question;

        List<TestCaseImportResultResponse.ValidationError> errors = new ArrayList<>();
        List<TestCaseResponse> created = new ArrayList<>();
        Set<Integer> failedRows = new HashSet<>();
        int successCount = 0;

        try (CSVReader reader = new CSVReaderBuilder(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .withSkipLines(0)
                .build()) {

            String[] header = reader.readNext();
            if (header == null || header.length == 0) {
                throw new BadRequestException("CSV file is empty (missing header)");
            }

            Map<String, Integer> headerIndex = toHeaderIndex(header);
            requireColumns(headerIndex, REQUIRED_COLUMNS);

            String[] row;
            int rowNumber = 1;
            while ((row = reader.readNext()) != null) {
                rowNumber++;
                if (isRowEmpty(row, headerIndex)) {
                    continue;
                }

                boolean ok = importOneRow(coding.getId(), row, rowNumber, headerIndex, errors, created);
                if (ok) {
                    successCount++;
                } else {
                    failedRows.add(rowNumber);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read testcase CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read CSV file", e);
        } catch (CsvValidationException e) {
            log.error("Failed to parse testcase CSV: {}", e.getMessage(), e);
            throw new BadRequestException("Invalid CSV format: " + e.getMessage());
        }

        return TestCaseImportResultResponse.builder()
                .successCount(successCount)
                .failedCount(failedRows.size())
                .errors(errors.isEmpty() ? null : errors)
                .testCases(created.isEmpty() ? null : created)
                .build();
    }

    @Override
    public TestCaseResponse createTestCase(UUID questionId, TestCaseRequest request) {
        Question question =  questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));

        if (question.getQuestionType() != QuestionType.CODING || !(question instanceof CodingQuestion)) {
            throw new BadRequestException("Question is not a CODING question: " + questionId);
        }
        TestCase testCase = TestCase.builder()
                .input(request.getInput())
                .expected(request.getExpected())
                .hidden(request.isHidden())
                .codingQuestion((CodingQuestion) question)
                .build();
        return TestCaseResponse.toResponse(testCaseRepository.save(testCase));
    }

    private boolean importOneRow(
            UUID codingQuestionId,
            String[] row,
            int rowNumber,
            Map<String, Integer> headerIndex,
            List<TestCaseImportResultResponse.ValidationError> errors,
            List<TestCaseResponse> created

    ) {
        try {
            String input = unescapeTestCaseField(cellOrEmpty(row, headerIndex, "input"));
            String expected = unescapeTestCaseField(cellOrEmpty(row, headerIndex, "expected"));
            Boolean hiddenObj = parseHidden(row, headerIndex, rowNumber, errors);
            if (hiddenObj == null) {
                return false;
            }
            boolean hidden = hiddenObj;

            TestCaseRequest testCase = TestCaseRequest.builder()
                    .input(input)
                    .expected(expected)
                    .hidden(hidden)
                    .build();

            TestCaseResponse testCaseResponse = this.createTestCase(codingQuestionId, testCase);

            created.add(testCaseResponse);
            return true;
        } catch (Exception e) {
            addError(errors, rowNumber, "General", e.getMessage() != null ? e.getMessage() : "Import failed");
            return false;
        }
    }

    private Boolean parseHidden(
            String[] row,
            Map<String, Integer> headerIndex,
            int rowNumber,
            List<TestCaseImportResultResponse.ValidationError> errors
    ) {
        String v = getCell(row, headerIndex, "hidden");
        if (isBlank(v)) {
            return false;
        }
        String lower = v.trim().toLowerCase();
        if ("true".equals(lower)) {
            return true;
        }
        if ("false".equals(lower)) {
            return false;
        }
        addError(errors, rowNumber, "hidden", "Invalid boolean: " + v + " (expected true/false)");
        return null;
    }

    private String cellOrEmpty(String[] row, Map<String, Integer> headerIndex, String column) {
        String v = getCell(row, headerIndex, column);
        return v == null ? "" : v;
    }

    /**
     * CSV cells often contain literal backslash sequences (e.g. {@code 1\n2}) instead of real newlines.
     */
    private static String unescapeTestCaseField(String raw) {
        if (raw == null || raw.isEmpty()) {
            return raw;
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\' && i + 1 < raw.length()) {
                char n = raw.charAt(i + 1);
                switch (n) {
                    case 'n' -> {
                        sb.append('\n');
                        i++;
                    }
                    case 'r' -> {
                        sb.append('\r');
                        i++;
                    }
                    case 't' -> {
                        sb.append('\t');
                        i++;
                    }
                    case '\\' -> {
                        sb.append('\\');
                        i++;
                    }
                    case '"' -> {
                        sb.append('"');
                        i++;
                    }
                    default -> sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private boolean isRowEmpty(String[] row, Map<String, Integer> headerIndex) {
        for (String col : REQUIRED_COLUMNS) {
            Integer idx = headerIndex.get(col);
            if (idx == null || idx < 0 || idx >= row.length) {
                continue;
            }
            String v = row[idx];
            if (v != null && !v.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private Map<String, Integer> toHeaderIndex(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            if (header[i] == null) {
                continue;
            }
            String key = header[i].trim();
            if (!key.isEmpty()) {
                map.put(key, i);
            }
        }
        return map;
    }

    private void requireColumns(Map<String, Integer> headerIndex, List<String> requiredColumns) {
        List<String> missing = requiredColumns.stream()
                .filter(c -> !headerIndex.containsKey(c))
                .toList();
        if (!missing.isEmpty()) {
            throw new BadRequestException("CSV template missing columns: " + String.join(", ", missing));
        }
    }

    private String getCell(String[] row, Map<String, Integer> headerIndex, String columnName) {
        Integer idx = headerIndex.get(columnName);
        if (idx == null || idx < 0) {
            return null;
        }
        if (idx >= row.length) {
            return null;
        }
        String value = row[idx];
        return value == null ? null : value;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private void addError(
            List<TestCaseImportResultResponse.ValidationError> errors,
            int rowNumber,
            String field,
            String message
    ) {
        errors.add(TestCaseImportResultResponse.ValidationError.builder()
                .row(rowNumber)
                .field(field)
                .message(message)
                .build());
    }
}
