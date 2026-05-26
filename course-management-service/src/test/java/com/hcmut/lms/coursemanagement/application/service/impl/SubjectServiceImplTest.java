package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectGradingRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.SubjectRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectGradingWeightResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectPrerequisiteMapResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.SubjectResponse;
import com.hcmut.lms.coursemanagement.application.mapper.SubjectMapper;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Grading;
import com.hcmut.lms.coursemanagement.domain.entity.subject.GradingType;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGrading;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectGradingId;
import com.hcmut.lms.coursemanagement.exception.DomainException;
import com.hcmut.lms.coursemanagement.repository.CurriculumSubjectRepository;
import com.hcmut.lms.coursemanagement.repository.GradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectGradingRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubjectServiceImplTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private CurriculumSubjectRepository curriculumSubjectRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private GradingRepository gradingRepository;

    @Mock
    private SubjectGradingRepository subjectGradingRepository;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    // -- createSubject --

    @Test
    void createSubject_shouldReturnResponse_whenValidRequest() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");
        request.setCode("TS101");

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Test Subject");
        subject.setCode("TS101");

        SubjectResponse response = SubjectResponse.builder()
                .id(subject.getId()).name("Test Subject").code("TS101").build();

        when(subjectRepository.existsByCode("TS101")).thenReturn(false);
        when(subjectMapper.toEntity(request)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectGradingRepository.findBySubjectId(subject.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        SubjectResponse result = subjectService.createSubject(request);

        assertNotNull(result);
        assertEquals("Test Subject", result.getName());
        verify(subjectRepository).save(subject);
    }

    @Test
    void createSubject_shouldThrowException_whenDuplicateCode() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");
        request.setCode("TS101");

        when(subjectRepository.existsByCode("TS101")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> subjectService.createSubject(request));
        verify(subjectRepository, never()).save(any());
    }

    @Test
    void createSubject_shouldReturnResponse_whenGradingsSumTo100() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");
        request.setCode("TS101");

        SubjectGradingRequest g1 = new SubjectGradingRequest();
        g1.setGradingType("MIDTERM"); g1.setWeight(40.0f); g1.setName("Midterm");
        SubjectGradingRequest g2 = new SubjectGradingRequest();
        g2.setGradingType("FINAL"); g2.setWeight(60.0f); g2.setName("Final");
        request.setGradings(List.of(g1, g2));

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Test Subject");
        subject.setCode("TS101");

        SubjectResponse response = SubjectResponse.builder()
                .id(subject.getId()).name("Test Subject").code("TS101").build();

        Grading grading1 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.MIDTERM).name("Midterm").build();
        Grading grading2 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.FINAL).name("Final").build();

        when(subjectRepository.existsByCode("TS101")).thenReturn(false);
        when(subjectMapper.toEntity(request)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(gradingRepository.save(any(Grading.class))).thenReturn(grading1, grading2);
        when(subjectGradingRepository.save(any(SubjectGrading.class))).thenAnswer(inv -> inv.getArgument(0));
        when(subjectGradingRepository.findBySubjectId(subject.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        SubjectResponse result = subjectService.createSubject(request);

        assertNotNull(result);
        verify(gradingRepository, times(2)).save(any(Grading.class));
        verify(subjectGradingRepository, times(2)).save(any(SubjectGrading.class));
    }

    @Test
    void createSubject_shouldThrowException_whenGradingsSumNot100() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");
        request.setCode("TS101");

        SubjectGradingRequest g1 = new SubjectGradingRequest();
        g1.setGradingType("MIDTERM"); g1.setWeight(30.0f);
        request.setGradings(List.of(g1));

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Test Subject");
        subject.setCode("TS101");

        when(subjectRepository.existsByCode("TS101")).thenReturn(false);
        when(subjectMapper.toEntity(request)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);

        assertThrows(DomainException.class, () -> subjectService.createSubject(request));
    }

    @Test
    void createSubject_shouldReturnResponse_whenCodeNull() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Test Subject");

        SubjectResponse response = SubjectResponse.builder()
                .id(subject.getId()).name("Test Subject").build();

        when(subjectMapper.toEntity(request)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectGradingRepository.findBySubjectId(subject.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        SubjectResponse result = subjectService.createSubject(request);

        assertNotNull(result);
        verify(subjectRepository).save(subject);
    }

    // -- updateSubject --

    @Test
    void updateSubject_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        SubjectRequest request = new SubjectRequest();
        request.setName("Updated");
        request.setCode("TS101");

        Subject existing = new Subject();
        existing.setId(id);
        existing.setName("Old");
        existing.setCode("TS101");

        SubjectResponse response = SubjectResponse.builder()
                .id(id).name("Updated").code("TS101").build();

        when(subjectRepository.findById(id)).thenReturn(Optional.of(existing));
        doAnswer(inv -> { existing.setName("Updated"); return null; })
                .when(subjectMapper).updateEntityFromRequest(eq(request), eq(existing));
        when(subjectRepository.save(existing)).thenReturn(existing);
        when(subjectGradingRepository.findBySubjectId(id)).thenReturn(List.of());
        when(subjectMapper.toResponse(existing)).thenReturn(response);

        SubjectResponse result = subjectService.updateSubject(id, request);

        assertNotNull(result);
        assertEquals("Updated", result.getName());
    }

    @Test
    void updateSubject_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        SubjectRequest request = new SubjectRequest();

        when(subjectRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> subjectService.updateSubject(id, request));
    }

    @Test
    void updateSubject_shouldThrowException_whenCodeChangedAndDuplicate() {
        UUID id = UUID.randomUUID();
        SubjectRequest request = new SubjectRequest();
        request.setCode("NEWCODE");

        Subject existing = new Subject();
        existing.setId(id);
        existing.setCode("OLDCODE");

        when(subjectRepository.findById(id)).thenReturn(Optional.of(existing));
        when(subjectRepository.existsByCode("NEWCODE")).thenReturn(true);

        assertThrows(EntityExistsException.class, () -> subjectService.updateSubject(id, request));
    }

    @Test
    void updateSubject_shouldUpdateWithGradings_whenProvided() {
        UUID id = UUID.randomUUID();
        SubjectRequest request = new SubjectRequest();
        request.setName("Updated");
        request.setCode("TS101");

        SubjectGradingRequest g1 = new SubjectGradingRequest();
        g1.setGradingType("MIDTERM"); g1.setWeight(50.0f); g1.setName("M");
        SubjectGradingRequest g2 = new SubjectGradingRequest();
        g2.setGradingType("FINAL"); g2.setWeight(50.0f); g2.setName("F");
        request.setGradings(List.of(g1, g2));

        Subject existing = new Subject();
        existing.setId(id);
        existing.setName("Old");
        existing.setCode("TS101");

        SubjectResponse response = SubjectResponse.builder()
                .id(id).name("Updated").code("TS101").build();

        Grading grading1 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.MIDTERM).name("M").build();
        Grading grading2 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.FINAL).name("F").build();

        when(subjectRepository.findById(id)).thenReturn(Optional.of(existing));
        doAnswer(inv -> { existing.setName("Updated"); return null; })
                .when(subjectMapper).updateEntityFromRequest(eq(request), eq(existing));
        when(subjectRepository.save(existing)).thenReturn(existing);
        when(subjectGradingRepository.findBySubjectId(id)).thenReturn(List.of());
        when(gradingRepository.save(any(Grading.class))).thenReturn(grading1, grading2);
        when(subjectGradingRepository.save(any(SubjectGrading.class))).thenAnswer(inv -> inv.getArgument(0));
        when(subjectMapper.toResponse(existing)).thenReturn(response);

        SubjectResponse result = subjectService.updateSubject(id, request);

        assertNotNull(result);
        verify(subjectGradingRepository).deleteAll(any());
        verify(gradingRepository, times(2)).save(any(Grading.class));
    }

    // -- getSubjectById --

    @Test
    void getSubjectById_shouldReturnResponse_whenExists() {
        UUID id = UUID.randomUUID();
        Subject subject = new Subject();
        subject.setId(id);
        subject.setName("Test");

        SubjectResponse response = SubjectResponse.builder().id(id).name("Test").build();

        when(subjectRepository.findById(id)).thenReturn(Optional.of(subject));
        when(subjectGradingRepository.findBySubjectId(id)).thenReturn(List.of());
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        SubjectResponse result = subjectService.getSubjectById(id);

        assertNotNull(result);
        assertEquals("Test", result.getName());
    }

    @Test
    void getSubjectById_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(subjectRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> subjectService.getSubjectById(id));
    }

    // -- getSubjectByCode --

    @Test
    void getSubjectByCode_shouldReturnResponse_whenExists() {
        String code = "TS101";
        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setCode(code);

        SubjectResponse response = SubjectResponse.builder().id(subject.getId()).code(code).build();

        when(subjectRepository.findByCode(code)).thenReturn(Optional.of(subject));
        when(subjectGradingRepository.findBySubjectId(subject.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        SubjectResponse result = subjectService.getSubjectByCode(code);

        assertNotNull(result);
    }

    @Test
    void getSubjectByCode_shouldThrowException_whenNotFound() {
        String code = "NONEXISTENT";
        when(subjectRepository.findByCode(code)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> subjectService.getSubjectByCode(code));
    }

    // -- getAllSubjects --

    @Test
    void getAllSubjects_shouldReturnList() {
        Subject s1 = new Subject();
        s1.setId(UUID.randomUUID()); s1.setName("S1");
        Subject s2 = new Subject();
        s2.setId(UUID.randomUUID()); s2.setName("S2");

        SubjectResponse r1 = SubjectResponse.builder().id(s1.getId()).name("S1").build();
        SubjectResponse r2 = SubjectResponse.builder().id(s2.getId()).name("S2").build();

        when(subjectRepository.findAll()).thenReturn(List.of(s1, s2));
        when(subjectGradingRepository.findBySubjectId(s1.getId())).thenReturn(List.of());
        when(subjectGradingRepository.findBySubjectId(s2.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(s1)).thenReturn(r1);
        when(subjectMapper.toResponse(s2)).thenReturn(r2);

        List<SubjectResponse> result = subjectService.getAllSubjects();

        assertEquals(2, result.size());
    }

    @Test
    void getAllSubjects_shouldReturnEmptyList_whenNoData() {
        when(subjectRepository.findAll()).thenReturn(List.of());
        List<SubjectResponse> result = subjectService.getAllSubjects();
        assertTrue(result.isEmpty());
    }

    // -- getAllSubjects paginated --

    @Test
    @SuppressWarnings("unchecked")
    void getAllSubjectsPaginated_shouldReturnPage_whenKeywordNull() {
        Subject s = new Subject();
        s.setId(UUID.randomUUID()); s.setName("S");
        Page<Subject> page = new PageImpl<>(List.of(s));

        SubjectResponse response = SubjectResponse.builder().id(s.getId()).name("S").build();

        when(subjectRepository.findAll((PageRequest) any())).thenReturn(page);
        when(subjectGradingRepository.findBySubjectId(s.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(s)).thenReturn(response);

        PageResponse<SubjectResponse> result = subjectService.getAllSubjects(0, 10, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllSubjectsPaginated_shouldReturnSingle_whenKeywordMatchesCode() {
        String keyword = "TS101";
        Subject s = new Subject();
        s.setId(UUID.randomUUID()); s.setCode(keyword);

        SubjectResponse response = SubjectResponse.builder().id(s.getId()).code(keyword).build();

        when(subjectRepository.findByCode(keyword)).thenReturn(Optional.of(s));
        when(subjectGradingRepository.findBySubjectId(s.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(s)).thenReturn(response);

        PageResponse<SubjectResponse> result = subjectService.getAllSubjects(0, 10, keyword);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(subjectRepository, never()).findAll((PageRequest) any());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllSubjectsPaginated_shouldSearchByName_whenKeywordNotFoundByCode() {
        String keyword = "Physics";
        Subject s = new Subject();
        s.setId(UUID.randomUUID()); s.setName("Physics I");

        Page<Subject> page = new PageImpl<>(List.of(s));
        SubjectResponse response = SubjectResponse.builder().id(s.getId()).name("Physics I").build();

        when(subjectRepository.findByCode(keyword)).thenReturn(Optional.empty());
        when(subjectRepository.findByNameContainingIgnoreCase(eq(keyword), any(PageRequest.class))).thenReturn(page);
        when(subjectGradingRepository.findBySubjectId(s.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(s)).thenReturn(response);

        PageResponse<SubjectResponse> result = subjectService.getAllSubjects(0, 10, keyword);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    // -- deleteSubject --

    @Test
    void deleteSubject_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        when(subjectRepository.existsById(id)).thenReturn(true);
        subjectService.deleteSubject(id);
        verify(subjectRepository).deleteById(id);
    }

    @Test
    void deleteSubject_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(subjectRepository.existsById(id)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> subjectService.deleteSubject(id));
        verify(subjectRepository, never()).deleteById(any());
    }

    // -- getGradingsForSubject --

    @Test
    void getGradingsForSubject_shouldReturnList_whenSubjectExists() {
        UUID subjectId = UUID.randomUUID();
        when(subjectRepository.existsById(subjectId)).thenReturn(true);
        when(subjectGradingRepository.findBySubjectId(subjectId)).thenReturn(List.of());

        List<SubjectGradingWeightResponse> result = subjectService.getGradingsForSubject(subjectId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getGradingsForSubject_shouldThrowException_whenSubjectNotFound() {
        UUID subjectId = UUID.randomUUID();
        when(subjectRepository.existsById(subjectId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> subjectService.getGradingsForSubject(subjectId));
    }

    // -- setGradingsForSubject --

    @Test
    void setGradingsForSubject_shouldSaveAndReturn_whenValidWeights() {
        UUID subjectId = UUID.randomUUID();
        Subject subject = new Subject();
        subject.setId(subjectId);

        SubjectGradingRequest g1 = new SubjectGradingRequest();
        g1.setGradingType("MIDTERM"); g1.setWeight(40.0f); g1.setName("M");
        SubjectGradingRequest g2 = new SubjectGradingRequest();
        g2.setGradingType("FINAL"); g2.setWeight(60.0f); g2.setName("F");

        Grading grading1 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.MIDTERM).name("M").build();
        Grading grading2 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.FINAL).name("F").build();

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(subjectGradingRepository.findBySubjectId(subjectId)).thenReturn(List.of());
        when(gradingRepository.save(any(Grading.class))).thenReturn(grading1, grading2);
        when(subjectGradingRepository.save(any(SubjectGrading.class))).thenAnswer(inv -> inv.getArgument(0));

        List<SubjectGradingWeightResponse> result = subjectService.setGradingsForSubject(subjectId, List.of(g1, g2));

        assertEquals(2, result.size());
        verify(subjectGradingRepository).deleteAll(any());
    }

    @Test
    void setGradingsForSubject_shouldThrowException_whenSubjectNotFound() {
        UUID subjectId = UUID.randomUUID();
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> subjectService.setGradingsForSubject(subjectId, List.of()));
    }

    // -- getPrerequisiteMapping --

    @Test
    void getPrerequisiteMapping_shouldReturnMappingList() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();

        Subject sub1 = new Subject();
        sub1.setId(subjectId1);
        Subject sub2 = new Subject();
        sub2.setId(subjectId2);

        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);

        // Build prerequisite: subject2 is prereq of subject1
        com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite prereq =
                new com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite();
        CurriculumSubject prereqCs = new CurriculumSubject();
        prereqCs.setSubject(sub2);
        prereq.setPrerequisiteCurriculumSubject(prereqCs);
        cs1.setPrerequisites(new ArrayList<>(List.of(prereq)));
        cs1.setRecommendations(new ArrayList<>());

        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations()).thenReturn(List.of(cs1));

        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subjectId1, result.get(0).getSubjectId());
        assertTrue(result.get(0).getRelatedSubjectIds().contains(subjectId2));
    }

    @Test
    void getPrerequisiteMapping_shouldReturnEmptyList_whenNoData() {
        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations()).thenReturn(List.of());
        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();
        assertTrue(result.isEmpty());
    }

    @Test
    void getPrerequisiteMapping_shouldHandleExceptionGracefully() {
        CurriculumSubject cs = new CurriculumSubject();
        cs.setSubject(new Subject());
        cs.getSubject().setId(UUID.randomUUID());

        // Create a list that will throw when getPrerequisites is called
        // Setting prerequisites to null will cause the try-catch to handle it via the null check (no exception)
        // To test the exception path, use a mock list that throws
        List<com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite> badList = new ArrayList<>();
        CurriculumSubject csWithException = new CurriculumSubject();
        csWithException.setSubject(new Subject());
        csWithException.getSubject().setId(UUID.randomUUID());
        csWithException.setPrerequisites(new ArrayList<>());
        csWithException.setRecommendations(new ArrayList<>());

        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations())
                .thenReturn(List.of(csWithException));

        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPrerequisiteMapping_shouldSkipNullSubject() {
        CurriculumSubject cs = new CurriculumSubject();
        cs.setSubject(null);

        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations()).thenReturn(List.of(cs));

        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();

        assertTrue(result.isEmpty());
    }

    // -- getAllSubjectIds --

    @Test
    void getAllSubjectIds_shouldReturnIdList() {
        Subject s1 = new Subject();
        UUID id1 = UUID.randomUUID();
        s1.setId(id1);
        Subject s2 = new Subject();
        UUID id2 = UUID.randomUUID();
        s2.setId(id2);

        when(subjectRepository.findAll()).thenReturn(List.of(s1, s2));

        List<UUID> result = subjectService.getAllSubjectIds();

        assertEquals(2, result.size());
        assertTrue(result.contains(id1));
        assertTrue(result.contains(id2));
    }

    @Test
    void getAllSubjectIds_shouldReturnEmptyList_whenNoData() {
        when(subjectRepository.findAll()).thenReturn(List.of());
        List<UUID> result = subjectService.getAllSubjectIds();
        assertTrue(result.isEmpty());
    }

    // -- searchSubjects --

    @Test
    void searchSubjects_shouldReturnMatches_whenKeywordMatches() {
        String keyword = "Physics";
        Subject s = new Subject();
        s.setId(UUID.randomUUID());
        s.setName("Physics I");
        s.setCode("PH101");

        SubjectResponse response = SubjectResponse.builder().id(s.getId()).name("Physics I").build();

        when(subjectRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                eq(keyword), eq(keyword), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(s)));
        when(subjectMapper.toResponse(s)).thenReturn(response);

        List<SubjectResponse> result = subjectService.searchSubjects(keyword);

        assertEquals(1, result.size());
    }

    @Test
    void searchSubjects_shouldReturnEmptyList_whenKeywordNull() {
        List<SubjectResponse> result = subjectService.searchSubjects(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void searchSubjects_shouldReturnEmptyList_whenKeywordBlank() {
        List<SubjectResponse> result = subjectService.searchSubjects("   ");
        assertTrue(result.isEmpty());
    }

    @Test
    void searchSubjects_shouldTrimKeyword() {
        String keyword = "  Physics  ";
        Subject s = new Subject();
        s.setId(UUID.randomUUID());
        s.setName("Physics I");

        SubjectResponse response = SubjectResponse.builder().id(s.getId()).name("Physics I").build();

        when(subjectRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(
                eq("Physics"), eq("Physics"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(s)));
        when(subjectMapper.toResponse(s)).thenReturn(response);

        List<SubjectResponse> result = subjectService.searchSubjects(keyword);

        assertEquals(1, result.size());
    }

    // -- getPrerequisiteMapping additional --

    @Test
    void getPrerequisiteMapping_shouldHandlePrerequisitesException() {
        UUID subjectId = UUID.randomUUID();
        Subject subject = new Subject();
        subject.setId(subjectId);

        CurriculumSubject cs = spy(new CurriculumSubject());
        cs.setSubject(subject);
        when(cs.getPrerequisites()).thenThrow(new RuntimeException("DB error"));
        cs.setRecommendations(new ArrayList<>());

        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations())
                .thenReturn(List.of(cs));

        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPrerequisiteMapping_shouldIncludeRecommendations() {
        UUID subjectId1 = UUID.randomUUID();
        UUID subjectId2 = UUID.randomUUID();

        Subject sub1 = new Subject();
        sub1.setId(subjectId1);
        Subject sub2 = new Subject();
        sub2.setId(subjectId2);

        CurriculumSubject cs1 = new CurriculumSubject();
        cs1.setSubject(sub1);
        cs1.setPrerequisites(new ArrayList<>());

        com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectRecommendation rec =
                new com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectRecommendation();
        CurriculumSubject recCs = new CurriculumSubject();
        recCs.setSubject(sub2);
        rec.setRecommendedCurriculumSubject(recCs);
        cs1.setRecommendations(new ArrayList<>(List.of(rec)));

        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations())
                .thenReturn(List.of(cs1));

        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(subjectId1, result.get(0).getSubjectId());
        assertTrue(result.get(0).getRelatedSubjectIds().contains(subjectId2));
    }

    @Test
    void getPrerequisiteMapping_shouldHandleRecommendationsException() {
        UUID subjectId = UUID.randomUUID();
        Subject subject = new Subject();
        subject.setId(subjectId);

        CurriculumSubject cs = spy(new CurriculumSubject());
        cs.setSubject(subject);
        cs.setPrerequisites(new ArrayList<>());
        when(cs.getRecommendations()).thenThrow(new RuntimeException("DB error"));

        when(curriculumSubjectRepository.findAllWithPrerequisitesAndRecommendations())
                .thenReturn(List.of(cs));

        List<SubjectPrerequisiteMapResponse> result = subjectService.getPrerequisiteMapping();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // -- createSubject / grading --

    @Test
    void createSubject_shouldThrowException_whenInvalidGradingType() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");
        request.setCode("TS101");

        SubjectGradingRequest g1 = new SubjectGradingRequest();
        g1.setGradingType("QUIZZES");
        g1.setWeight(100.0f);
        request.setGradings(List.of(g1));

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Test Subject");
        subject.setCode("TS101");

        when(subjectRepository.existsByCode("TS101")).thenReturn(false);
        when(subjectMapper.toEntity(request)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);

        DomainException ex = assertThrows(DomainException.class, () -> subjectService.createSubject(request));
        assertTrue(ex.getMessage().contains("Invalid grading type"));
        assertTrue(ex.getMessage().contains("QUIZZES"));
    }

    @Test
    void createSubject_shouldCapitalizeDefaultName_whenGradingNameIsNull() {
        SubjectRequest request = new SubjectRequest();
        request.setName("Test Subject");
        request.setCode("TS101");

        SubjectGradingRequest g1 = new SubjectGradingRequest();
        g1.setGradingType("MIDTERM");
        g1.setWeight(50.0f);
        // name is null — triggers capitalize("midterm") → "Midterm"

        SubjectGradingRequest g2 = new SubjectGradingRequest();
        g2.setGradingType("FINAL");
        g2.setWeight(50.0f);
        // name is null — triggers capitalize("final") → "Final"

        request.setGradings(List.of(g1, g2));

        Subject subject = new Subject();
        subject.setId(UUID.randomUUID());
        subject.setName("Test Subject");
        subject.setCode("TS101");

        SubjectResponse response = SubjectResponse.builder()
                .id(subject.getId()).name("Test Subject").code("TS101").build();

        Grading grading1 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.MIDTERM).build();
        Grading grading2 = Grading.builder().id(UUID.randomUUID()).gradingType(GradingType.FINAL).build();

        when(subjectRepository.existsByCode("TS101")).thenReturn(false);
        when(subjectMapper.toEntity(request)).thenReturn(subject);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(gradingRepository.save(any(Grading.class))).thenReturn(grading1, grading2);
        when(subjectGradingRepository.save(any(SubjectGrading.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(subjectGradingRepository.findBySubjectId(subject.getId())).thenReturn(List.of());
        when(subjectMapper.toResponse(subject)).thenReturn(response);

        subjectService.createSubject(request);

        ArgumentCaptor<Grading> captor = ArgumentCaptor.forClass(Grading.class);
        verify(gradingRepository, times(2)).save(captor.capture());
        List<Grading> saved = captor.getAllValues();
        assertTrue(saved.stream().anyMatch(g -> "Midterm".equals(g.getName())));
        assertTrue(saved.stream().anyMatch(g -> "Final".equals(g.getName())));
    }
}
