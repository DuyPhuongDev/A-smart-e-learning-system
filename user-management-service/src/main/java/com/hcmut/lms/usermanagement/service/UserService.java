package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.request.InternalResolveUsersRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.InternalUserSummaryResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserRoleResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest request);

    UserResponse getById(UUID id);

    UserResponse getByEmail(String email);

    List<UserResponse> getAll();

    UserResponse update(UUID id, UpdateUserRequest request);

    void delete(UUID id);

    /**
     * Delete multiple users by their IDs
     * 
     * @param ids List of user IDs to delete
     */
    void deleteMultiple(List<UUID> ids);

    /**
     * Get user's role information
     * Used by authentication-service during login
     */
    UserRoleResponse getUserRole(UUID userId);

    /**
     * Get all users with TEACHER role
     */
    List<UserResponse> getAllTeachers();

    List<InternalUserSummaryResponse> resolveUsers(InternalResolveUsersRequest request);
}
