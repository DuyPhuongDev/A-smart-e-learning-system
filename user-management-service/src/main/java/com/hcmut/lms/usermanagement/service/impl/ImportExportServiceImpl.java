package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.exception.ImportValidationException;
import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.repository.*;
import com.hcmut.lms.usermanagement.service.ImportExportService;
import com.hcmut.lms.usermanagement.service.UserService;
import com.hcmut.lms.usermanagement.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
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
        
        // PHASE 1: Validate ALL records first
        List<ImportValidationException.ValidationError> validationErrors = new ArrayList<>();
        List<ValidatedUserData> validatedUsers = new ArrayList<>();
        
        // Track duplicates within the file itself
        Set<String> emailsInFile = new HashSet<>();
        Set<String> studentCodesInFile = new HashSet<>();
        Set<String> teacherCodesInFile = new HashSet<>();
        Set<String> adminCodesInFile = new HashSet<>();
        
        for (ExcelUtil.UserImportData data : importDataList) {
            // Validate required fields
            if (data.getEmail() == null || data.getEmail().isEmpty()) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "Email", "Thiếu email"));
                continue;
            }
            
            if (data.getFirstName() == null || data.getFirstName().isEmpty()) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "FirstName", "Thiếu họ"));
                continue;
            }
            
            if (data.getLastName() == null || data.getLastName().isEmpty()) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "LastName", "Thiếu tên"));
                continue;
            }
            
            if (data.getRoleName() == null || data.getRoleName().isEmpty()) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "RoleName", "Thiếu vai trò"));
                continue;
            }
            
            // Validate email format
            if (!EMAIL_PATTERN.matcher(data.getEmail()).matches()) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "Email", "Email không đúng định dạng"));
                continue;
            }
            
            // Check duplicate email within file
            if (emailsInFile.contains(data.getEmail().toLowerCase())) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "Email", "Email trùng lặp trong file"));
                continue;
            }
            emailsInFile.add(data.getEmail().toLowerCase());
            
            // Check if email already exists in database
            if (userRepository.existsByEmail(data.getEmail())) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "Email", "Email đã tồn tại trong hệ thống"));
                continue;
            }
            
            // Find role by name
            Optional<Role> roleOpt = roleRepository.findByName(data.getRoleName());
            if (roleOpt.isEmpty()) {
                validationErrors.add(new ImportValidationException.ValidationError(
                        data.getRowNumber(), "RoleName", "Vai trò không tồn tại: " + data.getRoleName()));
                continue;
            }
            Role role = roleOpt.get();
            
            // Validate role-specific codes
            String roleNameUpper = data.getRoleName().toUpperCase();
            
            if (roleNameUpper.equals("STUDENT")) {
                if (data.getStudentCode() == null || data.getStudentCode().isEmpty()) {
                    validationErrors.add(new ImportValidationException.ValidationError(
                            data.getRowNumber(), "StudentCode", "Thiếu mã sinh viên"));
                    continue;
                }
                // Check duplicate in file
                if (studentCodesInFile.contains(data.getStudentCode())) {
                    validationErrors.add(new ImportValidationException.ValidationError(
                            data.getRowNumber(), "StudentCode", "Mã sinh viên trùng lặp trong file"));
                    continue;
                }
                studentCodesInFile.add(data.getStudentCode());
                // Check in database
                if (studentRepository.existsByStudentCode(data.getStudentCode())) {
                    validationErrors.add(new ImportValidationException.ValidationError(
                            data.getRowNumber(), "StudentCode", "Mã sinh viên đã tồn tại"));
                    continue;
                }
            }
            
            if (roleNameUpper.equals("TEACHER")) {
                if (data.getTeacherCode() == null || data.getTeacherCode().isEmpty()) {
                    validationErrors.add(new ImportValidationException.ValidationError(
                            data.getRowNumber(), "TeacherCode", "Thiếu mã giảng viên"));
                    continue;
                }
                // Check duplicate in file
                if (teacherCodesInFile.contains(data.getTeacherCode())) {
                    validationErrors.add(new ImportValidationException.ValidationError(
                            data.getRowNumber(), "TeacherCode", "Mã giảng viên trùng lặp trong file"));
                    continue;
                }
                teacherCodesInFile.add(data.getTeacherCode());
                // Check in database
                if (teacherRepository.existsByTeacherCode(data.getTeacherCode())) {
                    validationErrors.add(new ImportValidationException.ValidationError(
                            data.getRowNumber(), "TeacherCode", "Mã giảng viên đã tồn tại"));
                    continue;
                }
            }
            
            if (roleNameUpper.equals("ADMIN")) {
                if (data.getAdminCode() != null && !data.getAdminCode().isEmpty()) {
                    // Check duplicate in file
                    if (adminCodesInFile.contains(data.getAdminCode())) {
                        validationErrors.add(new ImportValidationException.ValidationError(
                                data.getRowNumber(), "AdminCode", "Mã admin trùng lặp trong file"));
                        continue;
                    }
                    adminCodesInFile.add(data.getAdminCode());
                    // Check in database
                    if (adminRepository.existsByAdminCode(data.getAdminCode())) {
                        validationErrors.add(new ImportValidationException.ValidationError(
                                data.getRowNumber(), "AdminCode", "Mã admin đã tồn tại"));
                        continue;
                    }
                }
            }
            
            // All validations passed - add to validated list
            validatedUsers.add(new ValidatedUserData(data, role));
        }
        
        // If any validation errors, throw exception and don't import anything
        if (!validationErrors.isEmpty()) {
            log.warn("Import validation failed with {} errors", validationErrors.size());
            throw new ImportValidationException(validationErrors);
        }
        
        // PHASE 2: Import all validated users
        int successCount = 0;
        List<ImportResultDto.ImportError> errors = new ArrayList<>();
        
        for (ValidatedUserData validated : validatedUsers) {
            ExcelUtil.UserImportData data = validated.data;
            Role role = validated.role;
            
            try {
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
                String temporaryPassword = "12345678";
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
                }
                
                log.info("Imported user: {} with temporary password: {}", savedUser.getEmail(), temporaryPassword);
                successCount++;
                
            } catch (Exception e) {
                log.error("Failed to import user at row {}: {}", data.getRowNumber(), e.getMessage(), e);
                errors.add(createError(data.getRowNumber(), "General", e.getMessage()));
            }
        }
        
        return ImportResultDto.builder()
                .totalRows(importDataList.size())
                .successCount(successCount)
                .failedCount(errors.size())
                .errors(errors)
                .build();
    }
    
    /**
     * Helper class to hold validated user data with resolved role
     */
    private static class ValidatedUserData {
        final ExcelUtil.UserImportData data;
        final Role role;
        
        ValidatedUserData(ExcelUtil.UserImportData data, Role role) {
            this.data = data;
            this.role = role;
        }
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
                        return matchesEmail || matchesFirstName || matchesLastName;
                    }
                    // Note: status and department filters are not in the new schema
                    // You may need to add these fields if required
                    return true;
                })
                .toList();
        
        return ExcelUtil.exportUsersToExcel(filteredUsers);
    }
    
    private ImportResultDto.ImportError createError(int row, String field, String message) {
        return ImportResultDto.ImportError.builder()
                .row(row)
                .field(field)
                .message(message)
                .build();
    }
}
