package com.hcmut.lms.usermanagement.model.dto.response;

import com.hcmut.lms.usermanagement.model.enums.RoleType;
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
public class RoleDetailResponseDto {
    private UUID id;
    private String name;
    private String description;
    private RoleType type;
    private Boolean isActive;
    private List<PermissionDto> permissions;
}

