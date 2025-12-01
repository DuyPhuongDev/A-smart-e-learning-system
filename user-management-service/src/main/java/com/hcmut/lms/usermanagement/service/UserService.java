package com.hcmut.lms.usermanagement.service;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse create(CreateUserRequest request);
    UserResponse getById(UUID id);
    UserResponse getByEmail(String email);
    List<UserResponse> getAll();
    UserResponse update(UUID id, UpdateUserRequest request);
    void delete(UUID id);
}
