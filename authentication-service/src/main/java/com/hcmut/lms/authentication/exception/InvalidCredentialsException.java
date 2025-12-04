package com.hcmut.lms.authentication.exception;

public class InvalidCredentialsException extends AuthenticationException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}

