package com.hcmut.lms.usermanagement.model.dto.response;

import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private UUID id;
    private String email;
    private String fullName;
    private String avatar;
    private UserStatus status;
    private List<RoleResponseDto> roles;
}

