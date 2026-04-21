package com.hcmut.lms.assessment.service.impl;

import com.hcmut.lms.assessment.domain.entity.question.DifficultLevel;
import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import com.hcmut.lms.assessment.dto.request.question.AnswerOptionRequest;
import com.hcmut.lms.assessment.dto.request.question.EssayQuestionRequest;
import com.hcmut.lms.assessment.dto.request.question.McqQuestionRequest;
import com.hcmut.lms.assessment.dto.response.QuestionImportResultResponse;
import com.hcmut.lms.assessment.dto.response.QuestionResponse;
import com.hcmut.lms.assessment.exception.BadRequestException;
import com.hcmut.lms.assessment.service.QuestionImportService;
import com.hcmut.lms.assessment.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
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
public class QuestionImportServiceImpl implements QuestionImportService {

    private final QuestionService questionService;

    @Override
    public QuestionImportResultResponse importMcqQuestions(MultipartFile file) {
        return importQuestionsFromCsv(file, QuestionType.MCQ);
    }

    @Override
    public QuestionImportResultResponse importEssayQuestions(MultipartFile file) {
        return importQuestionsFromCsv(file, QuestionType.ESSAY);
    }

    private QuestionImportResultResponse importQuestionsFromCsv(MultipartFile file, QuestionType expectedType) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("CSV file is empty");
        }

        String filename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "";
        if (!filename.toLowerCase().endsWith(".csv")) {
            throw new BadRequestException("Only .csv files are supported");
        }

        List<QuestionImportResultResponse.ValidationError> errors = new ArrayList<>();
        List<QuestionResponse> createdQuestions = new ArrayList<>();
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

            if (expectedType == QuestionType.MCQ) {
                requireColumns(headerIndex, List.of(
                        "questionType", "difficultLevel", "point", "content", "required", "questionBankId",
                        "allowMultiAnswer", "shuffleOption",
                        "option1", "option2", "option3", "option4",
                        "correctAnswers",
                        "explanation1", "explanation2", "explanation3", "explanation4"
                ));
            } else if (expectedType == QuestionType.ESSAY) {
                requireColumns(headerIndex, List.of(
                        "questionType", "difficultLevel", "point", "content", "required", "questionBankId",
                        "sampleAnswer", "maxFileSize", "acceptedFileTypes"
                ));
            } else {
                throw new BadRequestException("Unsupported import type: " + expectedType);
            }

            String[] row;
            int rowNumber = 1; // header row = 1
            while ((row = reader.readNext()) != null) {
                rowNumber++;

                boolean rowOk = switch (expectedType) {
                    case MCQ -> importSingleMcqRow(row, rowNumber, headerIndex, errors, createdQuestions);
                    case ESSAY -> importSingleEssayRow(row, rowNumber, headerIndex, errors, createdQuestions);
                    default -> false;
                };

                if (rowOk) {
                    successCount++;
                } else {
                    failedRows.add(rowNumber);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read import CSV file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to read CSV file", e);
        } catch (CsvValidationException e) {
            log.error("Failed to parse import CSV: {}", e.getMessage(), e);
            throw new BadRequestException("Invalid CSV format: " + e.getMessage());
        }

        return QuestionImportResultResponse.builder()
                .successCount(successCount)
                .failedCount(failedRows.size())
                .errors(errors.isEmpty() ? null : errors)
                .questions(createdQuestions.isEmpty() ? null : createdQuestions)
                .build();
    }

    private boolean importSingleMcqRow(
            String[] row,
            int rowNumber,
            Map<String, Integer> headerIndex,
            List<QuestionImportResultResponse.ValidationError> errors,
            List<QuestionResponse> createdQuestions
    ) {
        try {
            String questionTypeStr = getCell(row, headerIndex, "questionType");
            if (isBlank(questionTypeStr)) {
                addError(errors, rowNumber, "questionType", "questionType is required");
                return false;
            }

            QuestionType questionType = parseEnumIgnoreCase(QuestionType.class, questionTypeStr);
            if (questionType != QuestionType.MCQ) {
                addError(errors, rowNumber, "questionType", "Expected questionType=MCQ but got: " + questionTypeStr);
                return false;
            }

            DifficultLevel difficultLevel = parseEnumIgnoreCase(DifficultLevel.class, getRequiredCell(row, headerIndex, "difficultLevel"));
            BigDecimal point = new BigDecimal(getRequiredCell(row, headerIndex, "point"));
            String content = getRequiredCell(row, headerIndex, "content");
            boolean required = parseBooleanStrict(getRequiredCell(row, headerIndex, "required"));

            // questionBankId is optional – empty -> null
            UUID questionBankId = null;
            String bankIdRaw = getCell(row, headerIndex, "questionBankId");
            if (!isBlank(bankIdRaw)) {
                questionBankId = parseUuidStrict(bankIdRaw);
            }

            boolean allowMultiAnswer = parseBooleanStrict(getRequiredCell(row, headerIndex, "allowMultiAnswer"));
            boolean shuffleOption = parseBooleanStrict(getRequiredCell(row, headerIndex, "shuffleOption"));

            String[] optionContents = new String[4];
            for (int i = 0; i < 4; i++) {
                String option = getRequiredCell(row, headerIndex, "option" + (i + 1));
                optionContents[i] = option;
            }

            String correctAnswersStr = getRequiredCell(row, headerIndex, "correctAnswers");
            Set<Integer> correctOptionNumbers = parseCorrectOptionNumbers(correctAnswersStr, rowNumber, errors);
            if (correctOptionNumbers.isEmpty()) {
                return false;
            }

            if (!allowMultiAnswer && correctOptionNumbers.size() > 1) {
                addError(errors, rowNumber, "allowMultiAnswer", "MCQ allowMultiAnswer=false nhưng correctAnswers có nhiều đáp án");
                return false;
            }

            // Validate that all referenced correct options have non-empty content
            for (Integer optionNumber : correctOptionNumbers) {
                if (optionNumber < 1 || optionNumber > 4) continue;
                if (isBlank(optionContents[optionNumber - 1])) {
                    addError(errors, rowNumber, "option" + optionNumber, "Option được đánh dấu đúng nhưng content đang rỗng");
                    return false;
                }
            }

            List<AnswerOptionRequest> answerOptions = new ArrayList<>(4);
            for (int i = 0; i < 4; i++) {
                int optionNumber = i + 1;
                boolean correct = correctOptionNumbers.contains(optionNumber);
                String explanation = getCell(row, headerIndex, "explanation" + optionNumber);

                answerOptions.add(AnswerOptionRequest.builder()
                        .content(optionContents[i])
                        .correct(correct)
                        .orderIndex(i+1)
                        .explanation(explanation)
                        .build());
            }

            McqQuestionRequest request = new McqQuestionRequest();
            request.setQuestionType(QuestionType.MCQ);
            request.setDifficultLevel(difficultLevel);
            request.setPoint(point);
            request.setContent(content);
            request.setRequired(required);
            request.setQuestionBankId(questionBankId);
            request.setAllowMultiAnswer(allowMultiAnswer);
            request.setShuffleOption(shuffleOption);
            request.setAnswerOptions(answerOptions);

            QuestionResponse created = questionService.createQuestion(request);
            createdQuestions.add(created);
            return true;
        } catch (BadRequestException e) {
            addError(errors, rowNumber, "General", e.getMessage());
            return false;
        } catch (Exception e) {
            addError(errors, rowNumber, "General", e.getMessage());
            return false;
        }
    }

    private boolean importSingleEssayRow(
            String[] row,
            int rowNumber,
            Map<String, Integer> headerIndex,
            List<QuestionImportResultResponse.ValidationError> errors,
            List<QuestionResponse> createdQuestions
    ) {
        try {
            String questionTypeStr = getCell(row, headerIndex, "questionType");
            if (isBlank(questionTypeStr)) {
                addError(errors, rowNumber, "questionType", "questionType is required");
                return false;
            }

            QuestionType questionType = parseEnumIgnoreCase(QuestionType.class, questionTypeStr);
            if (questionType != QuestionType.ESSAY) {
                addError(errors, rowNumber, "questionType", "Expected questionType=ESSAY but got: " + questionTypeStr);
                return false;
            }

            DifficultLevel difficultLevel = parseEnumIgnoreCase(DifficultLevel.class, getRequiredCell(row, headerIndex, "difficultLevel"));
            BigDecimal point = new BigDecimal(getRequiredCell(row, headerIndex, "point"));
            String content = getRequiredCell(row, headerIndex, "content");
            boolean required = parseBooleanStrict(getRequiredCell(row, headerIndex, "required"));

            // questionBankId is optional – empty -> null
            UUID questionBankId = null;
            String bankIdRaw = getCell(row, headerIndex, "questionBankId");
            if (!isBlank(bankIdRaw)) {
                questionBankId = parseUuidStrict(bankIdRaw);
            }

            String sampleAnswer = normalizeNullable(getCell(row, headerIndex, "sampleAnswer"));
            if (isBlank(sampleAnswer)) {
                addError(errors, rowNumber, "sampleAnswer", "sampleAnswer is required");
                return false;
            }

            int maxFileSize;
            try {
                maxFileSize = Integer.parseInt(getRequiredCell(row, headerIndex, "maxFileSize"));
            } catch (Exception e) {
                addError(errors, rowNumber, "maxFileSize", "Invalid maxFileSize");
                return false;
            }

            String acceptedFileTypesRaw = getCell(row, headerIndex, "acceptedFileTypes");
            List<String> acceptedFileTypes = parseAcceptedFileTypes(acceptedFileTypesRaw);

            EssayQuestionRequest request = new EssayQuestionRequest();
            request.setQuestionType(QuestionType.ESSAY);
            request.setDifficultLevel(difficultLevel);
            request.setPoint(point);
            request.setContent(content);
            request.setRequired(required);
            request.setQuestionBankId(questionBankId);
            request.setSampleAnswer(sampleAnswer);
            request.setMaxFileSize(maxFileSize);
            request.setAcceptedFileTypes(acceptedFileTypes);

            QuestionResponse created = questionService.createQuestion(request);
            createdQuestions.add(created);
            return true;
        } catch (BadRequestException e) {
            addError(errors, rowNumber, "General", e.getMessage());
            return false;
        } catch (Exception e) {
            addError(errors, rowNumber, "General", e.getMessage());
            return false;
        }
    }

    private Map<String, Integer> toHeaderIndex(String[] header) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < header.length; i++) {
            if (header[i] == null) continue;
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
        if (idx == null || idx < 0) return null;
        if (idx >= row.length) return null;
        String value = row[idx];
        return value == null ? null : value.trim();
    }

    private String getRequiredCell(String[] row, Map<String, Integer> headerIndex, String columnName) {
        String value = getCell(row, headerIndex, columnName);
        if (isBlank(value)) {
            throw new BadRequestException("Missing required column '" + columnName + "'");
        }
        return value;
    }

    private <E extends Enum<E>> E parseEnumIgnoreCase(Class<E> enumType, String value) {
        if (value == null) {
            throw new BadRequestException("Value is required");
        }
        String normalized = value.trim().toUpperCase();
        try {
            return Enum.valueOf(enumType, normalized);
        } catch (Exception e) {
            throw new BadRequestException("Invalid " + enumType.getSimpleName() + ": " + value);
        }
    }

    private boolean parseBooleanStrict(String value) {
        if (value == null) throw new BadRequestException("Boolean value is required");
        String v = value.trim().toLowerCase();
        if ("true".equals(v)) return true;
        if ("false".equals(v)) return false;
        throw new BadRequestException("Invalid boolean: " + value + " (expected true/false)");
    }

    private UUID parseUuidStrict(String value) {
        try {
            return UUID.fromString(value.trim());
        } catch (Exception e) {
            throw new BadRequestException("Invalid UUID: " + value);
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private String normalizeNullable(String s) {
        if (s == null) return null;
        String trimmed = s.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Set<Integer> parseCorrectOptionNumbers(
            String correctAnswersStr,
            int rowNumber,
            List<QuestionImportResultResponse.ValidationError> errors
    ) {
        String normalized = correctAnswersStr.trim();
        if (normalized.isEmpty()) {
            addError(errors, rowNumber, "correctAnswers", "correctAnswers is required");
            return Set.of();
        }

        Set<Integer> correctSet = new HashSet<>();
        String[] parts = normalized.split(";");
        for (String part : parts) {
            String token = part.trim();
            if (token.isEmpty()) continue;
            try {
                int optionNumber = Integer.parseInt(token);
                if (optionNumber < 1 || optionNumber > 4) {
                    addError(errors, rowNumber, "correctAnswers", "Invalid option number in correctAnswers: " + token);
                    return Set.of();
                }
                correctSet.add(optionNumber);
            } catch (Exception e) {
                addError(errors, rowNumber, "correctAnswers", "Invalid number in correctAnswers: " + token);
                return Set.of();
            }
        }
        return correctSet;
    }

    private List<String> parseAcceptedFileTypes(String acceptedFileTypesRaw) {
        String raw = acceptedFileTypesRaw == null ? "" : acceptedFileTypesRaw.trim();
        if (raw.isEmpty()) return null;
        String[] parts = raw.split(";");
        List<String> result = new ArrayList<>();
        for (String p : parts) {
            String t = p.trim();
            if (!t.isEmpty()) result.add(t);
        }
        return result.isEmpty() ? null : result;
    }

    private void addError(
            List<QuestionImportResultResponse.ValidationError> errors,
            int rowNumber,
            String field,
            String message
    ) {
        errors.add(QuestionImportResultResponse.ValidationError.builder()
                .row(rowNumber)
                .field(field)
                .message(message)
                .build());
    }
}

