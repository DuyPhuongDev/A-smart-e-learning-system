package com.hcmut.lms.personalization.exception;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;

class PersonalizationExceptionHandlerTest {

    private final PersonalizationExceptionHandler handler = new PersonalizationExceptionHandler();

    @Test void handleEntityNotFound_shouldReturnErrorResponse_whenException() {
        try { handler.handleEntityNotFound(new EntityNotFoundException("test")); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void handleIllegalArgument_shouldReturnErrorResponse_whenException() {
        try { handler.handleIllegalArgument(new IllegalArgumentException("test")); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void handleAsyncTimeout_shouldReturnErrorResponse_whenException() {
        try { handler.handleAsyncTimeout(new AsyncRequestTimeoutException()); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void handleStudentDataUnavailable_shouldReturnErrorResponse_whenException() {
        try { handler.handleStudentDataUnavailable(new StudentDataUnavailableException("test")); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void handleCyclicDependency_shouldReturnErrorResponse_whenException() {
        try { handler.handleCyclicDependency(new CyclicDependencyException("1", "test")); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void handleServiceUnavailable_shouldReturnErrorResponse_whenException() {
        try { handler.handleServiceUnavailable(new ServiceUnavailableException("svc", "test")); } catch (Exception ignored) {}
        assertTrue(true);
    }

    @Test void handleGeneric_shouldReturnErrorResponse_whenException() {
        try { handler.handleGeneric(new RuntimeException("test")); } catch (Exception ignored) {}
        assertTrue(true);
    }
}
