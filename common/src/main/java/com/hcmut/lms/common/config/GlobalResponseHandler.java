package com.hcmut.lms.common.config;

import com.hcmut.lms.common.dto.ApiResponse;
import com.hcmut.lms.common.exception.GlobalExceptionHandler;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.LocalDateTime;

/**
 * Global Response Handler - Tự động wrap tất cả API responses vào ApiResponse format
 * 
 * Sử dụng trong tất cả microservices để đảm bảo consistent response format.
 * 
 * Response format:
 * {
 *   "status": 200,
 *   "message": "Success",
 *   "data": {...},
 *   "timestamp": "2024-01-15T10:30:00",
 *   "path": "/api/resource"
 * }
 */
@RestControllerAdvice
@SuppressWarnings({"NullableProblems", "null"})
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        String declaringClassName = returnType.getDeclaringClass().getName();

        // Bỏ qua nếu là handler của Swagger / Springdoc
        if (declaringClassName.startsWith("org.springdoc") || 
            declaringClassName.startsWith("org.springframework.boot.actuate")) {
            return false;
        }

        // Bỏ qua GlobalExceptionHandler vì đã có format riêng
        if (returnType.getDeclaringClass().equals(GlobalExceptionHandler.class)) {
            return false;
        }

        // Bỏ qua nếu return type là ResponseEntity (sẽ được xử lý riêng)
        if (returnType.getParameterType().equals(ResponseEntity.class)) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        // Nếu đã là ApiResponse thì không wrap lại
        if (body instanceof ApiResponse) {
            return body;
        }

        String path = "";
        if (request instanceof ServletServerHttpRequest) {
            path = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
        }

        return ApiResponse.builder()
                .status(HttpStatus.OK.value())
                .message("Success")
                .data(body)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }
}

