package com.hcmut.lms.authentication.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {
    @NotNull(message = "User ID is required")
    private UUID userId;
    
    @NotBlank(message = "Old email is required")
    @Email(message = "Old email must be valid")
    private String oldEmail;
    
    @NotBlank(message = "New email is required")
    @Email(message = "New email must be valid")
    private String newEmail;
}

