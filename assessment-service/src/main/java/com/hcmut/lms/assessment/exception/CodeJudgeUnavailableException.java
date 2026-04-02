package com.hcmut.lms.assessment.exception;

public class CodeJudgeUnavailableException extends RuntimeException {
    public CodeJudgeUnavailableException(String message) {
        super(message);
    }

    public CodeJudgeUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}

