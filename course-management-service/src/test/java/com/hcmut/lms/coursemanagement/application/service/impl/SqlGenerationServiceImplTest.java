package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.GradeHistoryRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SqlGenerationResponse;
import com.hcmut.lms.coursemanagement.client.UserManagementClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import feign.FeignException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlGenerationServiceImplTest {

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private UserManagementClient userManagementClient;

    @InjectMocks
    private SqlGenerationServiceImpl sqlGenerationService;

    private GradeHistoryRequest validRequest;
    private UUID subjectId;
    private UUID semesterId;
    private UUID specId;
    private UUID academicYearId;
    private UUID existingUserId;

    @BeforeEach
    void setUp() {
        subjectId = UUID.randomUUID();
        semesterId = UUID.randomUUID();
        specId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();

        GradeHistoryRequest.StudentInfo studentInfo = GradeHistoryRequest.StudentInfo.builder()
                .studentCode("SV001")
                .email("sv001@example.com")
                .firstName("John")
                .lastName("Doe")
                .specializationCode("CS")
                .intakeYearCode("2022")
                .build();

        GradeHistoryRequest.GradeRecord record = GradeHistoryRequest.GradeRecord.builder()
                .subjectCode("MATH101")
                .credits(3)
                .gradeNumeric(8.5f)
                .gradeLetter("A")
                .semesterCode("HK221")
                .academicYearCode("22")
                .isPassed(true)
                .build();

        validRequest = GradeHistoryRequest.builder()
                .studentInfo(studentInfo)
                .gradeRecords(List.of(record))
                .build();
    }

    // -- generateInitSqlFromGradeHistory: new student --

    @Test
    void generateInitSqlFromGradeHistory_shouldReturnSql_whenNewStudent() {
        Specialization spec = new Specialization();
        spec.setId(specId);
        spec.setCode("CS");

        AcademicYear ay = new AcademicYear();
        ay.setId(academicYearId);
        ay.setYearCode("2022");
        ay.setStartDate(LocalDate.of(2022, 9, 1));

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCode("MATH101");
        subject.setSubjectGroupId(UUID.randomUUID());

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");

        when(specializationRepository.existsByCode("CS")).thenReturn(true);
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(ay));
        when(subjectRepository.existsByCode("MATH101")).thenReturn(true);
        when(semesterRepository.findBySemesterCode("HK221")).thenReturn(Optional.of(semester));

        // Step 2: student not found (new student)
        when(userManagementClient.getStudentByStudentCode("SV001"))
                .thenThrow(FeignException.NotFound.class);

        // Step 3: batch load
        when(subjectRepository.findAll()).thenReturn(List.of(subject));
        when(semesterRepository.findAll()).thenReturn(List.of(semester));
        when(userManagementClient.getTeachersBySubjectGroupId(subject.getSubjectGroupId())).thenReturn(List.of());

        // Step 4: resolve specialization and academic year from repos
        when(specializationRepository.findByCode("CS")).thenReturn(Optional.of(spec));
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(ay));

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);
            filesMock.when(() -> Files.writeString(any(Path.class), any(CharSequence.class),
                    any(java.nio.charset.Charset.class), any(java.nio.file.OpenOption.class), any(java.nio.file.OpenOption.class)))
                    .thenReturn(null);

            SqlGenerationResponse result = sqlGenerationService.generateInitSqlFromGradeHistory(validRequest);

            assertNotNull(result);
            assertNotNull(result.getSqlContent());
            assertTrue(result.getSqlContent().contains("Create new user"));
            assertTrue(result.getTotalRecords() >= 3);
            assertEquals("SQL initialization file generated successfully", result.getMessage());
        }
    }

    // -- generateInitSqlFromGradeHistory: existing student --

    @Test
    void generateInitSqlFromGradeHistory_shouldReturnSql_whenExistingStudent() {
        existingUserId = UUID.randomUUID();

        Specialization spec = new Specialization();
        spec.setId(specId);
        spec.setCode("CS");

        AcademicYear ay = new AcademicYear();
        ay.setId(academicYearId);
        ay.setYearCode("2022");
        ay.setStartDate(LocalDate.of(2022, 9, 1));

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCode("MATH101");
        subject.setSubjectGroupId(UUID.randomUUID());

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");

        UserResponse existingUser = new UserResponse();
        existingUser.setId(existingUserId);
        existingUser.setStudentCode("SV001");

        when(specializationRepository.existsByCode("CS")).thenReturn(true);
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(ay));
        when(subjectRepository.existsByCode("MATH101")).thenReturn(true);
        when(semesterRepository.findBySemesterCode("HK221")).thenReturn(Optional.of(semester));

        // Step 2: existing student
        when(userManagementClient.getStudentByStudentCode("SV001")).thenReturn(existingUser);

        // Step 3: batch load
        when(subjectRepository.findAll()).thenReturn(List.of(subject));
        when(semesterRepository.findAll()).thenReturn(List.of(semester));
        when(userManagementClient.getTeachersBySubjectGroupId(subject.getSubjectGroupId())).thenReturn(List.of());

        when(specializationRepository.findByCode("CS")).thenReturn(Optional.of(spec));
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(ay));

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);
            filesMock.when(() -> Files.writeString(any(Path.class), any(CharSequence.class),
                    any(java.nio.charset.Charset.class), any(java.nio.file.OpenOption.class), any(java.nio.file.OpenOption.class)))
                    .thenReturn(null);

            SqlGenerationResponse result = sqlGenerationService.generateInitSqlFromGradeHistory(validRequest);

            assertNotNull(result);
            assertTrue(result.getSqlContent().contains("Using existing student"));
        }
    }

    // -- validation fails --

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenValidationFails() {
        when(specializationRepository.existsByCode("CS")).thenReturn(false);
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.empty());
        when(subjectRepository.existsByCode("MATH101")).thenReturn(false);
        when(semesterRepository.findBySemesterCode("HK221")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> sqlGenerationService.generateInitSqlFromGradeHistory(validRequest));
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenSpecializationNotFound() {
        when(specializationRepository.existsByCode("CS")).thenReturn(true);
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(
                AcademicYear.builder().id(academicYearId).yearCode("2022").startDate(LocalDate.of(2022, 9, 1)).build()));
        when(subjectRepository.existsByCode("MATH101")).thenReturn(true);
        when(semesterRepository.findBySemesterCode("HK221")).thenReturn(Optional.of(
                Semester.builder().id(semesterId).semesterCode("HK221").build()));
        when(userManagementClient.getStudentByStudentCode("SV001"))
                .thenThrow(FeignException.NotFound.class);
        when(subjectRepository.findAll()).thenReturn(List.of());
        when(semesterRepository.findAll()).thenReturn(List.of());
        when(specializationRepository.findByCode("CS")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> sqlGenerationService.generateInitSqlFromGradeHistory(validRequest));
    }

    @Test
    void generateInitSqlFromGradeHistory_shouldThrowException_whenWriteFileFails() {
        Specialization spec = new Specialization();
        spec.setId(specId);
        spec.setCode("CS");

        AcademicYear ay = new AcademicYear();
        ay.setId(academicYearId);
        ay.setYearCode("2022");
        ay.setStartDate(LocalDate.of(2022, 9, 1));

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCode("MATH101");
        subject.setSubjectGroupId(UUID.randomUUID());

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");

        when(specializationRepository.existsByCode("CS")).thenReturn(true);
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(ay));
        when(subjectRepository.existsByCode("MATH101")).thenReturn(true);
        when(semesterRepository.findBySemesterCode("HK221")).thenReturn(Optional.of(semester));
        when(userManagementClient.getStudentByStudentCode("SV001"))
                .thenThrow(FeignException.NotFound.class);
        when(subjectRepository.findAll()).thenReturn(List.of(subject));
        when(semesterRepository.findAll()).thenReturn(List.of(semester));
        when(userManagementClient.getTeachersBySubjectGroupId(subject.getSubjectGroupId())).thenReturn(List.of());
        when(specializationRepository.findByCode("CS")).thenReturn(Optional.of(spec));
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(ay));

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            filesMock.when(() -> Files.createDirectories(any(Path.class)))
                    .thenThrow(new IOException("Disk full"));

            assertThrows(RuntimeException.class,
                    () -> sqlGenerationService.generateInitSqlFromGradeHistory(validRequest));
        }
    }
}
