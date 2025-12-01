package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.exception.DuplicateResourceException;
import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.repository.AdminRepository;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.StudentRepository;
import com.hcmut.lms.usermanagement.repository.TeacherRepository;
import com.hcmut.lms.usermanagement.repository.UserRepository;
import com.hcmut.lms.usermanagement.service.ImportExportService;
import com.hcmut.lms.usermanagement.service.UserService;
import com.hcmut.lms.usermanagement.util.ExcelUtil;
import com.hcmut.lms.usermanagement.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final UserService userService;
    private final AuthServiceClient authServiceClient;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @Override
    @Transactional
    public ImportResultDto importUsersFromExcel(MultipartFile file) {
        List<ExcelUtil.UserImportData> importDataList = ExcelUtil.parseExcelFile(file);
        
        int successCount = 0;
        int failedCount = 0;
        List<ImportResultDto.ImportError> errors = new ArrayList<>();
        
        for (ExcelUtil.UserImportData data : importDataList) {
            try {
                // Validate required fields
                if (data.getEmail() == null || data.getEmail().isEmpty()) {
                    errors.add(createError(data.getRowNumber(), "Email", "Email is required"));
                    failedCount++;
                    continue;
                }
                
                if (data.getRoleName() == null || data.getRoleName().isEmpty()) {
                    errors.add(createError(data.getRowNumber(), "Role Name", "Role name is required"));
                    failedCount++;
                    continue;
                }
                
                // Validate email format
                if (!EMAIL_PATTERN.matcher(data.getEmail()).matches()) {
                    errors.add(createError(data.getRowNumber(), "Email", "Invalid email format"));
                    failedCount++;
                    continue;
                }
                
                // Check if email already exists
                if (userRepository.existsByEmail(data.getEmail())) {
                    errors.add(createError(data.getRowNumber(), "Email", "Email already exists"));
                    failedCount++;
                    continue;
                }
                
                // Find role by name
                Role role = roleRepository.findByName(data.getRoleName())
                        .orElseThrow(() -> {
                            errors.add(createError(data.getRowNumber(), "Role Name", "Role not found: " + data.getRoleName()));
                            return new ResourceNotFoundException("Role", "name", data.getRoleName());
                        });
                
                // Validate role-specific codes
                String roleNameUpper = data.getRoleName().toUpperCase();
                if (roleNameUpper.contains("STUDENT") || roleNameUpper.equals("STUDENT")) {
                    if (data.getStudentCode() == null || data.getStudentCode().isEmpty()) {
                        errors.add(createError(data.getRowNumber(), "Student Code", "Student code is required for student role"));
                        failedCount++;
                        continue;
                    }
                    if (studentRepository.existsByStudentCode(data.getStudentCode())) {
                        errors.add(createError(data.getRowNumber(), "Student Code", "Student code already exists"));
                        failedCount++;
                        continue;
                    }
                }
                
                if (roleNameUpper.contains("TEACHER") || roleNameUpper.equals("TEACHER")) {
                    if (data.getTeacherCode() == null || data.getTeacherCode().isEmpty()) {
                        errors.add(createError(data.getRowNumber(), "Teacher Code", "Teacher code is required for teacher role"));
                        failedCount++;
                        continue;
                    }
                    if (teacherRepository.existsByTeacherCode(data.getTeacherCode())) {
                        errors.add(createError(data.getRowNumber(), "Teacher Code", "Teacher code already exists"));
                        failedCount++;
                        continue;
                    }
                }
                
                if (roleNameUpper.contains("ADMIN") || roleNameUpper.equals("ADMIN")) {
                    if (data.getAdminCode() != null && !data.getAdminCode().isEmpty()) {
                        if (adminRepository.existsByAdminCode(data.getAdminCode())) {
                            errors.add(createError(data.getRowNumber(), "Admin Code", "Admin code already exists"));
                            failedCount++;
                            continue;
                        }
                    }
                }
                
                // Create user request
                CreateUserRequest createUserRequest = new CreateUserRequest();
                createUserRequest.setEmail(data.getEmail());
                createUserRequest.setFirstName(data.getFirstName());
                createUserRequest.setLastName(data.getLastName());
                createUserRequest.setPhone(data.getPhone());
                createUserRequest.setSpecializationId(data.getSpecializationId());
                createUserRequest.setRoleId(role.getId());
                createUserRequest.setStudentCode(data.getStudentCode());
                createUserRequest.setTeacherCode(data.getTeacherCode());
                createUserRequest.setBio(data.getTeacherBio());
                createUserRequest.setAdminCode(data.getAdminCode());
                
                // Generate temporary password
                String temporaryPassword = PasswordGenerator.generateTemporaryPassword();
                createUserRequest.setPassword(temporaryPassword);
                
                // Create user using service
                userService.create(createUserRequest);
                
                // Get created user to create credentials
                User savedUser = userRepository.findByEmail(data.getEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "email", data.getEmail()));
                
                // Create credentials in auth service
                try {
                    AuthServiceClient.CreateCredentialsRequest credentialsRequest = 
                        new AuthServiceClient.CreateCredentialsRequest(
                            savedUser.getId(), 
                            savedUser.getEmail(), 
                            temporaryPassword
                        );
                    authServiceClient.createUserCredentials(credentialsRequest);
                    log.info("Created credentials for user: {}", savedUser.getEmail());
                } catch (Exception e) {
                    log.warn("Failed to create credentials for user {}: {}", savedUser.getEmail(), e.getMessage());
                    // Continue even if credential creation fails
                }
                
                log.info("Imported user: {} with temporary password: {}", savedUser.getEmail(), temporaryPassword);
                successCount++;
                
            } catch (ResourceNotFoundException e) {
                log.error("Failed to import user at row {}: {}", data.getRowNumber(), e.getMessage());
                failedCount++;
            } catch (DuplicateResourceException e) {
                log.error("Failed to import user at row {}: {}", data.getRowNumber(), e.getMessage());
                errors.add(createError(data.getRowNumber(), "Duplicate", e.getMessage()));
                failedCount++;
            } catch (Exception e) {
                log.error("Failed to import user at row {}: {}", data.getRowNumber(), e.getMessage(), e);
                errors.add(createError(data.getRowNumber(), "General", e.getMessage()));
                failedCount++;
            }
        }
        
        return ImportResultDto.builder()
                .totalRows(importDataList.size())
                .successCount(successCount)
                .failedCount(failedCount)
                .errors(errors)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public byte[] exportUsersToExcel(String search, com.hcmut.lms.usermanagement.model.enums.UserStatus status, String department) {
        // Fetch all users (you can add filtering logic here if needed)
        List<User> users = userRepository.findAll();
        
        // Apply filters if provided
        List<User> filteredUsers = users.stream()
                .filter(user -> {
                    if (search != null && !search.isEmpty()) {
                        String searchLower = search.toLowerCase();
                        boolean matchesEmail = user.getEmail() != null && user.getEmail().toLowerCase().contains(searchLower);
                        boolean matchesFirstName = user.getFirstName() != null && user.getFirstName().toLowerCase().contains(searchLower);
                        boolean matchesLastName = user.getLastName() != null && user.getLastName().toLowerCase().contains(searchLower);
                        if (!matchesEmail && !matchesFirstName && !matchesLastName) {
                            return false;
                        }
                    }
                    // Note: status and department filters are not in the new schema
                    // You may need to add these fields if required
                    return true;
                })
                .toList();
        
        return ExcelUtil.exportUsersToExcel(filteredUsers);
    }
    
    @Override
    public byte[] generateImportTemplate() {
        return ExcelUtil.generateTemplate();
    }
    
    private ImportResultDto.ImportError createError(int row, String field, String message) {
        return ImportResultDto.ImportError.builder()
                .row(row)
                .field(field)
                .message(message)
                .build();
    }
}
