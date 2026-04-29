package com.hcmut.lms.personalization.exception;

/**
 * Thrown when student progress data is unavailable or incomplete,
 * making it impossible to perform a meaningful validation.
 */
public class StudentDataUnavailableException extends RuntimeException {

    private final String field;

    public StudentDataUnavailableException(String message) {
        super(message);
        this.field = null;
    }

    public StudentDataUnavailableException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}