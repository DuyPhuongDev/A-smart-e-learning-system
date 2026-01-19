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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.http.server.ServletServerHttpResponse;
import jakarta.servlet.http.HttpServletResponse;

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

        // Cho phép xử lý cả ResponseEntity và các return type khác
        // Khi controller trả về ResponseEntity<T>, Spring sẽ unwrap và body sẽ được truyền vào beforeBodyWrite()
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

        // if internal -> no wrap
        if(path.contains("internal")) return body;

        // Xác định status code: ưu tiên lấy từ response, nếu không có thì suy luận từ HTTP method
        int statusCode = determineStatusCode(returnType, request, response);
        
        // Nếu body là String (thường là error message từ exception handlers), dùng làm message
        String message = "Success";
        if (body instanceof String) {
            message = (String) body;
        }

        // Nếu body là null (ví dụ: @ResponseStatus(NO_CONTENT) hoặc void return type)
        // Vẫn wrap vào ApiResponse format để đảm bảo consistency
        if (body == null) {
            return ApiResponse.builder()
                    .status(statusCode)
                    .message(statusCode == HttpStatus.NO_CONTENT.value() ? "No Content" : message)
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(path)
                    .build();
        }

        // Nếu body là String (error message), đặt vào message và data = null
        if (body instanceof String) {
            return ApiResponse.builder()
                    .status(statusCode)
                    .message((String) body)
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .path(path)
                    .build();
        }

        return ApiResponse.builder()
                .status(statusCode)
                .message(message)
                .data(body)
                .timestamp(LocalDateTime.now())
                .path(path)
                .build();
    }

    /**
     * Xác định status code dựa trên @ResponseStatus annotation, response và HTTP method
     * Best practice: Ưu tiên @ResponseStatus > HttpServletResponse > suy luận từ HTTP method
     */
    private int determineStatusCode(MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        // Ưu tiên 1: Kiểm tra @ResponseStatus annotation trên method
        ResponseStatus responseStatus = returnType.getMethodAnnotation(ResponseStatus.class);
        if (responseStatus != null) {
            return responseStatus.value().value();
        }
        
        // Ưu tiên 2: Lấy status code từ HttpServletResponse (nếu đã được set)
        if (response instanceof ServletServerHttpResponse) {
            HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
            int status = servletResponse.getStatus();
            // Nếu status code đã được set (khác 0), sử dụng nó
            if (status > 0) {
                return status;
            }
        }
        
        // Ưu tiên 3: Suy luận từ HTTP method nếu return type là ResponseEntity
        Class<?> returnTypeClass = returnType.getParameterType();
        if (ResponseEntity.class.isAssignableFrom(returnTypeClass)) {
            String method = request.getMethod() != null ? request.getMethod().name() : "GET";
            return switch (method) {
                case "POST" -> HttpStatus.CREATED.value(); // 201
                case "PUT", "PATCH" -> HttpStatus.OK.value(); // 200
                case "DELETE" -> HttpStatus.NO_CONTENT.value(); // 204
                default -> HttpStatus.OK.value(); // 200
            };
        }
        
        // Mặc định: 200 OK
        return HttpStatus.OK.value();
    }
}

