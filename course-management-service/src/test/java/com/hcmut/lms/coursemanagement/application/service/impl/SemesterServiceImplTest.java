package com.hcmut.lms.coursemanagement.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SemesterRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SemesterResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SemesterMapper;
import com.hcmut.lms.coursemanagement.client.UserManagementClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class SemesterServiceImplTest {

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private UserManagementClient userManagementClient;

    @Mock
    private SemesterMapper semesterMapper;

    @InjectMocks
    private SemesterServiceImpl semesterService;

    private AcademicYear buildAcademicYear(UUID id, String yearCode) {
        AcademicYear ay = AcademicYear.builder()
                .yearCode(yearCode)
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();
        ay.setId(id);
        return ay;
    }

    // --- create ---

    @Test
    void createSemester_shouldReturnResponse_whenValidRequestWithAcademicYearId() {
        UUID academicYearId = UUID.randomUUID();
        UUID semesterId = UUID.randomUUID();

        SemesterRequest request = new SemesterRequest();
        request.setSemesterCode("HK251");
        request.setStartDate(LocalDate.of(2025, 9, 1));
        request.setEndDate(LocalDate.of(2026, 1, 15));
        request.setAcademicYearId(academicYearId);

        AcademicYear academicYear = buildAcademicYear(academicYearId, "2025");

        Semester entity = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 1, 15))
                .build();

        Semester savedEntity = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 1, 15))
                .build();
        savedEntity.setId(semesterId);

        SemesterResponse response = SemesterResponse.builder()
                .id(semesterId)
                .semesterCode("HK251")
                .build();

        when(semesterMapper.toEntity(any())).thenReturn(entity);
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));
        when(semesterRepository.save(entity)).thenReturn(savedEntity);
        when(semesterMapper.toResponse(savedEntity)).thenReturn(response);

        SemesterResponse result = semesterService.createSemester(request);

        assertNotNull(result);
        assertEquals(semesterId, result.getId());
        verify(semesterMapper).toEntity(request);
        verify(academicYearRepository).findById(academicYearId);
        verify(semesterRepository).save(entity);
        verify(semesterMapper).toResponse(savedEntity);
    }

    @Test
    void createSemester_shouldReturnResponse_whenValidRequestWithoutAcademicYearId() {
        UUID semesterId = UUID.randomUUID();

        SemesterRequest request = new SemesterRequest();
        request.setSemesterCode("HK251");
        request.setStartDate(LocalDate.of(2025, 9, 1));
        request.setEndDate(LocalDate.of(2026, 1, 15));
        request.setAcademicYearId(null);

        Semester entity = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 1, 15))
                .build();

        Semester savedEntity = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 1, 15))
                .build();
        savedEntity.setId(semesterId);

        SemesterResponse response = SemesterResponse.builder()
                .id(semesterId)
                .semesterCode("HK251")
                .build();

        when(semesterMapper.toEntity(any())).thenReturn(entity);
        when(semesterRepository.save(entity)).thenReturn(savedEntity);
        when(semesterMapper.toResponse(savedEntity)).thenReturn(response);

        SemesterResponse result = semesterService.createSemester(request);

        assertNotNull(result);
        assertEquals(semesterId, result.getId());
        verify(academicYearRepository, never()).findById(any());
    }

    @Test
    void createSemester_shouldThrowException_whenAcademicYearNotFound() {
        UUID academicYearId = UUID.randomUUID();

        SemesterRequest request = new SemesterRequest();
        request.setSemesterCode("HK251");
        request.setStartDate(LocalDate.of(2025, 9, 1));
        request.setEndDate(LocalDate.of(2026, 1, 15));
        request.setAcademicYearId(academicYearId);

        Semester entity = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 1, 15))
                .build();

        when(semesterMapper.toEntity(any())).thenReturn(entity);
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> semesterService.createSemester(request));
        verify(semesterRepository, never()).save(any());
    }

    // --- getById ---

    @Test
    void getSemesterById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Semester entity = Semester.builder().semesterCode("HK251").build();
        entity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK251")
                .build();

        when(semesterRepository.findById(id)).thenReturn(Optional.of(entity));
        when(semesterMapper.toResponse(entity)).thenReturn(response);

        SemesterResponse result = semesterService.getSemesterById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getSemesterById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(semesterRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> semesterService.getSemesterById(id));
    }

    // --- getAll ---

    @Test
    void getAllSemesters_shouldReturnList() {
        UUID id = UUID.randomUUID();
        Semester entity = Semester.builder().semesterCode("HK251").build();
        entity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK251")
                .build();

        when(semesterRepository.findAll()).thenReturn(List.of(entity));
        when(semesterMapper.toResponse(entity)).thenReturn(response);

        List<SemesterResponse> result = semesterService.getAllSemesters();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getAllSemesters_shouldReturnEmptyList_whenNone() {
        when(semesterRepository.findAll()).thenReturn(List.of());

        List<SemesterResponse> result = semesterService.getAllSemesters();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getAll paginated ---

    @Test
    void getAllSemestersPaginated_shouldReturnPageResponse() {
        UUID id = UUID.randomUUID();
        Semester entity = Semester.builder().semesterCode("HK251").build();
        entity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK251")
                .build();

        Page<Semester> entityPage = new PageImpl<>(List.of(entity));

        when(semesterRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(semesterMapper.toResponse(entity)).thenReturn(response);

        PageResponse<SemesterResponse> result = semesterService.getAllSemesters(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // --- getByAcademicYearId ---

    @Test
    void getSemestersByAcademicYearId_shouldReturnList() {
        UUID academicYearId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Semester entity = Semester.builder().semesterCode("HK251").build();
        entity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK251")
                .build();

        when(semesterRepository.findByAcademicYearId(academicYearId)).thenReturn(List.of(entity));
        when(semesterMapper.toResponse(entity)).thenReturn(response);

        List<SemesterResponse> result = semesterService.getSemestersByAcademicYearId(academicYearId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getSemestersByAcademicYearId_shouldReturnEmptyList_whenNone() {
        UUID academicYearId = UUID.randomUUID();
        when(semesterRepository.findByAcademicYearId(academicYearId)).thenReturn(List.of());

        List<SemesterResponse> result = semesterService.getSemestersByAcademicYearId(academicYearId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // --- getByAcademicYearId paginated ---

    @Test
    void getSemestersByAcademicYearIdPaginated_shouldReturnPageResponse() {
        UUID academicYearId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        Semester entity = Semester.builder().semesterCode("HK251").build();
        entity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK251")
                .build();

        Page<Semester> entityPage = new PageImpl<>(List.of(entity));

        when(semesterRepository.findByAcademicYearId(eq(academicYearId), any(Pageable.class)))
                .thenReturn(entityPage);
        when(semesterMapper.toResponse(entity)).thenReturn(response);

        PageResponse<SemesterResponse> result = semesterService
                .getSemestersByAcademicYearId(academicYearId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // --- getCurrentSemester ---

    @Test
    void getCurrentSemester_shouldReturnResponse_whenFound() {
        UUID id = UUID.randomUUID();
        Semester entity = Semester.builder().semesterCode("HK251").build();
        entity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK251")
                .build();

        when(semesterRepository.findCurrentSemester(any(LocalDate.class))).thenReturn(Optional.of(entity));
        when(semesterMapper.toResponse(entity)).thenReturn(response);

        SemesterResponse result = semesterService.getCurrentSemester();

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void getCurrentSemester_shouldThrowException_whenNoCurrentSemester() {
        when(semesterRepository.findCurrentSemester(any(LocalDate.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> semesterService.getCurrentSemester());
    }

    // --- getRemainSemester ---

    @Test
    void getRemainSemester_shouldReturnList_whenStudentExists() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setIntakeYearId(intakeYearId);

        AcademicYear intakeYear = AcademicYear.builder()
                .yearCode("2025")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();
        intakeYear.setId(intakeYearId);

        AcademicYear ay2 = AcademicYear.builder()
                .yearCode("2026")
                .startDate(LocalDate.of(2026, 9, 1))
                .endDate(LocalDate.of(2027, 7, 31))
                .build();
        ay2.setId(UUID.randomUUID());

        UUID semId = UUID.randomUUID();
        Semester semester = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2099, 12, 31)) // far future → included
                .build();
        semester.setId(semId);
        semester.setAcademicYear(intakeYear);

        SemesterResponse response = SemesterResponse.builder()
                .id(semId)
                .semesterCode("HK251")
                .build();

        when(userManagementClient.getUserById(studentId)).thenReturn(student);
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(intakeYear));
        when(academicYearRepository.findAll()).thenReturn(List.of(intakeYear, ay2));
        when(semesterRepository.findAll()).thenReturn(List.of(semester));
        when(semesterMapper.toResponse(semester)).thenReturn(response);

        List<SemesterResponse> result = semesterService.getRemainSemester(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(semId, result.get(0).getId());
    }

    @Test
    void getRemainSemester_shouldThrowException_whenStudentNotFound() {
        UUID studentId = UUID.randomUUID();
        when(userManagementClient.getUserById(studentId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> semesterService.getRemainSemester(studentId));
    }

    // --- update ---

    @Test
    void updateSemester_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();

        SemesterRequest request = new SemesterRequest();
        request.setSemesterCode("HK252");
        request.setStartDate(LocalDate.of(2026, 1, 16));
        request.setEndDate(LocalDate.of(2026, 7, 31));
        request.setAcademicYearId(academicYearId);

        Semester existingEntity = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 1, 15))
                .build();
        existingEntity.setId(id);

        AcademicYear academicYear = buildAcademicYear(academicYearId, "2025");

        Semester updatedEntity = Semester.builder()
                .semesterCode("HK252")
                .build();
        updatedEntity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK252")
                .build();

        when(semesterRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.of(academicYear));
        when(semesterRepository.save(existingEntity)).thenReturn(updatedEntity);
        when(semesterMapper.toResponse(updatedEntity)).thenReturn(response);

        SemesterResponse result = semesterService.updateSemester(id, request);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(semesterMapper).updateEntityFromRequest(request, existingEntity);
        verify(academicYearRepository).findById(academicYearId);
    }

    @Test
    void updateSemester_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        SemesterRequest request = new SemesterRequest();

        when(semesterRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> semesterService.updateSemester(id, request));
    }

    // --- update additional ---

    @Test
    void updateSemester_shouldThrowException_whenAcademicYearNotFound() {
        UUID id = UUID.randomUUID();
        UUID academicYearId = UUID.randomUUID();

        SemesterRequest request = new SemesterRequest();
        request.setAcademicYearId(academicYearId);

        Semester existingEntity = Semester.builder()
                .semesterCode("HK251")
                .build();
        existingEntity.setId(id);

        when(semesterRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(academicYearRepository.findById(academicYearId)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> semesterService.updateSemester(id, request));
        assertTrue(ex.getMessage().contains("Academic year not found"));
    }

    @Test
    void updateSemester_shouldUpdateWithoutAcademicYear_whenAcademicYearIdNull() {
        UUID id = UUID.randomUUID();

        SemesterRequest request = new SemesterRequest();
        request.setSemesterCode("HK252");
        request.setAcademicYearId(null);

        Semester existingEntity = Semester.builder()
                .semesterCode("HK251")
                .build();
        existingEntity.setId(id);

        SemesterResponse response = SemesterResponse.builder()
                .id(id)
                .semesterCode("HK252")
                .build();

        when(semesterRepository.findById(id)).thenReturn(Optional.of(existingEntity));
        when(semesterRepository.save(existingEntity)).thenReturn(existingEntity);
        when(semesterMapper.toResponse(existingEntity)).thenReturn(response);

        SemesterResponse result = semesterService.updateSemester(id, request);

        assertNotNull(result);
        verify(academicYearRepository, never()).findById(any());
    }

    // --- getRemainSemester additional ---

    @Test
    void getRemainSemester_shouldReturnEmpty_whenNoAcademicYearsInRange() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setIntakeYearId(intakeYearId);

        AcademicYear intakeYear = AcademicYear.builder()
                .yearCode("2025")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();
        intakeYear.setId(intakeYearId);

        when(userManagementClient.getUserById(studentId)).thenReturn(student);
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(intakeYear));
        when(academicYearRepository.findAll()).thenReturn(List.of());

        List<SemesterResponse> result = semesterService.getRemainSemester(studentId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getRemainSemester_shouldThrow_whenYearCodeNull() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setIntakeYearId(intakeYearId);

        AcademicYear intakeYear = AcademicYear.builder()
                .yearCode(null)
                .build();
        intakeYear.setId(intakeYearId);

        when(userManagementClient.getUserById(studentId)).thenReturn(student);
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(intakeYear));

        assertThrows(IllegalArgumentException.class, () -> semesterService.getRemainSemester(studentId));
    }

    @Test
    void getRemainSemester_shouldThrow_whenYearCodeNonNumeric() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setIntakeYearId(intakeYearId);

        AcademicYear intakeYear = AcademicYear.builder()
                .yearCode("ABC")
                .build();
        intakeYear.setId(intakeYearId);

        when(userManagementClient.getUserById(studentId)).thenReturn(student);
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(intakeYear));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> semesterService.getRemainSemester(studentId));
        assertTrue(ex.getMessage().contains("Cannot parse year from code"));
    }

    @Test
    void getRemainSemester_shouldHandleTwoDigitYearCode() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setIntakeYearId(intakeYearId);

        AcademicYear intakeYear = AcademicYear.builder()
                .yearCode("25")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();
        intakeYear.setId(intakeYearId);

        UUID semId = UUID.randomUUID();
        Semester semester = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2099, 12, 31))
                .build();
        semester.setId(semId);
        semester.setAcademicYear(intakeYear);

        SemesterResponse response = SemesterResponse.builder()
                .id(semId)
                .semesterCode("HK251")
                .build();

        when(userManagementClient.getUserById(studentId)).thenReturn(student);
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(intakeYear));
        when(academicYearRepository.findAll()).thenReturn(List.of(intakeYear));
        when(semesterRepository.findAll()).thenReturn(List.of(semester));
        when(semesterMapper.toResponse(semester)).thenReturn(response);

        List<SemesterResponse> result = semesterService.getRemainSemester(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRemainSemester_shouldIncludeSemesterEndingToday() {
        UUID studentId = UUID.randomUUID();
        UUID intakeYearId = UUID.randomUUID();

        UserResponse student = new UserResponse();
        student.setId(studentId);
        student.setIntakeYearId(intakeYearId);

        AcademicYear intakeYear = AcademicYear.builder()
                .yearCode("2025")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.of(2026, 7, 31))
                .build();
        intakeYear.setId(intakeYearId);

        UUID semId = UUID.randomUUID();
        Semester semester = Semester.builder()
                .semesterCode("HK251")
                .startDate(LocalDate.of(2025, 9, 1))
                .endDate(LocalDate.now())
                .build();
        semester.setId(semId);
        semester.setAcademicYear(intakeYear);

        SemesterResponse response = SemesterResponse.builder()
                .id(semId)
                .semesterCode("HK251")
                .build();

        when(userManagementClient.getUserById(studentId)).thenReturn(student);
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(intakeYear));
        when(academicYearRepository.findAll()).thenReturn(List.of(intakeYear));
        when(semesterRepository.findAll()).thenReturn(List.of(semester));
        when(semesterMapper.toResponse(semester)).thenReturn(response);

        List<SemesterResponse> result = semesterService.getRemainSemester(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // --- delete ---

    @Test
    void deleteSemester_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(semesterRepository.existsById(id)).thenReturn(true);

        semesterService.deleteSemester(id);

        verify(semesterRepository).deleteById(id);
    }

    @Test
    void deleteSemester_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(semesterRepository.existsById(id)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> semesterService.deleteSemester(id));
        verify(semesterRepository, never()).deleteById(any());
    }
}
