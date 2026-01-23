package com.hcmut.lms.usermanagement.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ImportValidationException extends RuntimeException {
    
    private final List<ValidationError> errors;
    
    public ImportValidationException(List<ValidationError> errors) {
        super("Import validation failed");
        this.errors = errors;
    }
    
    /**
     * Get errors grouped by error type with row numbers
     * Example: {"Email đã tồn tại": [2, 5, 10], "Thiếu họ tên": [3, 7]}
     */
    public Map<String, List<Integer>> getErrorsGroupedByType() {
        return errors.stream()
                .collect(Collectors.groupingBy(
                        ValidationError::getMessage,
                        Collectors.mapping(ValidationError::getRow, Collectors.toList())
                ));
    }
    
    /**
     * Format errors for display: "Error Type: row1, row2, row3"
     */
    public String getFormattedErrors() {
        return getErrorsGroupedByType().entrySet().stream()
                .map(entry -> entry.getKey() + ": Lỗi ở dòng " +
                        entry.getValue().stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(", ")))
                .collect(Collectors.joining("\n"));
    }
    
    @Getter
    public static class ValidationError {
        private final int row;
        private final String field;
        private final String message;
        
        public ValidationError(int row, String field, String message) {
            this.row = row;
            this.field = field;
            this.message = message;
        }
    }
}
