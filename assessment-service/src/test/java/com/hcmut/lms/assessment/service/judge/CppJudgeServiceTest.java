package com.hcmut.lms.assessment.service.judge;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CppJudgeServiceTest {

    private final CppJudgeService cppJudgeService = new CppJudgeService();

    @Test
    void isSupportedLanguageShouldAcceptConfiguredAliasesCaseInsensitive() {
        assertTrue(cppJudgeService.isSupportedLanguage("CPP"));
        assertTrue(cppJudgeService.isSupportedLanguage(" c++17 "));
        assertTrue(cppJudgeService.isSupportedLanguage("gnu++17"));
        assertFalse(cppJudgeService.isSupportedLanguage("python"));
    }

    @Test
    void evaluateShouldRejectUnsupportedLanguageBeforeExecution() {
        CodingQuestion question = buildValidQuestion();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cppJudgeService.evaluate(question, "int main(){return 0;}", "python")
        );

        assertTrue(ex.getMessage().contains("Only C++ language is supported"));
    }

    @Test
    void evaluateShouldRejectInvalidQuestionConfigWithoutTestcases() {
        CodingQuestion question = CodingQuestion.builder()
                .executionTimeLimit(1000)
                .executionMemoryLimit(64)
                .testCases(List.of())
                .build();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cppJudgeService.evaluate(question, "int main(){return 0;}", "cpp")
        );

        assertTrue(ex.getMessage().contains("no test cases"));
    }

    @Test
    void normalizeOutputShouldFollowCompetitiveRules() throws Exception {
        Method normalizeOutput = CppJudgeService.class.getDeclaredMethod("normalizeOutput", String.class);
        normalizeOutput.setAccessible(true);

        String normalized = (String) normalizeOutput.invoke(cppJudgeService, "1  \r\n2\t\r\n\r\n");
        String expected = (String) normalizeOutput.invoke(cppJudgeService, "1\n2\n");

        assertEquals("1\n2", normalized);
        assertEquals(expected, normalized);
    }

    private CodingQuestion buildValidQuestion() {
        TestCase testCase = TestCase.builder()
                .input("1\n")
                .expected("1\n")
                .hidden(false)
                .build();

        return CodingQuestion.builder()
                .executionTimeLimit(1000)
                .executionMemoryLimit(64)
                .testCases(new ArrayList<>(List.of(testCase)))
                .build();
    }
}
