package com.hcmut.lms.usermanagement.model.dto.response;

import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDto {
    private UUID id;
    private String email;
    private String fullName;
    private String avatar;
    private String phone;
    private String address;
    private String department;
    private String studentId;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RoleResponseDto> roles;
}

