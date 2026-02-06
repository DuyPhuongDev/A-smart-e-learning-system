package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userService.create(request);
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        return userService.getById(id);
    }

    @GetMapping("/email/{email}")
    public UserResponse getByEmail(@PathVariable String email) {
        return userService.getByEmail(email);
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @PutMapping("/{id}")
    public UserResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        return userService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    @DeleteMapping("/batch")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMultiple(@RequestBody List<UUID> ids) {
        userService.deleteMultiple(ids);
    }

    @GetMapping("/teachers")
    public List<UserResponse> getAllTeachers() {
        return userService.getAllTeachers();
    }
}
