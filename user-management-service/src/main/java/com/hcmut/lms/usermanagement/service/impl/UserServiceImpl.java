package com.hcmut.lms.usermanagement.service.impl;

import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.exception.DuplicateResourceException;
import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.UserMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.request.InternalResolveUsersRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.InternalUserSummaryResponse;
import com.hcmut.lms.usermanagement.model.dto.response.StudentResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserRoleResponse;
import com.hcmut.lms.usermanagement.model.entity.*;
import com.hcmut.lms.usermanagement.repository.*;
import com.hcmut.lms.usermanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final AdminRepository adminRepository;
    private final UserMapper userMapper;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public UserResponse create(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));

        User user = userMapper.toEntity(request);
        user.setRole(role);
        user = userRepository.save(user);

        // Create user type specific records
        if (request.getStudentCode() != null) {
            if (studentRepository.existsByStudentCode(request.getStudentCode())) {
                throw new DuplicateResourceException("Student", "studentCode", request.getStudentCode());
            }
            Student student = Student.builder()
                    .user(user)
                    .studentCode(request.getStudentCode())
                    .build();
            studentRepository.save(student);
        }

        if (request.getTeacherCode() != null) {
            if (teacherRepository.existsByTeacherCode(request.getTeacherCode())) {
                throw new DuplicateResourceException("Teacher", "teacherCode", request.getTeacherCode());
            }
            Teacher teacher = Teacher.builder()
                    .user(user)
                    .teacherCode(request.getTeacherCode())
                    .bio(request.getBio())
                    .build();
            teacherRepository.save(teacher);
        }

        if (request.getAdminCode() != null) {
            if (adminRepository.existsByAdminCode(request.getAdminCode())) {
                throw new DuplicateResourceException("Admin", "adminCode", request.getAdminCode());
            }
            Admin admin = Admin.builder()
                    .user(user)
                    .adminCode(request.getAdminCode())
                    .build();
            adminRepository.save(admin);
        }

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse update(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        String oldEmail = user.getEmail();
        boolean emailChanged = false;

        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("User", "email", request.getEmail());
            }
            emailChanged = true;
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "id", request.getRoleId()));
            user.setRole(role);
        }

        userMapper.updateEntity(request, user);
        user = userRepository.save(user);

        // Sync email to authentication service if email changed
        if (emailChanged) {
            try {
                AuthServiceClient.UpdateEmailRequest updateEmailRequest = new AuthServiceClient.UpdateEmailRequest(
                        user.getId(), oldEmail, request.getEmail());
                authServiceClient.updateUserEmail(updateEmailRequest);
                log.info("Email synced to authentication service for user: {}", user.getId());
            } catch (Exception e) {
                log.warn("Failed to sync email to authentication service for user {}: {}",
                        user.getId(), e.getMessage());
                // Continue even if sync fails - can be retried later
            }
        }

        // Update user type specific records
        if (request.getStudentCode() != null) {
            Student student = user.getStudent();
            if (student == null) {
                student = Student.builder()
                        .user(user)
                        .studentCode(request.getStudentCode())
                        .build();
                studentRepository.save(student);
            } else {
                student.setStudentCode(request.getStudentCode());
                studentRepository.save(student);
            }
        }

        if (request.getTeacherCode() != null) {
            Teacher teacher = user.getTeacher();
            if (teacher == null) {
                teacher = Teacher.builder()
                        .user(user)
                        .teacherCode(request.getTeacherCode())
                        .bio(request.getBio())
                        .build();
                teacherRepository.save(teacher);
            } else {
                teacher.setTeacherCode(request.getTeacherCode());
                if (request.getBio() != null) {
                    teacher.setBio(request.getBio());
                }
                teacherRepository.save(teacher);
            }
        }

        if (request.getAdminCode() != null) {
            Admin admin = user.getAdmin();
            if (admin == null) {
                admin = Admin.builder()
                        .user(user)
                        .adminCode(request.getAdminCode())
                        .build();
                adminRepository.save(admin);
            } else {
                admin.setAdminCode(request.getAdminCode());
                adminRepository.save(admin);
            }
        }

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void deleteMultiple(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        userRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleResponse getUserRole(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        return UserRoleResponse.builder()
                .userId(user.getId())
                .roleId(user.getRole().getId())
                .roleName(user.getRole().getName())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllTeachers() {
        return userRepository.findAllTeachersWithRole("TEACHER").stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternalUserSummaryResponse> resolveUsers(InternalResolveUsersRequest request) {
        Specification<User> specification = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            var roleJoin = root.join("role");

            if (!CollectionUtils.isEmpty(request.getRoleNames())) {
                List<String> normalizedRoleNames = request.getRoleNames().stream()
                        .filter(StringUtils::hasText)
                        .map(String::trim)
                        .map(String::toUpperCase)
                        .toList();
                if (!normalizedRoleNames.isEmpty()) {
                    predicates.add(cb.upper(roleJoin.get("name")).in(normalizedRoleNames));
                }
            }

            if (!CollectionUtils.isEmpty(request.getSpecializationIds())) {
                predicates.add(root.get("specializationId").in(request.getSpecializationIds()));
            }

            if (!CollectionUtils.isEmpty(request.getUserIds())) {
                predicates.add(root.get("id").in(request.getUserIds()));
            }

            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };

        return userRepository.findAll(specification).stream()
                .map(user -> InternalUserSummaryResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .roleName(user.getRole() != null ? user.getRole().getName() : null)
                        .specializationId(user.getSpecializationId())
                        .build())
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getStudentByStudentCode(String studentCode) {
        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "studentCode", studentCode));
        return userMapper.toResponse(student.getUser());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getTeachersBySubjectGroupId(UUID subjectGroupId) {
        return teacherRepository.findAll().stream()
                .filter(teacher -> subjectGroupId.equals(teacher.getSubjectGroupId()))
                .map(teacher -> userMapper.toResponse(teacher.getUser()))
                .collect(Collectors.toList());
    }
}
