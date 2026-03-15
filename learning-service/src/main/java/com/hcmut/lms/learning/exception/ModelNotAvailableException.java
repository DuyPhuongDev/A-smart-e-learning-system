package com.hcmut.lms.learning.exception;

public class ModelNotAvailableException extends RuntimeException {
    public ModelNotAvailableException(String message) {
        super(message);
    }

    public ModelNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
