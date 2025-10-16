package com.hcmut.lms.usermanagement.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDto {
    private Integer totalRows;
    private Integer successCount;
    private Integer failedCount;
    private List<ImportError> errors;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private Integer row;
        private String field;
        private String message;
    }
}

