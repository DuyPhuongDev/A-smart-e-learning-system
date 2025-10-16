package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.model.entity.UserRole;
import com.hcmut.lms.usermanagement.model.enums.UserStatus;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.UserRepository;
import com.hcmut.lms.usermanagement.service.ImportExportService;
import com.hcmut.lms.usermanagement.util.ExcelUtil;
import com.hcmut.lms.usermanagement.util.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportExportServiceImpl implements ImportExportService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
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
                
                if (data.getFullName() == null || data.getFullName().isEmpty()) {
                    errors.add(createError(data.getRowNumber(), "Full Name", "Full name is required"));
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
                
                // Create user
                User user = User.builder()
                        .email(data.getEmail())
                        .fullName(data.getFullName())
                        .phone(data.getPhone())
                        .address(data.getAddress())
                        .department(data.getDepartment())
                        .studentId(data.getStudentId())
                        .status(UserStatus.ACTIVE)
                        .build();
                
                // Assign roles if provided
                if (data.getRoles() != null && !data.getRoles().isEmpty()) {
                    Set<UserRole> userRoles = new HashSet<>();
                    String[] roleNames = data.getRoles().split(",");
                    
                    for (String roleName : roleNames) {
                        roleName = roleName.trim();
                        Role role = roleRepository.findByName(roleName).orElse(null);
                        
                        if (role == null) {
                            log.warn("Role not found: {}. Skipping for user: {}", roleName, data.getEmail());
                            continue;
                        }
                        
                        UserRole userRole = UserRole.builder()
                                .user(user)
                                .role(role)
                                .build();
                        userRoles.add(userRole);
                    }
                    
                    user.setUserRoles(userRoles);
                }
                
                // Save user
                User savedUser = userRepository.save(user);
                
                // Create credentials
                String temporaryPassword = PasswordGenerator.generateTemporaryPassword();
                authServiceClient.createUserCredentials(savedUser.getId(), savedUser.getEmail(), temporaryPassword);
                
                log.info("Imported user: {} with temporary password: {}", savedUser.getEmail(), temporaryPassword);
                successCount++;
                
            } catch (Exception e) {
                log.error("Failed to import user at row {}: {}", data.getRowNumber(), e.getMessage());
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
    public byte[] exportUsersToExcel(String search, UserStatus status, String department) {
        // Fetch users with filters
        List<User> users = userRepository.findByFilters(search, status, department, PageRequest.of(0, 10000))
                .getContent();
        
        return ExcelUtil.exportUsersToExcel(users);
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

