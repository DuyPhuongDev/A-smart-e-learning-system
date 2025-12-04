package com.hcmut.lms.common.helper;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CurrentUserInfo {
    private UUID id;
    private String role;

    public boolean hasRole(String role) {
        return role.equals(this.role);
    }
}
