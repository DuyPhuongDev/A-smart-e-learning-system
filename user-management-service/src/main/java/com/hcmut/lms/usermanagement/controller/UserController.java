package com.hcmut.lms.usermanagement.controller;

import com.hcmut.lms.usermanagement.model.dto.request.AssignRoleRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequestDto;
import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.dto.response.RoleResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserDetailResponseDto;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponseDto;
import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import com.hcmut.lms.usermanagement.service.ImportExportService;
import com.hcmut.lms.usermanagement.service.RoleService;
import com.hcmut.lms.usermanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final RoleService roleService;
    private final ImportExportService importExportService;

    @GetMapping
    public Page<UserResponseDto> getAllUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) UUID roleId,
            Pageable pageable) {
        return userService.getAllUsers(search, status, department, roleId, pageable);
    }

    @GetMapping("/{id}")
    public UserDetailResponseDto getUserById(@PathVariable UUID id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@Valid @RequestBody CreateUserRequestDto dto) {
        UserResponseDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PutMapping("/{id}")
    public UserResponseDto updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequestDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
    
    @PatchMapping("/{id}/lock")
    public void lockUser(@PathVariable UUID id) {
        userService.lockUser(id);
    }
    
    @PatchMapping("/{id}/unlock")
    public void unlockUser(@PathVariable UUID id) {
        userService.unlockUser(id);
    }
    
    @PostMapping("/{id}/reset-password")
    public void resetPassword(@PathVariable UUID id) {
        userService.resetPassword(id);
    }

    @PostMapping("/{userId}/roles")
    public void assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequestDto dto) {
        roleService.assignRoleToUser(userId, dto.getRoleId());
    }
    
    @DeleteMapping("/{userId}/roles/{roleId}")
    public void removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId) {
        roleService.removeRoleFromUser(userId, roleId);
    }
    
    @GetMapping("/{userId}/roles")
    public List<RoleResponseDto> getUserRoles(@PathVariable UUID userId) {
        return roleService.getUserRoles(userId);
    }
    
    @PostMapping("/import")
    public ImportResultDto importUsers(@RequestParam("file") MultipartFile file) {
        return importExportService.importUsersFromExcel(file);
    }
    
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportUsers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String department) {
        byte[] excelData = importExportService.exportUsersToExcel(search, status, department);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelData);
    }
    
    @GetMapping("/import/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        byte[] template = importExportService.generateImportTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "user_import_template.xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(template);
    }
}

