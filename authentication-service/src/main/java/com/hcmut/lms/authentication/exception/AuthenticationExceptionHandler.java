package com.hcmut.lms.authentication.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.hcmut.lms.authentication")
public class AuthenticationExceptionHandler {
    
    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleInvalidCredentialsException(InvalidCredentialsException ex) {
        log.warn("Invalid credentials: {}", ex.getMessage());
        return ex.getMessage();
    }
    
    @ExceptionHandler(AccountLockedException.class)
    @ResponseStatus(HttpStatus.LOCKED)
    public Map<String, Object> handleAccountLockedException(AccountLockedException ex) {
        log.warn("Account locked: {}", ex.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("message", ex.getMessage());
        response.put("lockedUntil", ex.getLockedUntil());
        return response;
    }
    
    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleTokenExpiredException(TokenExpiredException ex) {
        log.warn("Token expired: {}", ex.getMessage());
        return ex.getMessage();
    }
    
    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleInvalidTokenException(InvalidTokenException ex) {
        log.warn("Invalid token: {}", ex.getMessage());
        return ex.getMessage();
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ex.getMessage();
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicateResourceException(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ex.getMessage();
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return errors;
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex) {
        log.error("Unexpected error: ", ex);
        return "An unexpected error occurred";
    }
}

