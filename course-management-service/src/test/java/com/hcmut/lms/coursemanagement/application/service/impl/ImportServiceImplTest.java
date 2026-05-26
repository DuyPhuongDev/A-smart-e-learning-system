package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionImportData;
import com.hcmut.lms.coursemanagement.application.dto.response.ImportResultResponse;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import com.hcmut.lms.coursemanagement.util.ExcelUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private ImportServiceImpl importService;

    private static final UUID SEMESTER_ID = UUID.randomUUID();
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();
    private static final UUID TEACHER_ID = UUID.randomUUID();

    private ClassSectionImportData buildValidImportRow() {
        return ClassSectionImportData.builder()
                .rowNumber(2)
                .subjectCode("SUB101")
                .classCode("CS101")
                .sectionName("Section A")
                .status("OPEN")
                .maxStudents(50)
                .teacherCode("TC001")
                .build();
    }

    private Semester buildSemester() {
        Semester sem = new Semester();
        sem.setId(SEMESTER_ID);
        sem.setSemesterCode("2024.1");
        return sem;
    }

    private Subject buildSubject() {
        Subject s = new Subject();
        s.setId(UUID.randomUUID());
        s.setCode("SUB101");
        s.setName("Calculus");
        s.setCredits(3);
        return s;
    }

    private UserResponse buildTeacher() {
        UserResponse user = new UserResponse();
        user.setId(TEACHER_ID);
        user.setTeacherCode("TC001");
        return user;
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnResult_whenAllRowsValid() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of(buildTeacher()));
        when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.of(buildSubject()));
        when(classSectionRepository.existsByCode("CS101")).thenReturn(false);
        when(classSectionRepository.saveAll(anyList())).thenReturn(List.of());

        ClassSectionImportData data = buildValidImportRow();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(1, result.getSuccessCount());
            assertEquals(0, result.getFailedCount());
            assertNull(result.getErrors());
        }
        verify(classSectionRepository).saveAll(anyList());
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenSemesterNotFound() {
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID));
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenMissingClassCode() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());

        ClassSectionImportData data = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode("SUB101").classCode(null).build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
            assertNotNull(result.getErrors());
            assertEquals("Class Code", result.getErrors().get(0).getField());
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenMissingSubjectCode() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());

        ClassSectionImportData data = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode(null).classCode("CS101").build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
            assertEquals("Subject Code", result.getErrors().get(0).getField());
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenDuplicateClassCodeInFile() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());
        when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.of(buildSubject()));
        when(classSectionRepository.existsByCode("CS101")).thenReturn(false);

        ClassSectionImportData row1 = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode("SUB101").classCode("CS101").build();
        ClassSectionImportData row2 = ClassSectionImportData.builder()
                .rowNumber(3).subjectCode("SUB102").classCode("CS101").build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(row1, row2));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(1, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
            assertTrue(result.getErrors().get(0).getMessage().contains("trùng lặp"));
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenClassCodeAlreadyExistsInDb() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());
        when(classSectionRepository.existsByCode("CS101")).thenReturn(true);

        ClassSectionImportData data = buildValidImportRow();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
            assertTrue(result.getErrors().get(0).getMessage().contains("đã tồn tại"));
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenSubjectNotFound() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());
        when(classSectionRepository.existsByCode("CS101")).thenReturn(false);
        when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.empty());

        ClassSectionImportData data = buildValidImportRow();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
            assertTrue(result.getErrors().get(0).getMessage().contains("Môn học không tồn tại"));
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldUseDefaultStatus_whenInvalidStatusString() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of(buildTeacher()));
        when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.of(buildSubject()));
        when(classSectionRepository.existsByCode("CS101")).thenReturn(false);
        when(classSectionRepository.saveAll(anyList())).thenReturn(List.of());

        ClassSectionImportData data = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode("SUB101").classCode("CS101")
                .status("INVALID_STATUS").maxStudents(50).teacherCode("TC001").build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(1, result.getSuccessCount());
            assertEquals(0, result.getFailedCount());
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldReturnError_whenTeacherCodeNotFound() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of(buildTeacher()));
        when(classSectionRepository.existsByCode("CS101")).thenReturn(false);
        when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.of(buildSubject()));

        ClassSectionImportData data = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode("SUB101").classCode("CS101").teacherCode("TC999").build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
            assertTrue(result.getErrors().get(0).getMessage().contains("Mã giảng viên không tồn tại"));
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldHandleTeachersFetchException() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenThrow(new RuntimeException("Service unavailable"));
        lenient().when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.of(buildSubject()));
        lenient().when(classSectionRepository.existsByCode("CS101")).thenReturn(false);
        lenient().when(classSectionRepository.saveAll(anyList())).thenReturn(List.of());

        ClassSectionImportData data = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode("SUB101").classCode("CS101").teacherCode("TC001").build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(data));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(1, result.getFailedCount());
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldHandleAllRowsInvalid() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());

        List<ClassSectionImportData> rows = List.of(
                ClassSectionImportData.builder().rowNumber(2).classCode(null).subjectCode(null).build(),
                ClassSectionImportData.builder().rowNumber(3).classCode(null).subjectCode(null).build()
        );
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(rows);

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(0, result.getSuccessCount());
            assertEquals(2, result.getFailedCount());
            verify(classSectionRepository, never()).saveAll(anyList());
        }
    }

    @Test
    void importClassSectionsFromExcel_shouldCachedSubjectsAcrossRows() {
        when(mockFile.getOriginalFilename()).thenReturn("classes.xlsx");
        when(semesterRepository.findById(SEMESTER_ID)).thenReturn(Optional.of(buildSemester()));
        when(userServiceClient.getAllTeachers()).thenReturn(List.of());
        Subject subject = buildSubject();
        when(subjectRepository.findByCode("SUB101")).thenReturn(Optional.of(subject));
        when(classSectionRepository.existsByCode(anyString())).thenReturn(false);
        when(classSectionRepository.saveAll(anyList())).thenReturn(List.of());

        ClassSectionImportData row1 = ClassSectionImportData.builder()
                .rowNumber(2).subjectCode("SUB101").classCode("CS101").build();
        ClassSectionImportData row2 = ClassSectionImportData.builder()
                .rowNumber(3).subjectCode("SUB101").classCode("CS102").build();
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.parseClassSectionsFromExcel(mockFile))
                    .thenReturn(List.of(row1, row2));

            ImportResultResponse result = importService.importClassSectionsFromExcel(mockFile, SEMESTER_ID, CURRENT_USER_ID);

            assertEquals(2, result.getSuccessCount());
        }
        verify(subjectRepository, times(1)).findByCode("SUB101");
    }
}
