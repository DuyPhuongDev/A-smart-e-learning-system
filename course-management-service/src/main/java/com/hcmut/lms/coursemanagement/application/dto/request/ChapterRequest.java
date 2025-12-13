package com.hcmut.lms.coursemanagement.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ChapterRequest {
    

    private String title;
    
    private String description;
    
    private Integer orderIndex;
    
//    private String status;
    
    @NotNull(message = "Class section ID is required")
    private UUID classSectionId;
}

