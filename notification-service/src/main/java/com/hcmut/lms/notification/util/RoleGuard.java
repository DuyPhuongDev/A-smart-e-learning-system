package com.hcmut.lms.notification.util;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.notification.exception.ForbiddenException;

public final class RoleGuard {

    private RoleGuard() {
    }

    public static void requireAdmin(CurrentUserInfo userInfo) {
        if (userInfo == null || userInfo.getRole() == null || !"ADMIN".equalsIgnoreCase(userInfo.getRole())) {
            throw new ForbiddenException("Admin role is required");
        }
    }
}
