package com.hcmut.lms.usermanagement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.hcmut.lms.usermanagement.client.AuthServiceClient;
import com.hcmut.lms.usermanagement.exception.ImportValidationException;
import com.hcmut.lms.usermanagement.model.dto.response.ImportResultDto;
import com.hcmut.lms.usermanagement.model.entity.Role;
import com.hcmut.lms.usermanagement.model.entity.User;
import com.hcmut.lms.usermanagement.repository.AdminRepository;
import com.hcmut.lms.usermanagement.repository.RoleRepository;
import com.hcmut.lms.usermanagement.repository.StudentRepository;
import com.hcmut.lms.usermanagement.repository.TeacherRepository;
import com.hcmut.lms.usermanagement.repository.UserRepository;
import com.hcmut.lms.usermanagement.service.UserService;
import com.hcmut.lms.usermanagement.util.ExcelUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class ImportExportServiceImplTest {

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
    private UserService userService;

    @Mock
    private AuthServiceClient authServiceClient;

    @InjectMocks
    private ImportExportServiceImpl importExportService;

    // --- happy path ---

    @Test
    void importUsersFromExcel_shouldReturnResult_whenValidFile() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "student@hcmut.edu.vn", "First", "Last", "STUDENT");
        data.setStudentCode("S001");

        Role role = role("STUDENT");
        User savedUser = savedUser("student@hcmut.edu.vn");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
            when(studentRepository.existsByStudentCode("S001")).thenReturn(false);
            when(userRepository.findByEmail("student@hcmut.edu.vn")).thenReturn(Optional.of(savedUser));

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertNotNull(result);
            assertEquals(1, result.getTotalRows());
            assertEquals(1, result.getSuccessCount());
            verify(userService).create(any());
            verify(authServiceClient).createUserCredentials(any());
        }
    }

    @Test
    void importUsersFromExcel_shouldReturnResult_whenEmptyFile() {
        MultipartFile file = mock(MultipartFile.class);

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(Collections.emptyList());

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertEquals(0, result.getTotalRows());
            assertEquals(0, result.getSuccessCount());
        }
    }

    // --- each validation error ---

    @Test
    void import_shouldThrow_whenMissingEmail() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, null, "First", "Last", "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenEmptyEmail() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "", "First", "Last", "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenMissingFirstName() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", null, "Last", "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenEmptyFirstName() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "", "Last", "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenMissingLastName() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", null, "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenEmptyLastName() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "", "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenMissingRoleName() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "Last", null);
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenEmptyRoleName() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "Last", "");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenInvalidEmailFormat() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "not-an-email", "First", "Last", "STUDENT");
        assertImportThrows(file, data);
    }

    @Test
    void import_shouldThrow_whenDuplicateEmailInFile() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data1 = userData(2, "dup@hcmut.edu.vn", "First", "Last", "STUDENT");
        data1.setStudentCode("S001");
        ExcelUtil.UserImportData data2 = userData(3, "dup@hcmut.edu.vn", "Other", "User", "STUDENT");
        data2.setStudentCode("S002");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data1, data2));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role("STUDENT")));
            when(studentRepository.existsByStudentCode("S001")).thenReturn(false);

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenEmailExistsInDb() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "existing@hcmut.edu.vn", "First", "Last", "STUDENT");
        data.setStudentCode("S001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(userRepository.existsByEmail("existing@hcmut.edu.vn")).thenReturn(true);

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenRoleNotFound() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "Last", "UNKNOWN");
        data.setStudentCode("S001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    // --- student validation ---

    @Test
    void import_shouldThrow_whenStudentMissingStudentCode() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "Last", "STUDENT");
        // no studentCode set

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role("STUDENT")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenStudentEmptyStudentCode() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "Last", "STUDENT");
        data.setStudentCode("");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role("STUDENT")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenDuplicateStudentCodeInFile() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data1 = userData(2, "a@hcmut.edu.vn", "A", "A", "STUDENT");
        data1.setStudentCode("S001");
        ExcelUtil.UserImportData data2 = userData(3, "b@hcmut.edu.vn", "B", "B", "STUDENT");
        data2.setStudentCode("S001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data1, data2));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role("STUDENT")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenStudentCodeExistsInDb() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "test@hcmut.edu.vn", "First", "Last", "STUDENT");
        data.setStudentCode("S001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role("STUDENT")));
            when(studentRepository.existsByStudentCode("S001")).thenReturn(true);

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    // --- teacher validation ---

    @Test
    void import_shouldReturnResult_whenTeacherValid() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "teacher@hcmut.edu.vn", "First", "Last", "TEACHER");
        data.setTeacherCode("T001");
        data.setTeacherBio("Bio text");

        Role role = role("TEACHER");
        User savedUser = savedUser("teacher@hcmut.edu.vn");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role));
            when(teacherRepository.existsByTeacherCode("T001")).thenReturn(false);
            when(userRepository.findByEmail("teacher@hcmut.edu.vn")).thenReturn(Optional.of(savedUser));

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertEquals(1, result.getSuccessCount());
        }
    }

    @Test
    void import_shouldThrow_whenTeacherMissingTeacherCode() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "t@hcmut.edu.vn", "First", "Last", "TEACHER");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role("TEACHER")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenTeacherEmptyTeacherCode() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "t@hcmut.edu.vn", "First", "Last", "TEACHER");
        data.setTeacherCode("");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role("TEACHER")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenDuplicateTeacherCodeInFile() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data1 = userData(2, "a@hcmut.edu.vn", "A", "A", "TEACHER");
        data1.setTeacherCode("T001");
        ExcelUtil.UserImportData data2 = userData(3, "b@hcmut.edu.vn", "B", "B", "TEACHER");
        data2.setTeacherCode("T001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data1, data2));
            when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role("TEACHER")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenTeacherCodeExistsInDb() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "t@hcmut.edu.vn", "First", "Last", "TEACHER");
        data.setTeacherCode("T001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role("TEACHER")));
            when(teacherRepository.existsByTeacherCode("T001")).thenReturn(true);

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    // --- admin validation ---

    @Test
    void import_shouldReturnResult_whenAdminValidWithCode() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "admin@hcmut.edu.vn", "First", "Last", "ADMIN");
        data.setAdminCode("A001");

        Role role = role("ADMIN");
        User savedUser = savedUser("admin@hcmut.edu.vn");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
            when(userRepository.findByEmail("admin@hcmut.edu.vn")).thenReturn(Optional.of(savedUser));

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertEquals(1, result.getSuccessCount());
        }
    }

    @Test
    void import_shouldReturnResult_whenAdminNoAdminCode() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "admin2@hcmut.edu.vn", "First", "Last", "ADMIN");
        // no adminCode → should pass through without checking duplicates

        Role role = role("ADMIN");
        User savedUser = savedUser("admin2@hcmut.edu.vn");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role));
            when(userRepository.findByEmail("admin2@hcmut.edu.vn")).thenReturn(Optional.of(savedUser));

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertEquals(1, result.getSuccessCount());
        }
    }

    @Test
    void import_shouldThrow_whenDuplicateAdminCodeInFile() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data1 = userData(2, "a@hcmut.edu.vn", "A", "A", "ADMIN");
        data1.setAdminCode("A001");
        ExcelUtil.UserImportData data2 = userData(3, "b@hcmut.edu.vn", "B", "B", "ADMIN");
        data2.setAdminCode("A001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data1, data2));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role("ADMIN")));

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    @Test
    void import_shouldThrow_whenAdminCodeExistsInDb() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "admin@hcmut.edu.vn", "First", "Last", "ADMIN");
        data.setAdminCode("A001");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("ADMIN")).thenReturn(Optional.of(role("ADMIN")));
            when(adminRepository.existsByAdminCode("A001")).thenReturn(true);

            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    // --- phase 2 catch block ---

    @Test
    void import_shouldCaptureCreateError_whenUserServiceFails() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "fail@hcmut.edu.vn", "First", "Last", "STUDENT");
        data.setStudentCode("S001");

        Role role = role("STUDENT");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
            when(studentRepository.existsByStudentCode("S001")).thenReturn(false);
            doThrow(new RuntimeException("Create failed")).when(userService).create(any());

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertEquals(1, result.getTotalRows());
            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
        }
    }

    @Test
    void import_shouldContinueOnAuthServiceFailure() {
        MultipartFile file = mock(MultipartFile.class);
        ExcelUtil.UserImportData data = userData(2, "student@hcmut.edu.vn", "First", "Last", "STUDENT");
        data.setStudentCode("S001");

        Role role = role("STUDENT");
        User savedUser = savedUser("student@hcmut.edu.vn");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
            when(studentRepository.existsByStudentCode("S001")).thenReturn(false);
            when(userRepository.findByEmail("student@hcmut.edu.vn")).thenReturn(Optional.of(savedUser));
            doThrow(new RuntimeException("Auth service down")).when(authServiceClient).createUserCredentials(any());

            ImportResultDto result = importExportService.importUsersFromExcel(file);

            assertEquals(1, result.getSuccessCount());
        }
    }

    // --- export ---

    @Test
    void exportUsersToExcel_shouldReturnByteArray_whenDataExists() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@hcmut.edu.vn");
        user.setFirstName("First");
        user.setLastName("Last");

        byte[] expectedBytes = new byte[] { 1, 2, 3 };

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(expectedBytes);

            byte[] result = importExportService.exportUsersToExcel(null, null, null);

            assertNotNull(result);
            assertEquals(expectedBytes.length, result.length);
        }
    }

    @Test
    void exportUsersToExcel_shouldReturnEmptyExcel_whenNoData() {
        byte[] expectedBytes = new byte[] {};

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(expectedBytes);

            byte[] result = importExportService.exportUsersToExcel(null, null, null);

            assertEquals(0, result.length);
        }
    }

    @Test
    void exportUsersToExcel_shouldFilterBySearch_whenSearchMatchesEmail() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setEmail("match@hcmut.edu.vn");
        user1.setFirstName("X");
        user1.setLastName("Y");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setEmail("other@hcmut.edu.vn");
        user2.setFirstName("Z");
        user2.setLastName("W");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user1, user2));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] { 1 });

            byte[] result = importExportService.exportUsersToExcel("match", null, null);

            assertNotNull(result);
        }
    }

    @Test
    void exportUsersToExcel_shouldFilterBySearch_whenSearchMatchesFirstName() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@hcmut.edu.vn");
        user.setFirstName("John");
        user.setLastName("Doe");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] { 1 });

            byte[] result = importExportService.exportUsersToExcel("john", null, null);

            assertNotNull(result);
        }
    }

    @Test
    void exportUsersToExcel_shouldFilterBySearch_whenSearchMatchesLastName() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@hcmut.edu.vn");
        user.setFirstName("John");
        user.setLastName("Doe");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] { 1 });

            byte[] result = importExportService.exportUsersToExcel("doe", null, null);

            assertNotNull(result);
        }
    }

    @Test
    void exportUsersToExcel_shouldFilterBySearch_whenNoneMatch() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@hcmut.edu.vn");
        user.setFirstName("John");
        user.setLastName("Doe");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] {});

            byte[] result = importExportService.exportUsersToExcel("nomatch", null, null);

            assertNotNull(result);
        }
    }

    @Test
    void exportUsersToExcel_shouldHandleNullFields_whenFiltering() {
        User user = new User();
        user.setId(UUID.randomUUID());
        // email, firstName, lastName all null

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] {});

            byte[] result = importExportService.exportUsersToExcel("search", null, null);

            assertNotNull(result);
        }
    }

    @Test
    void exportUsersToExcel_shouldHandleEmptySearchString() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@hcmut.edu.vn");

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] { 1 });

            byte[] result = importExportService.exportUsersToExcel("", null, null);

            assertNotNull(result);
        }
    }

    @Test
    void exportUsersToExcel_shouldSkipSearchFilter_whenSearchIsNull() {
        User user = new User();
        user.setId(UUID.randomUUID());

        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            when(userRepository.findAll()).thenReturn(List.of(user));
            excelMock.when(() -> ExcelUtil.exportUsersToExcel(any())).thenReturn(new byte[] { 1 });

            byte[] result = importExportService.exportUsersToExcel(null, null, null);

            assertNotNull(result);
        }
    }

    // --- helper ---

    private void assertImportThrows(MultipartFile file, ExcelUtil.UserImportData data) {
        try (MockedStatic<ExcelUtil> excelMock = org.mockito.Mockito.mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseExcelFile(file)).thenReturn(List.of(data));
            assertThrows(ImportValidationException.class, () -> importExportService.importUsersFromExcel(file));
        }
    }

    private ExcelUtil.UserImportData userData(int row, String email, String firstName, String lastName, String roleName) {
        ExcelUtil.UserImportData data = new ExcelUtil.UserImportData();
        data.setRowNumber(row);
        data.setEmail(email);
        data.setFirstName(firstName);
        data.setLastName(lastName);
        data.setRoleName(roleName);
        return data;
    }

    private Role role(String name) {
        Role r = new Role();
        r.setId(UUID.randomUUID());
        r.setName(name);
        return r;
    }

    private User savedUser(String email) {
        User u = new User();
        u.setId(UUID.randomUUID());
        u.setEmail(email);
        return u;
    }
}
