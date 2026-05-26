package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.config.CurriculumFallbackConfig;
import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumFullResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;
import com.hcmut.lms.coursemanagement.application.mapper.CurriculumMapper;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.*;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurriculumServiceImplTest {

    @Mock
    private CurriculumRepository curriculumRepository;

    @Mock
    private SpecializationRepository specializationRepository;

    @Mock
    private CurriculumMapper curriculumMapper;

    @Mock
    private AcademicYearRepository academicYearRepository;

    @Mock
    private CurriculumSectionRepository curriculumSectionRepository;

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @Mock
    private CurriculumSubjectPriorityRepository curriculumSubjectPriorityRepository;

    @Mock
    private CurriculumFallbackConfig curriculumFallbackConfig;

    @InjectMocks
    private CurriculumServiceImpl curriculumService;

    private UUID specId;
    private UUID intakeYearId;
    private CurriculumId curriculumId;
    private Curriculum curriculum;
    private CurriculumResponse curriculumResponse;

    @BeforeEach
    void setUp() {
        specId = UUID.randomUUID();
        intakeYearId = UUID.randomUUID();
        curriculumId = new CurriculumId("CURR2022", specId, intakeYearId);

        curriculum = new Curriculum();
        curriculum.setId(curriculumId);
        curriculum.setName("Curriculum 2022");
        curriculum.setTotalCredits(150);

        curriculumResponse = CurriculumResponse.builder()
                .code("CURR2022")
                .name("Curriculum 2022")
                .specializationId(specId)
                .intakeYearId(intakeYearId)
                .build();
    }

    // -- createCurriculum --

    @Test
    void createCurriculum_shouldReturnResponse_whenValidRequest() {
        CurriculumRequest request = new CurriculumRequest();
        request.setCode("CURR2022");
        request.setSpecializationId(specId);
        request.setIntakeYearId(intakeYearId);
        request.setName("Curriculum 2022");

        Specialization spec = new Specialization();
        spec.setId(specId);

        AcademicYear ay = new AcademicYear();
        ay.setId(intakeYearId);

        when(curriculumRepository.existsById(any(CurriculumId.class))).thenReturn(false);
        when(curriculumMapper.toEntity(request)).thenReturn(curriculum);
        when(specializationRepository.findById(specId)).thenReturn(Optional.of(spec));
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.of(ay));
        when(curriculumRepository.save(any(Curriculum.class))).thenReturn(curriculum);
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        CurriculumResponse result = curriculumService.createCurriculum(request);

        assertNotNull(result);
        assertEquals("CURR2022", result.getCode());
        verify(curriculumRepository).save(any(Curriculum.class));
    }

    @Test
    void createCurriculum_shouldThrowException_whenAlreadyExists() {
        CurriculumRequest request = new CurriculumRequest();
        request.setCode("CURR2022");
        request.setSpecializationId(specId);
        request.setIntakeYearId(intakeYearId);

        when(curriculumRepository.existsById(any(CurriculumId.class))).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> curriculumService.createCurriculum(request));
        verify(curriculumRepository, never()).save(any());
    }

    @Test
    void createCurriculum_shouldThrowException_whenSpecializationNotFound() {
        CurriculumRequest request = new CurriculumRequest();
        request.setCode("CURR2022");
        request.setSpecializationId(specId);
        request.setIntakeYearId(intakeYearId);

        when(curriculumRepository.existsById(any(CurriculumId.class))).thenReturn(false);
        when(curriculumMapper.toEntity(request)).thenReturn(curriculum);
        when(specializationRepository.findById(specId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> curriculumService.createCurriculum(request));
    }

    @Test
    void createCurriculum_shouldThrowException_whenIntakeYearNotFound() {
        CurriculumRequest request = new CurriculumRequest();
        request.setCode("CURR2022");
        request.setSpecializationId(specId);
        request.setIntakeYearId(intakeYearId);

        Specialization spec = new Specialization();
        spec.setId(specId);

        when(curriculumRepository.existsById(any(CurriculumId.class))).thenReturn(false);
        when(curriculumMapper.toEntity(request)).thenReturn(curriculum);
        when(specializationRepository.findById(specId)).thenReturn(Optional.of(spec));
        when(academicYearRepository.findById(intakeYearId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> curriculumService.createCurriculum(request));
    }

    // -- updateCurriculum --

    @Test
    void updateCurriculum_shouldReturnUpdatedResponse_whenExists() {
        CurriculumRequest request = new CurriculumRequest();
        request.setName("Updated Curriculum");

        when(curriculumRepository.findById(curriculumId)).thenReturn(Optional.of(curriculum));
        doAnswer(inv -> { curriculum.setName("Updated Curriculum"); return null; })
                .when(curriculumMapper).updateEntityFromRequest(eq(request), eq(curriculum));
        when(curriculumRepository.save(curriculum)).thenReturn(curriculum);
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        CurriculumResponse result = curriculumService.updateCurriculum("CURR2022", specId, intakeYearId, request);

        assertNotNull(result);
        verify(curriculumRepository).save(curriculum);
    }

    @Test
    void updateCurriculum_shouldThrowException_whenNotFound() {
        CurriculumRequest request = new CurriculumRequest();

        when(curriculumRepository.findById(any(CurriculumId.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.updateCurriculum("CURR2022", specId, intakeYearId, request));
    }

    // -- getCurriculumById --

    @Test
    void getCurriculumById_shouldReturnResponse_whenExists() {
        when(curriculumRepository.findById(curriculumId)).thenReturn(Optional.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        CurriculumResponse result = curriculumService.getCurriculumById("CURR2022", specId, intakeYearId);

        assertNotNull(result);
        assertEquals("CURR2022", result.getCode());
    }

    @Test
    void getCurriculumById_shouldThrowException_whenNotFound() {
        when(curriculumRepository.findById(any(CurriculumId.class))).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.getCurriculumById("CURR2022", specId, intakeYearId));
    }

    // -- getAllCurriculums --

    @Test
    void getAllCurriculums_shouldReturnList() {
        when(curriculumRepository.findAll()).thenReturn(List.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        List<CurriculumResponse> result = curriculumService.getAllCurriculums();

        assertEquals(1, result.size());
    }

    @Test
    void getAllCurriculums_shouldReturnEmptyList_whenNoData() {
        when(curriculumRepository.findAll()).thenReturn(List.of());
        List<CurriculumResponse> result = curriculumService.getAllCurriculums();
        assertTrue(result.isEmpty());
    }

    // -- getAllCurriculums paginated --

    @Test
    @SuppressWarnings("unchecked")
    void getAllCurriculumsPaginated_shouldReturnPageResponse() {
        Page<Curriculum> page = new PageImpl<>(List.of(curriculum));
        when(curriculumRepository.findAll((PageRequest) any())).thenReturn(page);
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        PageResponse<CurriculumResponse> result = curriculumService.getAllCurriculums(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // -- getCurriculumsBySpecializationId --

    @Test
    void getCurriculumsBySpecializationId_shouldReturnList() {
        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        List<CurriculumResponse> result = curriculumService.getCurriculumsBySpecializationId(specId);

        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCurriculumsBySpecializationIdPaginated_shouldReturnPageResponse() {
        Page<Curriculum> page = new PageImpl<>(List.of(curriculum));
        when(curriculumRepository.findByIdSpecializationId(eq(specId), any(PageRequest.class))).thenReturn(page);
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        PageResponse<CurriculumResponse> result = curriculumService.getCurriculumsBySpecializationId(specId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // -- getCurriculumsByIntakeYearId --

    @Test
    void getCurriculumsByIntakeYearId_shouldReturnList() {
        when(curriculumRepository.findByIdIntakeYearId(intakeYearId)).thenReturn(List.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        List<CurriculumResponse> result = curriculumService.getCurriculumsByIntakeYearId(intakeYearId);

        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCurriculumsByIntakeYearIdPaginated_shouldReturnPageResponse() {
        Page<Curriculum> page = new PageImpl<>(List.of(curriculum));
        when(curriculumRepository.findByIdIntakeYearId(eq(intakeYearId), any(PageRequest.class))).thenReturn(page);
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        PageResponse<CurriculumResponse> result = curriculumService.getCurriculumsByIntakeYearId(intakeYearId, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // -- resolveCurriculumBySpecializationAndIntakeYear --

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldReturnResponse_whenFoundDirect() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2022, 9, 1));
        curriculum.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        CurriculumResponse result = curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022);

        assertNotNull(result);
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldUseFallback_whenNotFoundDirect() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2021, 9, 1)); // Does NOT match 2022
        curriculum.setIntakeYear(ay);

        AcademicYear fallbackAy = new AcademicYear();
        fallbackAy.setId(UUID.randomUUID());
        fallbackAy.setStartDate(LocalDate.of(2022, 9, 1));
        fallbackAy.setYearCode("2022");

        Curriculum fallbackCurriculum = new Curriculum();
        CurriculumId fallbackId = new CurriculumId("CURR2022", specId, UUID.randomUUID());
        fallbackCurriculum.setId(fallbackId);
        fallbackCurriculum.setIntakeYear(fallbackAy);

        CurriculumResponse fallbackResponse = CurriculumResponse.builder()
                .code("CURR2022")
                .specializationId(specId)
                .build();

        when(curriculumRepository.findByIdSpecializationId(specId))
                .thenReturn(List.of(curriculum), List.of(fallbackCurriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2022");
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(fallbackAy));
        when(curriculumMapper.toResponse(fallbackCurriculum)).thenReturn(fallbackResponse);

        CurriculumResponse result = curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022);

        assertNotNull(result);
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldThrowException_whenNotFoundEvenWithFallback() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2021, 9, 1));
        curriculum.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("9999");
        when(academicYearRepository.findByYearCode("9999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldThrowException_whenMultipleMatches() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2022, 9, 1));

        Curriculum c1 = new Curriculum();
        c1.setIntakeYear(ay);
        Curriculum c2 = new Curriculum();
        c2.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(c1, c2));

        assertThrows(IllegalStateException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldThrowException_whenNullParams() {
        assertThrows(IllegalArgumentException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(null, null));
    }

    // -- deleteCurriculum --

    @Test
    void deleteCurriculum_shouldDelete_whenExists() {
        when(curriculumRepository.existsById(curriculumId)).thenReturn(true);
        curriculumService.deleteCurriculum("CURR2022", specId, intakeYearId);
        verify(curriculumRepository).deleteById(curriculumId);
    }

    @Test
    void deleteCurriculum_shouldThrowException_whenNotFound() {
        when(curriculumRepository.existsById(any(CurriculumId.class))).thenReturn(false);
        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.deleteCurriculum("CURR2022", specId, intakeYearId));
    }

    // -- getCurriculumFull --

    @Test
    void getCurriculumFull_shouldReturnFullResponse_whenExists() {
        CurriculumId cid = new CurriculumId("CURR2022", specId, intakeYearId);
        Curriculum c = new Curriculum();
        c.setId(cid);
        c.setName("Full Curriculum");
        c.setTotalCredits(150);

        CurriculumSection section = new CurriculumSection();
        section.setId(UUID.randomUUID());
        section.setName("Section 1");
        section.setRequiredCredits(10);
        section.setDisplayOrder(1);
        section.setPriorityWeight(100);
        section.setCurriculum(c);  // Required for filtering by curriculum code

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setCode("SUB101");
        subject.setName("Subject 101");
        subject.setCredits(3);

        CurriculumSubject cs = new CurriculumSubject();
        CurriculumSubjectId csId = new CurriculumSubjectId(section.getId(), subject.getId(), 1);
        cs.setId(csId);
        cs.setSubject(subject);
        cs.setCurriculumSection(section);
        cs.setIsRequired(true);
        cs.setDisplayOrder(1);
        cs.setPrerequisites(new ArrayList<>());
        cs.setRecommendations(new ArrayList<>());
        cs.setParallels(new ArrayList<>());

        section.setCurriculumSubjects(List.of(cs));

        when(curriculumRepository.findAll()).thenReturn(List.of(c));
        when(curriculumSectionRepository.findByCurriculumId("CURR2022", specId, intakeYearId))
                .thenReturn(List.of(section));
        when(curriculumSubjectRepository.findAll()).thenReturn(List.of(cs));
        when(curriculumSubjectPriorityRepository.findAll()).thenReturn(List.of());

        CurriculumFullResponse result = curriculumService.getCurriculumFull("CURR2022");

        assertNotNull(result);
        assertEquals("CURR2022", result.getCurriculumCode());
        assertEquals(1, result.getSections().size());
        assertEquals(1, result.getSections().get(0).getSubjects().size());
    }

    @Test
    void getCurriculumFull_shouldThrowException_whenNotFound() {
        when(curriculumRepository.findAll()).thenReturn(List.of());
        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.getCurriculumFull("NONEXISTENT"));
    }

    // -- resolveCurriculumBySpecializationAndIntakeYear additional --

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldThrowException_whenNoFallbackFound() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2021, 9, 1));
        curriculum.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2022");
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    // -- matchesIntakeYear -- (tested via resolveCurriculumBySpecializationAndIntakeYear)

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldMatchByYearCode() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(null); // No start date, will use yearCode
        ay.setYearCode("2022");
        curriculum.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        CurriculumResponse result = curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022);

        assertNotNull(result);
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldMatchByTwoDigitYearCode() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(null);
        ay.setYearCode("22");
        curriculum.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumMapper.toResponse(curriculum)).thenReturn(curriculumResponse);

        CurriculumResponse result = curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022);

        assertNotNull(result);
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldNotMatch_whenYearCodeNonNumeric() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(null);
        ay.setYearCode("ABC");
        curriculum.setIntakeYear(ay);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("9999");
        when(academicYearRepository.findByYearCode("9999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    // -- getCurriculumFull with duplicate priority keys --

    @Test
    void getCurriculumFull_shouldHandleDuplicatePriorityKeys() {
        CurriculumId cid = new CurriculumId("CURR2022", specId, intakeYearId);
        Curriculum c = new Curriculum();
        c.setId(cid);
        c.setName("Full Curriculum");
        c.setTotalCredits(150);

        CurriculumSection section = new CurriculumSection();
        section.setId(UUID.randomUUID());
        section.setName("Section 1");
        section.setRequiredCredits(10);
        section.setDisplayOrder(1);
        section.setPriorityWeight(100);
        section.setCurriculum(c);

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setCode("SUB101");
        subject.setName("Subject 101");
        subject.setCredits(3);

        CurriculumSubject cs = new CurriculumSubject();
        CurriculumSubjectId csId = new CurriculumSubjectId(section.getId(), subject.getId(), 1);
        cs.setId(csId);
        cs.setSubject(subject);
        cs.setCurriculumSection(section);
        cs.setIsRequired(true);
        cs.setDisplayOrder(1);
        cs.setPrerequisites(new ArrayList<>());
        cs.setRecommendations(new ArrayList<>());
        cs.setParallels(new ArrayList<>());

        section.setCurriculumSubjects(List.of(cs));

        // Create TWO priorities with the same composite key — triggers merge function (left, right) -> left
        CurriculumSubjectPriority priority1 = new CurriculumSubjectPriority();
        priority1.setCurriculumSectionId(section.getId());
        priority1.setSubjectId(subject.getId());
        priority1.setCurriculumSubjectId(1);
        priority1.setRecommendedYear(1);
        priority1.setRecommendedSemesterInYear(1);

        CurriculumSubjectPriority priority2 = new CurriculumSubjectPriority();
        priority2.setCurriculumSectionId(section.getId());
        priority2.setSubjectId(subject.getId());
        priority2.setCurriculumSubjectId(1);
        priority2.setRecommendedYear(2);
        priority2.setRecommendedSemesterInYear(2);

        when(curriculumRepository.findAll()).thenReturn(List.of(c));
        when(curriculumSectionRepository.findByCurriculumId("CURR2022", specId, intakeYearId))
                .thenReturn(List.of(section));
        when(curriculumSubjectRepository.findAll()).thenReturn(List.of(cs));
        when(curriculumSubjectPriorityRepository.findAll()).thenReturn(List.of(priority1, priority2));

        CurriculumFullResponse result = curriculumService.getCurriculumFull("CURR2022");

        assertNotNull(result);
        assertEquals(1, result.getSections().size());
    }

    // -- toRelation exercised via getCurriculumFull --

    @Test
    void getCurriculumFull_shouldIncludePrerequisitesInSubjectRelation() {
        CurriculumId cid = new CurriculumId("CURR2022", specId, intakeYearId);
        Curriculum c = new Curriculum();
        c.setId(cid);
        c.setName("Full Curriculum");
        c.setTotalCredits(150);

        CurriculumSection section = new CurriculumSection();
        section.setId(UUID.randomUUID());
        section.setName("Section 1");
        section.setRequiredCredits(10);
        section.setDisplayOrder(1);
        section.setPriorityWeight(100);
        section.setCurriculum(c);

        Subject sub1 = new Subject();
        sub1.setId(UUID.randomUUID());
        sub1.setCode("SUB101");
        sub1.setName("Subject 101");
        sub1.setCredits(3);

        Subject sub2 = new Subject();
        sub2.setId(UUID.randomUUID());
        sub2.setCode("SUB100");
        sub2.setName("Prerequisite Subject");
        sub2.setCredits(3);

        CurriculumSubject prereqCs = new CurriculumSubject();
        prereqCs.setSubject(sub2);

        com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite prereq =
                new com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite();
        prereq.setPrerequisiteCurriculumSubject(prereqCs);

        CurriculumSubject cs = new CurriculumSubject();
        CurriculumSubjectId csId = new CurriculumSubjectId(section.getId(), sub1.getId(), 1);
        cs.setId(csId);
        cs.setSubject(sub1);
        cs.setCurriculumSection(section);
        cs.setIsRequired(true);
        cs.setDisplayOrder(1);
        cs.setPrerequisites(new ArrayList<>(List.of(prereq)));
        cs.setRecommendations(new ArrayList<>());
        cs.setParallels(new ArrayList<>());

        section.setCurriculumSubjects(List.of(cs));

        when(curriculumRepository.findAll()).thenReturn(List.of(c));
        when(curriculumSectionRepository.findByCurriculumId("CURR2022", specId, intakeYearId))
                .thenReturn(List.of(section));
        when(curriculumSubjectRepository.findAll()).thenReturn(List.of(cs));
        when(curriculumSubjectPriorityRepository.findAll()).thenReturn(List.of());

        CurriculumFullResponse result = curriculumService.getCurriculumFull("CURR2022");

        assertNotNull(result);
        assertEquals(1, result.getSections().size());
        assertEquals(1, result.getSections().get(0).getSubjects().size());
        assertNotNull(result.getSections().get(0).getSubjects().get(0).getPrerequisites());
        assertEquals(1, result.getSections().get(0).getSubjects().get(0).getPrerequisites().size());
    }

    // -- resolveFallbackIntakeYear with yearCode parsing --

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldThrowException_whenEmptyEvenWithFallback() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2021, 9, 1));
        curriculum.setIntakeYear(ay);

        AcademicYear fallbackAy = new AcademicYear();
        fallbackAy.setId(intakeYearId);
        fallbackAy.setStartDate(LocalDate.of(2022, 9, 1));

        when(curriculumRepository.findByIdSpecializationId(specId))
                .thenReturn(List.of(curriculum)); // same list returned both times
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2022");
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(fallbackAy));

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldHandleNullIntakeYearInCurriculum() {
        curriculum.setIntakeYear(null);

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("9999");
        when(academicYearRepository.findByYearCode("9999")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldHandleNonNumericFallbackYearCode() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2021, 9, 1));
        curriculum.setIntakeYear(ay);

        AcademicYear fallbackAy = new AcademicYear();
        fallbackAy.setId(intakeYearId);
        fallbackAy.setStartDate(null);
        fallbackAy.setYearCode("INVALID_YEAR");

        when(curriculumRepository.findByIdSpecializationId(specId)).thenReturn(List.of(curriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2022");
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(fallbackAy));

        assertThrows(EntityNotFoundException.class,
                () -> curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022));
    }

    // -- getCurriculumFull with null subject in relation --

    @Test
    void getCurriculumFull_shouldHandleNullSubjectInPrerequisite() {
        CurriculumId cid = new CurriculumId("CURR2022", specId, intakeYearId);
        Curriculum c = new Curriculum();
        c.setId(cid);
        c.setName("Full Curriculum");
        c.setTotalCredits(150);

        CurriculumSection section = new CurriculumSection();
        section.setId(UUID.randomUUID());
        section.setName("Section 1");
        section.setRequiredCredits(10);
        section.setDisplayOrder(1);
        section.setPriorityWeight(100);
        section.setCurriculum(c);

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setCode("SUB101");
        subject.setName("Subject 101");
        subject.setCredits(3);

        // Create prerequisite CurriculumSubject with NULL subject
        CurriculumSubject prereqCs = new CurriculumSubject();
        prereqCs.setSubject(null); // null subject triggers return null in toRelation

        com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite prereq =
                new com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite();
        prereq.setPrerequisiteCurriculumSubject(prereqCs);

        CurriculumSubject cs = new CurriculumSubject();
        CurriculumSubjectId csId = new CurriculumSubjectId(section.getId(), subject.getId(), 1);
        cs.setId(csId);
        cs.setSubject(subject);
        cs.setCurriculumSection(section);
        cs.setIsRequired(true);
        cs.setDisplayOrder(1);
        cs.setPrerequisites(new ArrayList<>(List.of(prereq)));
        cs.setRecommendations(new ArrayList<>());
        cs.setParallels(new ArrayList<>());

        section.setCurriculumSubjects(List.of(cs));

        when(curriculumRepository.findAll()).thenReturn(List.of(c));
        when(curriculumSectionRepository.findByCurriculumId("CURR2022", specId, intakeYearId))
                .thenReturn(List.of(section));
        when(curriculumSubjectRepository.findAll()).thenReturn(List.of(cs));
        when(curriculumSubjectPriorityRepository.findAll()).thenReturn(List.of());

        CurriculumFullResponse result = curriculumService.getCurriculumFull("CURR2022");

        assertNotNull(result);
        // null-subject prerequisite is filtered out by .filter(Objects::nonNull) on the relation
    }

    @Test
    void resolveCurriculumBySpecializationAndIntakeYear_shouldParseFallbackYearCode() {
        AcademicYear ay = new AcademicYear();
        ay.setStartDate(LocalDate.of(2021, 9, 1));
        curriculum.setIntakeYear(ay);

        AcademicYear fallbackAy = new AcademicYear();
        fallbackAy.setStartDate(null);
        fallbackAy.setYearCode("2022");

        Curriculum fallbackCurriculum = new Curriculum();
        fallbackCurriculum.setIntakeYear(fallbackAy);

        CurriculumResponse fallbackResponse = CurriculumResponse.builder()
                .code("CURR2022")
                .specializationId(specId)
                .build();

        when(curriculumRepository.findByIdSpecializationId(specId))
                .thenReturn(List.of(curriculum), List.of(fallbackCurriculum));
        when(curriculumFallbackConfig.getFallbackIntakeYearCode()).thenReturn("2022");
        when(academicYearRepository.findByYearCode("2022")).thenReturn(Optional.of(fallbackAy));
        when(curriculumMapper.toResponse(fallbackCurriculum)).thenReturn(fallbackResponse);

        CurriculumResponse result = curriculumService.resolveCurriculumBySpecializationAndIntakeYear(specId, 2022);

        assertNotNull(result);
    }
}
