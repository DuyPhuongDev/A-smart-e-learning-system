package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionExportData;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.util.ExcelUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceImplTest {

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ExportServiceImpl exportService;

    private static final UUID TEACHER_ID = UUID.randomUUID();
    private static final String TEACHER_NAME = "Nguyen Van A";

    // --- Helpers ---

    private ClassSection buildClassSection(UUID teacherId, Subject subject, Semester semester) {
        ClassSection cs = new ClassSection();
        cs.setCode("CS101");
        cs.setSectionName("Section A");
        cs.setStatus(ClassStatus.OPEN);
        cs.setMaxStudents(50);
        cs.setCurrentStudents(30);
        cs.setTeacherId(teacherId);
        cs.setSubject(subject);
        cs.setSemester(semester);
        return cs;
    }

    private Subject buildSubject(String code, String name, Integer credits) {
        Subject s = new Subject();
        s.setCode(code);
        s.setName(name);
        s.setCredits(credits);
        return s;
    }

    private Semester buildSemester(String code) {
        Semester sem = new Semester();
        sem.setSemesterCode(code);
        return sem;
    }

    private UserResponse buildTeacher() {
        UserResponse user = new UserResponse();
        user.setId(TEACHER_ID);
        user.setFirstName("Nguyen");
        user.setLastName("Van A");
        user.setTeacherCode("TC001");
        return user;
    }

    // --- Tests ---

    @Test
    void exportSubjectsAndClassesToExcel_shouldReturnByteArray_whenDataWithTeacher() {
        Subject subject = buildSubject("SUB101", "Calculus", 3);
        Semester semester = buildSemester("2024.1");
        ClassSection cs = buildClassSection(TEACHER_ID, subject, semester);

        when(classSectionRepository.findAllWithSubjectAndSemester()).thenReturn(List.of(cs));
        when(userServiceClient.getUserById(TEACHER_ID)).thenReturn(buildTeacher());

        byte[] expectedBytes = new byte[]{1, 2, 3};
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()))
                    .thenReturn(expectedBytes);

            byte[] result = exportService.exportSubjectsAndClassesToExcel();

            assertArrayEquals(expectedBytes, result);
            excelMock.verify(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()));
        }
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldExport_whenTeacherIdIsNull() {
        Subject subject = buildSubject("SUB102", "Physics", 4);
        Semester semester = buildSemester("2024.1");
        ClassSection cs = buildClassSection(null, subject, semester);

        when(classSectionRepository.findAllWithSubjectAndSemester()).thenReturn(List.of(cs));

        byte[] expectedBytes = new byte[]{4, 5, 6};
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()))
                    .thenReturn(expectedBytes);

            byte[] result = exportService.exportSubjectsAndClassesToExcel();

            assertArrayEquals(expectedBytes, result);
            verify(userServiceClient, never()).getUserById(any());
        }
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldHandleTeacherFetchException() {
        Subject subject = buildSubject("SUB103", "Chemistry", 3);
        Semester semester = buildSemester("2024.1");
        ClassSection cs = buildClassSection(TEACHER_ID, subject, semester);

        when(classSectionRepository.findAllWithSubjectAndSemester()).thenReturn(List.of(cs));
        when(userServiceClient.getUserById(TEACHER_ID)).thenThrow(new RuntimeException("Service unavailable"));

        byte[] expectedBytes = new byte[]{7, 8, 9};
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()))
                    .thenReturn(expectedBytes);

            byte[] result = exportService.exportSubjectsAndClassesToExcel();

            assertArrayEquals(expectedBytes, result);
            // teacher name should be null, but export still succeeds
        }
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldHandleNullTeacherInResponse() {
        Subject subject = buildSubject("SUB104", "Biology", 3);
        Semester semester = buildSemester("2024.1");
        ClassSection cs = buildClassSection(TEACHER_ID, subject, semester);

        when(classSectionRepository.findAllWithSubjectAndSemester()).thenReturn(List.of(cs));
        when(userServiceClient.getUserById(TEACHER_ID)).thenReturn(null);

        byte[] expectedBytes = new byte[]{10, 11, 12};
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()))
                    .thenReturn(expectedBytes);

            byte[] result = exportService.exportSubjectsAndClassesToExcel();

            assertArrayEquals(expectedBytes, result);
        }
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldHandleNullSubjectAndSemesterFields() {
        // Class section with null subject and null semester
        ClassSection cs = new ClassSection();
        cs.setCode("CS999");
        cs.setSectionName("Section X");
        cs.setStatus(null);
        cs.setMaxStudents(20);
        cs.setCurrentStudents(5);
        cs.setTeacherId(null);
        cs.setSubject(null);
        cs.setSemester(null);

        when(classSectionRepository.findAllWithSubjectAndSemester()).thenReturn(List.of(cs));

        byte[] expectedBytes = new byte[]{13, 14, 15};
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()))
                    .thenReturn(expectedBytes);

            byte[] result = exportService.exportSubjectsAndClassesToExcel();

            assertArrayEquals(expectedBytes, result);
        }
    }

    @Test
    void exportSubjectsAndClassesToExcel_shouldReturnEmptyExcel_whenNoData() {
        when(classSectionRepository.findAllWithSubjectAndSemester()).thenReturn(List.of());

        byte[] expectedBytes = new byte[0];
        try (MockedStatic<ExcelUtil> excelMock = mockStatic(ExcelUtil.class)) {
            excelMock.when(() -> ExcelUtil.exportSubjectsWithClassSectionsToExcel(anyList()))
                    .thenReturn(expectedBytes);

            byte[] result = exportService.exportSubjectsAndClassesToExcel();

            assertArrayEquals(expectedBytes, result);
        }
    }
}
