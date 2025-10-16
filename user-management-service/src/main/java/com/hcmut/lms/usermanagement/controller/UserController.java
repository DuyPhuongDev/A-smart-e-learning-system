package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping
    public ResponseDto<String> getAllUsers() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get all users - To be implemented")
                .build();
    }

    @GetMapping("/{id}")
    public ResponseDto<String> getUserById(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get user by ID - To be implemented")
                .build();
    }

    @PostMapping
    public ResponseDto<String> createUser() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create user - To be implemented")
                .build();
    }

    @PutMapping("/{id}")
    public ResponseDto<String> updateUser(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Update user - To be implemented")
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseDto<String> deleteUser(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Delete user - To be implemented")
                .build();
    }

    @PostMapping("/{id}/roles")
    public ResponseDto<String> assignRole(@PathVariable String id) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Assign role - To be implemented")
                .build();
    }
}

