package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDto {
    
    @Size(max = 255, message = "Full name must not exceed 255 characters")
    private String fullName;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
    
    @Size(max = 500, message = "Avatar URL must not exceed 500 characters")
    private String avatar;
}

