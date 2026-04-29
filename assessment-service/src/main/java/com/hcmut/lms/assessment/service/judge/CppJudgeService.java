package com.hcmut.lms.assessment.service.judge;

import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.CodingQuestion;
import com.hcmut.lms.assessment.exception.CodeJudgeUnavailableException;
import com.hcmut.lms.assessment.service.judge.dto.CodingJudgeEvaluation;
import com.hcmut.lms.assessment.service.judge.dto.JudgeVerdict;
import com.hcmut.lms.assessment.service.judge.dto.TestCaseJudgeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@Slf4j
public class CppJudgeService {

    private static final int COMPILE_TIMEOUT_SECONDS = 20;
    private static final int DETAIL_MAX_LENGTH = 600;
    private static final List<String> SUPPORTED_CPP_LANGUAGE_LIST = List.of(
            "cpp",
            "c++",
            "cpp17",
            "c++17",
            "gnu++17"
    );
    private static final Set<String> SUPPORTED_CPP_LANGUAGES = Set.copyOf(SUPPORTED_CPP_LANGUAGE_LIST);

    public CodingJudgeEvaluation evaluate(
            CodingQuestion codingQuestion,
            String sourceCode,
            String language,
            boolean isPrecheck
    ) {
        List<TestCase> testCases = codingQuestion.getTestCases() == null
                ? List.of()
                : codingQuestion.getTestCases();

        if(isPrecheck){
            testCases = testCases.stream().filter(tc -> !tc.isHidden()).toList();
        }

        validateQuestionConfiguration(codingQuestion, testCases);
        if (!isSupportedLanguage(language)) {
            throw new IllegalArgumentException("Only C++ language is supported for coding judge");
        }

        Path workDir = null;
        try {
            workDir = Files.createTempDirectory("lms-cpp-judge-");
            Path sourceFile = workDir.resolve("main.cpp");
            Path outputBinary = workDir.resolve("main");
            Files.writeString(sourceFile, sourceCode == null ? "" : sourceCode, StandardCharsets.UTF_8);

            ProcessExecution compileExec = executeProcess(
                    List.of("g++", "-std=c++17", "-O2", "-pipe", "-o", outputBinary.toString(), sourceFile.toString()),
                    workDir,
                    null,
                    COMPILE_TIMEOUT_SECONDS
            );

            if (compileExec.timedOut()) {
                return buildCompileErrorEvaluation(testCases, "Compilation timed out");
            }

            if (compileExec.exitCode() != 0) {
                String compileError = trimForDetail(compileExec.stderr());
                if (isCommandNotFound(compileExec.stderr())) {
                    throw new CodeJudgeUnavailableException("Code judge is unavailable: g++ is not installed");
                }
                String detail = compileError.isBlank() ? "Compile error" : "Compile error: " + compileError;
                return buildCompileErrorEvaluation(testCases, detail);
            }

            return runAllTestCases(codingQuestion, workDir, testCases);
        } catch (CodeJudgeUnavailableException ex) {
            throw ex;
        } catch (IOException ex) {
            throw new CodeJudgeUnavailableException("Code judge is unavailable: failed to prepare execution sandbox", ex);
        } finally {
            if (workDir != null) {
                deleteRecursively(workDir);
            }
        }
    }

    private CodingJudgeEvaluation runAllTestCases(
            CodingQuestion codingQuestion,
            Path workDir,
            List<TestCase> testCases
    ) {
        int passed = 0;
        List<TestCaseJudgeResult> testCaseResults = new ArrayList<>();

        for (TestCase testCase : testCases) {
            TestCaseJudgeResult result = runSingleTestCase(codingQuestion, workDir, testCase);
            testCaseResults.add(result);
            if (result.isPass()) {
                passed++;
            }
        }

        int total = testCaseResults.size();
        JudgeVerdict overallVerdict;
        if (passed == total) {
            overallVerdict = JudgeVerdict.AC;
        } else if (passed > 0) {
            overallVerdict = JudgeVerdict.WA;
        } else {
            overallVerdict = testCaseResults.stream()
                    .map(TestCaseJudgeResult::getVerdict)
                    .filter(v -> v != JudgeVerdict.WA)
                    .findFirst()
                    .orElse(JudgeVerdict.WA);
        }

        return CodingJudgeEvaluation.builder()
                .passedCount(passed)
                .totalCount(total)
                .overallVerdict(overallVerdict)
                .detail("Passed " + passed + "/" + total + " testcases")
                .testCaseResults(testCaseResults)
                .build();
    }

    private TestCaseJudgeResult runSingleTestCase(
            CodingQuestion codingQuestion,
            Path workDir,
            TestCase testCase
    ) {
        int timeLimitMs = codingQuestion.getExecutionTimeLimit();
        int memoryLimitMb = codingQuestion.getExecutionMemoryLimit();
        int processWaitTimeoutSeconds = Math.max(2, (int) Math.ceil(timeLimitMs / 1000.0) + 2);
        int memoryLimitKb = memoryLimitMb * 1024;
        String timeoutDuration = formatTimeoutDuration(timeLimitMs);

        String timeoutCmd = resolveTimeoutCommand();

        String runCmd = "ulimit -v " + memoryLimitKb + "; " + timeoutCmd +
                " --signal=KILL --kill-after=1s " + timeoutDuration + " ./main";

        ProcessExecution runExec;
        try {
            runExec = executeProcess(
                    List.of("bash", "-lc", runCmd),
                    workDir,
                    testCase.getInput(),
                    processWaitTimeoutSeconds
            );
        } catch (IOException ex) {
            throw new CodeJudgeUnavailableException("Code judge is unavailable: failed to execute compiled program", ex);
        }

        if (isCommandNotFound(runExec.stderr())) {
            throw new CodeJudgeUnavailableException("Code judge is unavailable: timeout runtime dependency is missing");
        }
        if (isTimeoutIntervalUnsupported(runExec.stderr())) {
            throw new CodeJudgeUnavailableException("Code judge is unavailable: timeout command does not support configured interval");
        }

        JudgeVerdict verdict;
        String detailError = null;
        boolean pass = false;

        if (runExec.timedOut() || runExec.exitCode() == 124 || runExec.exitCode() == 137) {
            verdict = JudgeVerdict.TLE;
            detailError = "Time limit exceeded";
        } else if (runExec.exitCode() != 0) {
            verdict = JudgeVerdict.RE;
            detailError = trimForDetail(runExec.stderr());
            if (detailError.isBlank()) {
                detailError = "Runtime error";
            }
        } else {
            boolean outputMatched = compareCompetitiveOutput(runExec.stdout(), testCase.getExpected());
            if (outputMatched) {
                verdict = JudgeVerdict.AC;
                pass = true;
            } else {
                verdict = JudgeVerdict.WA;
                detailError = "Wrong answer";
            }
        }

        return TestCaseJudgeResult.builder()
                .testCaseId(testCase.getId())
                .verdict(verdict)
                .pass(pass)
                .output(trimForDetail(runExec.stdout()))
                .error(detailError)
                .executionTimeMs((int) runExec.durationMs())
                .build();
    }

    private CodingJudgeEvaluation buildCompileErrorEvaluation(List<TestCase> testCases, String detail) {
        List<TestCaseJudgeResult> results = testCases.stream()
                .map(tc -> TestCaseJudgeResult.builder()
                        .testCaseId(tc.getId())
                        .verdict(JudgeVerdict.CE)
                        .pass(false)
                        .output(null)
                        .error(detail)
                        .executionTimeMs(0)
                        .build())
                .toList();

        return CodingJudgeEvaluation.builder()
                .passedCount(0)
                .totalCount(testCases.size())
                .overallVerdict(JudgeVerdict.CE)
                .detail(detail)
                .testCaseResults(results)
                .build();
    }

    private ProcessExecution executeProcess(
            List<String> command,
            Path workDir,
            String stdinText,
            int timeoutSeconds
    ) throws IOException {
        long startedAt = System.nanoTime();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.directory(workDir.toFile());
        Process process = processBuilder.start();

        if (stdinText != null) {
            try (OutputStream os = process.getOutputStream()) {
                os.write(stdinText.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }
        } else {
            process.getOutputStream().close();
        }

        CompletableFuture<String> stdoutFuture = readAsync(process.getInputStream());
        CompletableFuture<String> stderrFuture = readAsync(process.getErrorStream());

        boolean finished;
        try {
            finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            throw new CodeJudgeUnavailableException("Code judge interrupted unexpectedly", ex);
        }

        if (!finished) {
            process.destroyForcibly();
        }

        String stdout;
        String stderr;
        try {
            stdout = stdoutFuture.get(2, TimeUnit.SECONDS);
            stderr = stderrFuture.get(2, TimeUnit.SECONDS);
        } catch (Exception ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof UncheckedIOException uncheckedIOException) {
                throw new CodeJudgeUnavailableException(
                        "Code judge failed to capture process output",
                        uncheckedIOException.getCause()
                );
            }
            throw new CodeJudgeUnavailableException("Code judge failed to capture process output", ex);
        }

        int exitCode = finished ? process.exitValue() : -1;
        long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);

        return new ProcessExecution(exitCode, stdout, stderr, durationMs, !finished);
    }

    private CompletableFuture<String> readAsync(InputStream inputStream) {
        return CompletableFuture.supplyAsync(() -> {
            try (inputStream) {
                return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            } catch (IOException ex) {
                throw new UncheckedIOException("Failed to read process stream", ex);
            }
        });
    }

    private boolean compareCompetitiveOutput(String actualOutput, String expectedOutput) {
        return normalizeOutput(actualOutput).equals(normalizeOutput(expectedOutput));
    }

    private String normalizeOutput(String text) {
        if (text == null) {
            return "";
        }

        String normalizedNewline = text.replace("\r\n", "\n").replace('\r', '\n');
        String[] lines = normalizedNewline.split("\n", -1);
        List<String> normalizedLines = new ArrayList<>(lines.length);
        for (String line : lines) {
            normalizedLines.add(rtrim(line));
        }

        int last = normalizedLines.size() - 1;
        while (last >= 0 && normalizedLines.get(last).isEmpty()) {
            last--;
        }

        if (last < 0) {
            return "";
        }

        return String.join("\n", normalizedLines.subList(0, last + 1));
    }

    private String rtrim(String line) {
        int end = line.length();
        while (end > 0) {
            char ch = line.charAt(end - 1);
            if (ch == ' ' || ch == '\t') {
                end--;
            } else {
                break;
            }
        }
        return line.substring(0, end);
    }

    private String trimForDetail(String text) {
        if (text == null || text.isBlank()) {
            return "";
        }
        String normalized = text.strip();
        return normalized.length() > DETAIL_MAX_LENGTH
                ? normalized.substring(0, DETAIL_MAX_LENGTH) + "...(truncated)"
                : normalized;
    }

    private boolean isCommandNotFound(String stderr) {
        if (stderr == null) {
            return false;
        }
        return stderr.toLowerCase(Locale.ROOT).contains("command not found");
    }

    private boolean isTimeoutIntervalUnsupported(String stderr) {
        if (stderr == null) {
            return false;
        }
        String normalized = stderr.toLowerCase(Locale.ROOT);
        return normalized.contains("invalid time interval");
    }

    private String formatTimeoutDuration(int timeLimitMs) {
        double seconds = Math.max(1, timeLimitMs) / 1000.0;
        return String.format(Locale.ROOT, "%.3fs", seconds);
    }

    public boolean isSupportedLanguage(String language) {
        if (language == null) {
            return false;
        }
        return SUPPORTED_CPP_LANGUAGES.contains(language.trim().toLowerCase(Locale.ROOT));
    }

    public String supportedLanguagesDescription() {
        return String.join(", ", SUPPORTED_CPP_LANGUAGE_LIST);
    }

    private void validateQuestionConfiguration(CodingQuestion codingQuestion, List<TestCase> testCases) {
        if (testCases.isEmpty()) {
            throw new IllegalArgumentException("Coding question has no test cases configured");
        }
        if (codingQuestion.getExecutionTimeLimit() <= 0) {
            throw new IllegalArgumentException("Coding question execution time limit must be > 0");
        }
        if (codingQuestion.getExecutionMemoryLimit() <= 0) {
            throw new IllegalArgumentException("Coding question execution memory limit must be > 0");
        }
    }

    private void deleteRecursively(Path root) {
        try (Stream<Path> walk = Files.walk(root)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ex) {
                            log.warn("Cannot delete judge temp path {}", path, ex);
                        }
                    });
        } catch (IOException ex) {
            log.warn("Cannot cleanup judge temp dir {}", root, ex);
        }
    }

    private record ProcessExecution(
            int exitCode,
            String stdout,
            String stderr,
            long durationMs,
            boolean timedOut
    ) {
    }

    private String resolveTimeoutCommand() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            return "gtimeout";
        }
        return "timeout";
    }
}
