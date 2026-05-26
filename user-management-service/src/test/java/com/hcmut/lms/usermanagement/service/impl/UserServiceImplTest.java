package com.hcmut.lms.usermanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.client.CourseServiceClient;
import com.hcmut.lms.usermanagement.client.dto.DepartmentResponse;
import com.hcmut.lms.usermanagement.client.dto.SemesterResponse;
import com.hcmut.lms.usermanagement.exception.DuplicateResourceException;
import com.hcmut.lms.usermanagement.exception.ResourceNotFoundException;
import com.hcmut.lms.usermanagement.mapper.UserMapper;
import com.hcmut.lms.usermanagement.model.dto.request.CreateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.request.InternalResolveUsersRequest;
import com.hcmut.lms.usermanagement.model.dto.request.UpdateUserRequest;
import com.hcmut.lms.usermanagement.model.dto.response.InternalUserSummaryResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserResponse;
import com.hcmut.lms.usermanagement.model.dto.response.UserRoleResponse;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.Student;
import com.hcmut.lms.usermanagement.model.entity.Teacher;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.repository.AdminRepository;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.StudentRepository;
import com.hcmut.lms.usermanagement.repository.TeacherRepository;
import com.hcmut.lms.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthServiceClient authServiceClient;

    @Mock
    private CourseServiceClient courseServiceClient;

    @InjectMocks
    private UserServiceImpl userService;

    // --- create ---

    @Test
    void create_shouldReturnResponse_whenValidRequest() {
        CreateUserRequest request = createUserRequest("test@hcmut.edu.vn", "pass", "First", "Last");
        Role role = createRole("STUDENT");
        User entity = createUser("test@hcmut.edu.vn", "First", "Last");
        User saved = createUser("test@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(saved.getId(), "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.existsByEmail("test@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals(response, result);
        verify(authServiceClient).createUserCredentials(any());
    }

    @Test
    void create_shouldThrowDuplicateException_whenEmailExists() {
        CreateUserRequest request = createUserRequest("existing@hcmut.edu.vn", "pass", "First", "Last");
        when(userRepository.existsByEmail("existing@hcmut.edu.vn")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(request));
    }

    @Test
    void create_shouldThrowException_whenRoleNotFound() {
        CreateUserRequest request = createUserRequest("test@hcmut.edu.vn", "pass", "First", "Last");

        when(userRepository.existsByEmail("test@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.create(request));
    }

    @Test
    void create_shouldCreateStudent_whenStudentCodeProvided() {
        CreateUserRequest request = createUserRequest("student@hcmut.edu.vn", "pass", "First", "Last");
        request.setStudentCode("S001");

        Role role = createRole("STUDENT");
        User entity = createUser("student@hcmut.edu.vn", "First", "Last");
        User saved = createUser("student@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(saved.getId(), "student@hcmut.edu.vn", "STUDENT");

        DepartmentResponse department = DepartmentResponse.builder()
                .id(UUID.randomUUID()).name("CS").build();
        SemesterResponse semester = SemesterResponse.builder()
                .id(UUID.randomUUID()).academicYearId(UUID.randomUUID()).build();

        when(userRepository.existsByEmail("student@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(studentRepository.existsByStudentCode("S001")).thenReturn(false);
        when(courseServiceClient.getDepartmentBySpecializationId(any())).thenReturn(department);
        when(courseServiceClient.getCurrentSemester()).thenReturn(semester);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals(response, result);
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void create_shouldCreateTeacher_whenTeacherCodeProvided() {
        CreateUserRequest request = createUserRequest("teacher@hcmut.edu.vn", "pass", "First", "Last");
        request.setTeacherCode("T001");
        request.setBio("Bio text");

        Role role = createRole("TEACHER");
        User entity = createUser("teacher@hcmut.edu.vn", "First", "Last");
        User saved = createUser("teacher@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(saved.getId(), "teacher@hcmut.edu.vn", "TEACHER");

        when(userRepository.existsByEmail("teacher@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(teacherRepository.existsByTeacherCode("T001")).thenReturn(false);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals(response, result);
        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void create_shouldThrowException_whenStudentCodeExists() {
        CreateUserRequest request = createUserRequest("student@hcmut.edu.vn", "pass", "First", "Last");
        request.setStudentCode("S001");

        Role role = createRole("STUDENT");
        User entity = createUser("student@hcmut.edu.vn", "First", "Last");
        User saved = createUser("student@hcmut.edu.vn", "First", "Last");

        when(userRepository.existsByEmail("student@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(studentRepository.existsByStudentCode("S001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(request));
    }

    // --- getById ---

    @Test
    void getById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        User user = createUser("test@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getById(id);

        assertEquals(response, result);
    }

    @Test
    void getById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(id));
    }

    // --- getByEmail ---

    @Test
    void getByEmail_shouldReturnResponse_whenExists() {
        User user = createUser("test@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(user.getId(), "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.findByEmail("test@hcmut.edu.vn")).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getByEmail("test@hcmut.edu.vn");

        assertEquals(response, result);
    }

    @Test
    void getByEmail_shouldThrowException_whenNotFound() {
        when(userRepository.findByEmail("unknown@hcmut.edu.vn")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getByEmail("unknown@hcmut.edu.vn"));
    }

    // --- getAll ---

    @Test
    void getAll_shouldReturnList() {
        User user = createUser("test@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(user.getId(), "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        List<UserResponse> result = userService.getAll();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    @Test
    void getAll_shouldReturnEmptyList_whenNone() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserResponse> result = userService.getAll();

        assertEquals(0, result.size());
    }

    // --- getAllTeachers ---

    @Test
    void getAllTeachers_shouldReturnList() {
        User teacher = createUser("teacher@hcmut.edu.vn", "Teach", "Er");
        UserResponse response = createUserResponse(teacher.getId(), "teacher@hcmut.edu.vn", "TEACHER");

        when(userRepository.findAllTeachersWithRole("TEACHER")).thenReturn(List.of(teacher));
        when(userMapper.toResponse(teacher)).thenReturn(response);

        List<UserResponse> result = userService.getAllTeachers();

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    // --- update ---

    @Test
    void update_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest("new@hcmut.edu.vn", "Updated", "Name");
        User existing = createUser("old@hcmut.edu.vn", "Old", "Name");
        User saved = createUser("new@hcmut.edu.vn", "Updated", "Name");
        UserResponse response = createUserResponse(id, "new@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@hcmut.edu.vn")).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(saved);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.update(id, request);

        assertEquals(response, result);
        verify(userMapper).updateEntity(request, existing);
        verify(authServiceClient).updateUserEmail(any());
    }

    @Test
    void update_shouldNotSyncEmail_whenEmailUnchanged() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest("same@hcmut.edu.vn", "Updated", "Name");
        User existing = createUser("same@hcmut.edu.vn", "Old", "Name");
        UserResponse response = createUserResponse(id, "same@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(authServiceClient, never()).updateUserEmail(any());
    }

    @Test
    void update_shouldSwallowAuthServiceFailure_whenEmailChanged() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest("new@hcmut.edu.vn", "Updated", "Name");
        User existing = createUser("old@hcmut.edu.vn", "Old", "Name");
        UserResponse response = createUserResponse(id, "new@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@hcmut.edu.vn")).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);
        doThrow(new RuntimeException("Auth service down")).when(authServiceClient).updateUserEmail(any());

        UserResponse result = userService.update(id, request);

        assertEquals(response, result);
    }

    @Test
    void update_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.update(id, updateUserRequest("e@hcmut.edu.vn", "F", "L")));
    }

    @Test
    void update_shouldSyncEmailToAuthService_whenEmailChanged() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest("new@hcmut.edu.vn", "F", "L");
        User existing = createUser("old@hcmut.edu.vn", "F", "L");
        UserResponse response = createUserResponse(id, "new@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("new@hcmut.edu.vn")).thenReturn(false);
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(authServiceClient).updateUserEmail(any());
    }

    // --- delete ---

    @Test
    void delete_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        User user = createUser("test@hcmut.edu.vn", "First", "Last");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.delete(id);

        verify(userRepository).delete(user);
    }

    @Test
    void delete_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.delete(id));
    }

    // --- deleteMultiple ---

    @Test
    void deleteMultiple_shouldDeleteInBatch_whenIdsProvided() {
        List<UUID> ids = List.of(UUID.randomUUID(), UUID.randomUUID());

        userService.deleteMultiple(ids);

        verify(userRepository).deleteAllByIdInBatch(ids);
    }

    @Test
    void deleteMultiple_shouldDoNothing_whenEmptyList() {
        userService.deleteMultiple(Collections.emptyList());

        verify(userRepository, never()).deleteAllByIdInBatch(anyList());
    }

    @Test
    void deleteMultiple_shouldDoNothing_whenNull() {
        userService.deleteMultiple(null);

        verify(userRepository, never()).deleteAllByIdInBatch(anyList());
    }

    // --- getUserRole ---

    @Test
    void getUserRole_shouldReturnResponse_whenUserExists() {
        Role role = createRole("ADMIN");
        User user = createUser("test@hcmut.edu.vn", "First", "Last");
        user.setRole(role);
        UUID userId = user.getId();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserRoleResponse result = userService.getUserRole(userId);

        assertEquals(userId, result.getUserId());
        assertEquals(role.getId(), result.getRoleId());
        assertEquals("ADMIN", result.getRoleName());
    }

    @Test
    void getUserRole_shouldThrowException_whenUserNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserRole(userId));
    }

    // --- getStudentByStudentCode ---

    @Test
    void getStudentByStudentCode_shouldReturnResponse_whenExists() {
        User user = createUser("student@hcmut.edu.vn", "Stu", "Dent");
        UserResponse response = createUserResponse(user.getId(), "student@hcmut.edu.vn", "STUDENT");
        Student student = Student.builder().user(user).studentCode("S001").build();

        when(studentRepository.findByStudentCode("S001")).thenReturn(Optional.of(student));
        when(userMapper.toResponse(user)).thenReturn(response);

        UserResponse result = userService.getStudentByStudentCode("S001");

        assertEquals(response, result);
    }

    @Test
    void getStudentByStudentCode_shouldThrowException_whenNotFound() {
        when(studentRepository.findByStudentCode("S999")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getStudentByStudentCode("S999"));
    }

    // --- resolveUsers ---

    @Test
    void resolveUsers_shouldReturnFilteredList_whenValidFilters() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        request.setRoleNames(List.of("STUDENT"));
        request.setSpecializationIds(List.of(UUID.randomUUID()));
        request.setUserIds(List.of(UUID.randomUUID()));

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.emptyList());

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void resolveUsers_shouldReturnEmptyList_whenNoMatch() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.emptyList());

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(0, result.size());
    }

    // --- getTeachersBySubjectGroupId ---

    @Test
    void getTeachersBySubjectGroupId_shouldReturnList() {
        UUID subjectGroupId = UUID.randomUUID();
        User teacher = createUser("teacher@hcmut.edu.vn", "Teach", "Er");
        Teacher teacherEntity = Teacher.builder()
                .user(teacher).teacherCode("T001").subjectGroupId(subjectGroupId).build();
        UserResponse response = createUserResponse(teacher.getId(), "teacher@hcmut.edu.vn", "TEACHER");

        when(teacherRepository.findAll()).thenReturn(List.of(teacherEntity));
        when(userMapper.toResponse(teacher)).thenReturn(response);

        List<UserResponse> result = userService.getTeachersBySubjectGroupId(subjectGroupId);

        assertEquals(1, result.size());
        assertEquals(response, result.get(0));
    }

    // --- create admin/teacher duplicate edge cases ---

    @Test
    void create_shouldThrowException_whenTeacherCodeExists() {
        CreateUserRequest request = createUserRequest("teacher@hcmut.edu.vn", "pass", "First", "Last");
        request.setTeacherCode("T001");

        Role role = createRole("TEACHER");
        User entity = createUser("teacher@hcmut.edu.vn", "First", "Last");
        User saved = createUser("teacher@hcmut.edu.vn", "First", "Last");

        when(userRepository.existsByEmail("teacher@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(teacherRepository.existsByTeacherCode("T001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(request));
    }

    @Test
    void create_shouldCreateAdmin_whenAdminCodeProvided() {
        CreateUserRequest request = createUserRequest("admin@hcmut.edu.vn", "pass", "First", "Last");
        request.setAdminCode("A001");

        Role role = createRole("ADMIN");
        User entity = createUser("admin@hcmut.edu.vn", "First", "Last");
        User saved = createUser("admin@hcmut.edu.vn", "First", "Last");
        UserResponse response = createUserResponse(saved.getId(), "admin@hcmut.edu.vn", "ADMIN");

        when(userRepository.existsByEmail("admin@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(adminRepository.existsByAdminCode("A001")).thenReturn(false);
        when(userMapper.toResponse(saved)).thenReturn(response);

        UserResponse result = userService.create(request);

        assertEquals(response, result);
        verify(adminRepository).save(any());
    }

    @Test
    void create_shouldThrowException_whenAdminCodeExists() {
        CreateUserRequest request = createUserRequest("admin@hcmut.edu.vn", "pass", "First", "Last");
        request.setAdminCode("A001");

        Role role = createRole("ADMIN");
        User entity = createUser("admin@hcmut.edu.vn", "First", "Last");
        User saved = createUser("admin@hcmut.edu.vn", "First", "Last");

        when(userRepository.existsByEmail("admin@hcmut.edu.vn")).thenReturn(false);
        when(roleRepository.findById(request.getRoleId())).thenReturn(Optional.of(role));
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(userRepository.save(entity)).thenReturn(saved);
        when(adminRepository.existsByAdminCode("A001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.create(request));
    }

    // --- update with role and type changes ---

    @Test
    void update_shouldThrowException_whenDuplicateEmailInUpdate() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest("dup@hcmut.edu.vn", "F", "L");
        User existing = createUser("old@hcmut.edu.vn", "F", "L");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmail("dup@hcmut.edu.vn")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> userService.update(id, request));
    }

    @Test
    void update_shouldChangeRole_whenRoleIdProvided() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        UUID newRoleId = UUID.randomUUID();
        request.setRoleId(newRoleId);

        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        Role newRole = createRole("ADMIN");
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "ADMIN");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.findById(newRoleId)).thenReturn(Optional.of(newRole));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(roleRepository).findById(newRoleId);
    }

    @Test
    void update_shouldCreateStudent_whenStudentCodeProvidedAndNoExistingStudent() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setStudentCode("S002");
        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void update_shouldUpdateExistingStudent_whenStudentCodeProvided() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setStudentCode("S003");

        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        Student existingStudent = Student.builder().user(existing).studentCode("S002").build();
        existing.setStudent(existingStudent);
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(studentRepository).save(existingStudent);
        assertEquals("S003", existingStudent.getStudentCode());
    }

    @Test
    void update_shouldCreateTeacher_whenTeacherCodeProvidedAndNoExisting() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setTeacherCode("T002");
        request.setBio("New bio");
        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "TEACHER");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(teacherRepository).save(any(Teacher.class));
    }

    @Test
    void update_shouldUpdateExistingTeacher_whenTeacherCodeProvided() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setTeacherCode("T003");

        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        Teacher existingTeacher = Teacher.builder().user(existing).teacherCode("T002").bio("Old bio").build();
        existing.setTeacher(existingTeacher);
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "TEACHER");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(teacherRepository).save(existingTeacher);
        assertEquals("T003", existingTeacher.getTeacherCode());
    }

    @Test
    void update_shouldUpdateTeacherBio_whenBioProvided() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setTeacherCode("T003");
        request.setBio("Updated bio");

        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        Teacher existingTeacher = Teacher.builder().user(existing).teacherCode("T002").bio("Old bio").build();
        existing.setTeacher(existingTeacher);
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "TEACHER");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(teacherRepository).save(existingTeacher);
        assertEquals("Updated bio", existingTeacher.getBio());
    }

    @Test
    void update_shouldCreateAdmin_whenAdminCodeProvidedAndNoExisting() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setAdminCode("A002");
        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "ADMIN");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(adminRepository).save(any());
    }

    @Test
    void update_shouldUpdateExistingAdmin_whenAdminCodeProvided() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        request.setAdminCode("A003");

        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        com.hcmut.lms.usermanagement.model.entity.Admin existingAdmin =
                com.hcmut.lms.usermanagement.model.entity.Admin.builder()
                        .user(existing).adminCode("A001").build();
        existing.setAdmin(existingAdmin);
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "ADMIN");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(adminRepository).save(existingAdmin);
        assertEquals("A003", existingAdmin.getAdminCode());
    }

    // --- resolveUsers with specification execution ---

    @Test
    @SuppressWarnings("unchecked")
    void resolveUsers_shouldExecuteSpecification_whenRoleNamesProvided() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        request.setRoleNames(List.of("  student  ", ""));

        User user = createUser("test@hcmut.edu.vn", "F", "L");
        Role role = createRole("STUDENT");
        user.setRole(role);

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(user));

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
        assertEquals(user.getEmail(), result.get(0).getEmail());
        assertEquals("STUDENT", result.get(0).getRoleName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void resolveUsers_shouldReturnResultsWithNullRole() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        request.setSpecializationIds(List.of(UUID.randomUUID()));

        User user = createUser("test@hcmut.edu.vn", "F", "L");
        user.setRole(null);

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(List.of(user));

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(1, result.size());
        assertEquals(null, result.get(0).getRoleName());
    }

    @Test
    @SuppressWarnings("unchecked")
    void resolveUsers_shouldReturnFiltered_whenAllFiltersPresent() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        request.setRoleNames(List.of("TEACHER"));
        request.setSpecializationIds(List.of(UUID.randomUUID()));
        request.setUserIds(List.of(UUID.randomUUID()));

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenReturn(Collections.emptyList());

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(0, result.size());
    }

    // --- resolveUsers with actual Specification execution ---

    @Test
    @SuppressWarnings("unchecked")
    void resolveUsers_shouldExecuteSpecification_whenAllFiltersProvided() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        request.setRoleNames(List.of("  student  ", "")); // blank filtered, "student" trimmed & upper
        request.setSpecializationIds(List.of(UUID.randomUUID()));
        request.setUserIds(List.of(UUID.randomUUID()));

        User user = createUser("test@hcmut.edu.vn", "F", "L");
        Role role = createRole("STUDENT");
        user.setRole(role);

        var root = mock(jakarta.persistence.criteria.Root.class, RETURNS_DEEP_STUBS);
        var query = mock(jakarta.persistence.criteria.CriteriaQuery.class);
        var cb = mock(jakarta.persistence.criteria.CriteriaBuilder.class, RETURNS_DEEP_STUBS);

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenAnswer(invocation -> {
                    org.springframework.data.jpa.domain.Specification<User> spec =
                            invocation.getArgument(0);
                    spec.toPredicate(root, query, cb);
                    return List.of(user);
                });

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.get(0).getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void resolveUsers_shouldSkipRolePredicate_whenAllRoleNamesBlank() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        request.setRoleNames(List.of("", "  ")); // all blank → normalized list empty

        User user = createUser("test@hcmut.edu.vn", "F", "L");
        Role role = createRole("STUDENT");
        user.setRole(role);

        var root = mock(jakarta.persistence.criteria.Root.class, RETURNS_DEEP_STUBS);
        var query = mock(jakarta.persistence.criteria.CriteriaQuery.class);
        var cb = mock(jakarta.persistence.criteria.CriteriaBuilder.class, RETURNS_DEEP_STUBS);

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenAnswer(invocation -> {
                    org.springframework.data.jpa.domain.Specification<User> spec =
                            invocation.getArgument(0);
                    spec.toPredicate(root, query, cb);
                    return List.of(user);
                });

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void resolveUsers_shouldBuildEmptyPredicates_whenNoFilters() {
        InternalResolveUsersRequest request = new InternalResolveUsersRequest();
        // No roleNames, specializationIds, or userIds set

        User user = createUser("test@hcmut.edu.vn", "F", "L");
        Role role = createRole("STUDENT");
        user.setRole(role);

        var root = mock(jakarta.persistence.criteria.Root.class, RETURNS_DEEP_STUBS);
        var query = mock(jakarta.persistence.criteria.CriteriaQuery.class);
        var cb = mock(jakarta.persistence.criteria.CriteriaBuilder.class, RETURNS_DEEP_STUBS);

        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class)))
                .thenAnswer(invocation -> {
                    org.springframework.data.jpa.domain.Specification<User> spec =
                            invocation.getArgument(0);
                    spec.toPredicate(root, query, cb);
                    return List.of(user);
                });

        List<InternalUserSummaryResponse> result = userService.resolveUsers(request);

        assertEquals(1, result.size());
    }

    // --- update with only email (no change) ---

    @Test
    void update_shouldThrowException_whenRoleNotFound() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        UUID badRoleId = UUID.randomUUID();
        request.setRoleId(badRoleId);

        User existing = createUser("test@hcmut.edu.vn", "F", "L");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(roleRepository.findById(badRoleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.update(id, request));
    }

    @Test
    void update_shouldNotChangeRole_whenRoleIdNotProvided() {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = updateUserRequest(null, "F", "L");
        User existing = createUser("test@hcmut.edu.vn", "F", "L");
        UserResponse response = createUserResponse(id, "test@hcmut.edu.vn", "STUDENT");

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toResponse(existing)).thenReturn(response);

        userService.update(id, request);

        verify(roleRepository, never()).findById(any());
    }

    // --- helper methods ---

    private CreateUserRequest createUserRequest(String email, String password, String firstName, String lastName) {
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail(email);
        request.setPassword(password);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setRoleId(UUID.randomUUID());
        return request;
    }

    private UpdateUserRequest updateUserRequest(String email, String firstName, String lastName) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail(email);
        request.setFirstName(firstName);
        request.setLastName(lastName);
        return request;
    }

    private Role createRole(String name) {
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName(name);
        return role;
    }

    private User createUser(String email, String firstName, String lastName) {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }

    private UserResponse createUserResponse(UUID id, String email, String roleName) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setEmail(email);
        response.setRoleName(roleName);
        response.setFirstName("First");
        response.setLastName("Last");
        return response;
    }
}
