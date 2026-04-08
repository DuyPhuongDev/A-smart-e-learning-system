package com.hcmut.lms.notification.client;

import com.hcmut.lms.notification.client.dto.InternalResolveUsersRequest;
import com.hcmut.lms.notification.client.dto.InternalUserSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-management-service")
public interface UserManagementInternalClient {

    @PostMapping("/api/users/internal/resolve")
    List<InternalUserSummaryResponse> resolveUsers(@RequestBody InternalResolveUsersRequest request);
}
