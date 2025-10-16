package com.hcmut.lms.usermanagement.exception;

import com.hcmut.lms.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUserNotFound(UserNotFoundException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleRoleNotFound(RoleNotFoundException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(EndpointNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEndpointNotFound(EndpointNotFoundException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(DuplicateRoleNameException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateRoleName(DuplicateRoleNameException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(SystemRoleModificationException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemRoleModification(SystemRoleModificationException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.FORBIDDEN.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(RoleInUseException.class)
    public ResponseEntity<ApiResponse<Void>> handleRoleInUse(RoleInUseException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.CONFLICT.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
    
    @ExceptionHandler(ExcelProcessingException.class)
    public ResponseEntity<ApiResponse<Void>> handleExcelProcessing(ExcelProcessingException e, WebRequest request) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Validation failed")
                .data(errors)
                .timestamp(LocalDateTime.now())
                .path(request.getDescription(false).replace("uri=", ""))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

