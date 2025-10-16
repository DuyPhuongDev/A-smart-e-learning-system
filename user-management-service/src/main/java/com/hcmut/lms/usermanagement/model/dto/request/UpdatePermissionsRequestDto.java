package com.hcmut.lms.usermanagement.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class UpdatePermissionsRequestDto {
    
    @NotEmpty(message = "Permission updates list cannot be empty")
    @Valid
    private List<PermissionUpdateItem> permissions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PermissionUpdateItem {
        @NotNull(message = "Endpoint ID is required")
        private UUID endpointId;
        
        @NotNull(message = "isAllowed flag is required")
        private Boolean isAllowed;
    }
}

