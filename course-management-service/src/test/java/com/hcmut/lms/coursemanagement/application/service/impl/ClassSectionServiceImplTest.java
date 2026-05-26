package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.EnsureClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.*;
import com.hcmut.lms.coursemanagement.application.mapper.ClassSectionMapper;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubject;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.CurriculumSubjectId;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectCategory;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
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
class ClassSectionServiceImplTest {

    @Mock
    private ClassSectionRepository classSectionRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private SemesterRepository semesterRepository;

    @Mock
    private ChapterRepository chapterRepository;

    @Mock
    private ClassSectionMapper classSectionMapper;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private ClassSectionServiceImpl classSectionService;

    private UUID classId;
    private UUID subjectId;
    private UUID semesterId;
    private UUID teacherId;
    private UUID userId;
    private ClassSection classSection;
    private ClassSectionResponse classSectionResponse;
    private CurrentUserInfo currentUser;

    @BeforeEach
    void setUp() {
        classId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        semesterId = UUID.randomUUID();
        teacherId = UUID.randomUUID();
        userId = UUID.randomUUID();

        currentUser = CurrentUserInfo.builder()
                .id(userId)
                .email("teacher@test.com")
                .role("TEACHER")
                .build();

        classSection = new ClassSection();
        classSection.setId(classId);
        classSection.setSectionName("Test Class");
        classSection.setStatus(ClassStatus.UP_COMING);
        classSection.setIsOfficial(false);
        classSection.setCode("TEST_CLASS");
        classSection.setMaxStudents(50);
        classSection.setCurrentStudents(0);
        classSection.setTeacherId(teacherId);
        classSection.setChapters(new ArrayList<>());

        classSectionResponse = ClassSectionResponse.builder()
                .id(classId)
                .sectionName("Test Class")
                .status("UP_COMING")
                .teacherId(teacherId)
                .code("TEST_CLASS")
                .maxStudents(50)
                .currentStudents(0)
                .build();
    }

    // -- createClassSection --

    @Test
    void createClassSection_shouldReturnResponse_whenValidWithSubjectAndSemester() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Test Class");
        request.setSubjectId(subjectId);
        request.setSemesterId(semesterId);
        request.setMaxStudents(50);

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCode("SUB101");
        subject.setCategory(SubjectCategory.MAJOR_FOUNDATION);

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");

        ClassSection newSection = new ClassSection();
        newSection.setSectionName("Test Class");

        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(classSectionMapper.toEntity(request)).thenReturn(newSection);
        when(classSectionRepository.save(any(ClassSection.class))).thenReturn(classSection);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.createClassSection(currentUser, request);

        assertNotNull(result);
        assertEquals("Test Class", result.getSectionName());
        verify(classSectionRepository).save(any(ClassSection.class));
    }

    @Test
    void createClassSection_shouldReturnResponse_whenValidWithoutSubjectAndSemester() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Teacher Class");
        request.setMaxStudents(30);

        ClassSection teacherClass = new ClassSection();
        teacherClass.setId(classId);
        teacherClass.setSectionName("Teacher Class");
        teacherClass.setStatus(ClassStatus.UP_COMING);
        teacherClass.setCode("TEACHER_CLASS");
        teacherClass.setMaxStudents(30);
        teacherClass.setCurrentStudents(0);

        ClassSectionResponse teacherResponse = ClassSectionResponse.builder()
                .id(classId)
                .sectionName("Teacher Class")
                .status("UP_COMING")
                .code("TEACHER_CLASS")
                .build();

        ClassSection newTeacherClass = new ClassSection();
        newTeacherClass.setSectionName("Teacher Class");

        when(classSectionMapper.toEntity(request)).thenReturn(newTeacherClass);
        when(classSectionRepository.save(any(ClassSection.class))).thenReturn(teacherClass);
        when(classSectionMapper.toResponseDTO(teacherClass)).thenReturn(teacherResponse);

        ClassSectionResponse result = classSectionService.createClassSection(currentUser, request);

        assertNotNull(result);
    }

    @Test
    void createClassSection_shouldSetCode_whenCodeProvided() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Custom Code Class");
        request.setCode("CUSTOM_CODE");
        request.setMaxStudents(40);

        ClassSection newSection = new ClassSection();
        newSection.setSectionName("Custom Code Class");

        ClassSection savedSection = new ClassSection();
        savedSection.setId(classId);
        savedSection.setSectionName("Custom Code Class");
        savedSection.setCode("CUSTOM_CODE");
        savedSection.setStatus(ClassStatus.UP_COMING);
        savedSection.setMaxStudents(40);
        savedSection.setCurrentStudents(0);

        ClassSectionResponse response = ClassSectionResponse.builder()
                .id(classId)
                .sectionName("Custom Code Class")
                .code("CUSTOM_CODE")
                .build();

        when(classSectionMapper.toEntity(request)).thenReturn(newSection);
        when(classSectionRepository.existsByCode("CUSTOM_CODE")).thenReturn(false);
        when(classSectionRepository.save(any(ClassSection.class))).thenReturn(savedSection);
        when(classSectionMapper.toResponseDTO(savedSection)).thenReturn(response);

        ClassSectionResponse result = classSectionService.createClassSection(currentUser, request);

        assertNotNull(result);
        assertEquals("CUSTOM_CODE", result.getCode());
    }

    @Test
    void createClassSection_shouldSetThumbnailAndIntroVideo_whenProvided() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Media Class");
        request.setMaxStudents(30);
        request.setThumbnail("http://thumb.url");
        request.setIntroVideo("http://video.url");

        ClassSection newSection = new ClassSection();
        newSection.setSectionName("Media Class");

        ClassSection savedSection = new ClassSection();
        savedSection.setId(classId);
        savedSection.setSectionName("Media Class");
        savedSection.setStatus(ClassStatus.UP_COMING);
        savedSection.setMaxStudents(30);
        savedSection.setCurrentStudents(0);
        savedSection.setThumbnailUrl("http://thumb.url");
        savedSection.setIntroVideo("http://video.url");

        ClassSectionResponse response = ClassSectionResponse.builder()
                .id(classId)
                .sectionName("Media Class")
                .build();

        when(classSectionMapper.toEntity(request)).thenReturn(newSection);
        when(classSectionRepository.save(any(ClassSection.class))).thenReturn(savedSection);
        when(classSectionMapper.toResponseDTO(savedSection)).thenReturn(response);

        ClassSectionResponse result = classSectionService.createClassSection(currentUser, request);

        assertNotNull(result);
    }

    // -- updateClassSection additional --

    @Test
    void updateClassSection_shouldThrowException_whenSubjectNotFound() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSubjectId(subjectId);

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.updateClassSection(classId, request));
    }

    @Test
    void updateClassSection_shouldThrowException_whenSemesterNotFound() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSemesterId(semesterId);

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.updateClassSection(classId, request));
    }

    @Test
    void updateClassSection_shouldSetThumbnailAndIntroVideo_whenProvided() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setThumbnail("http://thumb.new");
        request.setIntroVideo("http://video.new");

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.updateClassSection(classId, request);

        assertNotNull(result);
    }

    @Test
    void updateClassSection_shouldSetSubject_whenSubjectIdProvided() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSubjectId(subjectId);

        Subject subject = new Subject();
        subject.setId(subjectId);

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.updateClassSection(classId, request);

        assertNotNull(result);
        assertEquals(subject, classSection.getSubject());
    }

    @Test
    void updateClassSection_shouldSetSemester_whenSemesterIdProvided() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSemesterId(semesterId);

        Semester semester = new Semester();
        semester.setId(semesterId);

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.updateClassSection(classId, request);

        assertNotNull(result);
        assertEquals(semester, classSection.getSemester());
    }

    // -- getClassSectionsForDataset with curriculum subjects --

    @Test
    void getClassSectionsForDataset_shouldIncludeCurriculumSectionId_whenSubjectHasCurriculumSubjects() {
        List<UUID> ids = List.of(classId);

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setCode("SUB101");
        subject.setCredits(3);

        CurriculumSubject curriculumSubject = new CurriculumSubject();
        CurriculumSubjectId csId = new CurriculumSubjectId(UUID.randomUUID(), subjectId, 1);
        curriculumSubject.setId(csId);
        subject.setCurriculumSubjects(new ArrayList<>(List.of(curriculumSubject)));

        classSection.setSubject(subject);
        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");
        classSection.setSemester(semester);

        when(classSectionRepository.findByIdInWithAcademicYear(ids)).thenReturn(List.of(classSection));

        List<ClassSectionDatasetResponse> result = classSectionService.getClassSectionsForDataset(ids);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getCurriculumSectionId());
    }

    @Test
    void createClassSection_shouldThrowException_whenDuplicateCode() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Test Class");
        request.setCode("DUPLICATE");

        when(classSectionMapper.toEntity(request)).thenReturn(new ClassSection());
        when(classSectionRepository.existsByCode("DUPLICATE")).thenReturn(true);

        assertThrows(EntityExistsException.class,
                () -> classSectionService.createClassSection(currentUser, request));
    }

    @Test
    void createClassSection_shouldThrowException_whenSubjectNotFound() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Test Class");
        request.setSubjectId(subjectId);

        when(classSectionMapper.toEntity(request)).thenReturn(new ClassSection());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.createClassSection(currentUser, request));
    }

    @Test
    void createClassSection_shouldThrowException_whenSemesterNotFound() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Test Class");
        request.setSemesterId(semesterId);

        when(classSectionMapper.toEntity(request)).thenReturn(new ClassSection());
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.createClassSection(currentUser, request));
    }

    // -- updateClassSection --

    @Test
    void updateClassSection_shouldReturnUpdatedResponse_whenExists() {
        ClassSectionRequest request = new ClassSectionRequest();
        request.setSectionName("Updated Class");

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        doAnswer(inv -> { classSection.setSectionName("Updated Class"); return null; })
                .when(classSectionMapper).updateEntityFromDTO(eq(request), eq(classSection));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.updateClassSection(classId, request);

        assertNotNull(result);
        verify(classSectionRepository).save(classSection);
    }

    @Test
    void updateClassSection_shouldThrowException_whenNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.updateClassSection(classId, new ClassSectionRequest()));
    }

    // -- getClassSectionById --

    @Test
    void getClassSectionById_shouldReturnResponse_whenExists() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.getClassSectionById(classId);

        assertNotNull(result);
    }

    @Test
    void getClassSectionById_shouldThrowException_whenNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> classSectionService.getClassSectionById(classId));
    }

    // -- getAllClassSections --

    @Test
    @SuppressWarnings("unchecked")
    void getAllClassSections_shouldReturnPageResponse_whenNoFilters() {
        Page<ClassSection> page = new PageImpl<>(List.of(classSection));
        when(classSectionRepository.findByFilters(isNull(), isNull(), any(PageRequest.class))).thenReturn(page);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        PageResponse<ClassSectionResponse> result = classSectionService.getAllClassSections(0, 10, null, null);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAllClassSections_shouldReturnPageResponse_whenWithFilters() {
        Page<ClassSection> page = new PageImpl<>(List.of(classSection));
        when(classSectionRepository.findByFilters(eq("HK221"), eq(teacherId), any(PageRequest.class))).thenReturn(page);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        PageResponse<ClassSectionResponse> result = classSectionService.getAllClassSections(0, 10, "HK221", teacherId);

        assertNotNull(result);
    }

    // -- getClassSectionsByTeacherId --

    @Test
    @SuppressWarnings("unchecked")
    void getClassSectionsByTeacherId_shouldReturnPageResponse() {
        Page<ClassSection> page = new PageImpl<>(List.of(classSection));
        when(classSectionRepository.findByFilters(eq("HK221"), eq(teacherId), any(PageRequest.class))).thenReturn(page);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        PageResponse<ClassSectionResponse> result =
                classSectionService.getClassSectionsByTeacherId(teacherId, 0, 10, "HK221");

        assertNotNull(result);
    }

    // -- deleteClassSection --

    @Test
    void deleteClassSection_shouldDelete_whenExists() {
        when(classSectionRepository.existsById(classId)).thenReturn(true);
        classSectionService.deleteClassSection(classId);
        verify(classSectionRepository).deleteById(classId);
    }

    @Test
    void deleteClassSection_shouldThrowException_whenNotFound() {
        when(classSectionRepository.existsById(classId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> classSectionService.deleteClassSection(classId));
    }

    // -- assignTeacherToClassSection --

    @Test
    void assignTeacherToClassSection_shouldReturnResponse_whenValid() {
        UUID newTeacherId = UUID.randomUUID();
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        ClassSectionResponse result = classSectionService.assignTeacherToClassSection(classId, newTeacherId);

        assertNotNull(result);
        assertEquals(newTeacherId, classSection.getTeacherId());
    }

    @Test
    void assignTeacherToClassSection_shouldThrowException_whenClassNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.assignTeacherToClassSection(classId, teacherId));
    }

    // -- getCourseMenu --

    @Test
    void getCourseMenu_shouldReturnResponse_whenClassExists() {
        Chapter chapter = new Chapter();
        chapter.setId(UUID.randomUUID());
        chapter.setTitle("Chapter 1");
        chapter.setOrderIndex(1);

        VideoLectureForTest lecture = new VideoLectureForTest("Lecture 1", "https://example.com/video.mp4", 300);
        lecture.setOrderIndex(1);
        lecture.setIsMandatory(true);

        chapter.setLectures(new ArrayList<>(List.of(lecture)));

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(chapterRepository.findByClassSectionIdWithLectures(classId)).thenReturn(List.of(chapter));

        CourseMenuResponse result = classSectionService.getCourseMenu(classId);

        assertNotNull(result);
        assertEquals(classId, result.getCourseId());
        assertEquals(1, result.getChapters().size());
        assertEquals(1, result.getChapters().get(0).getLectures().size());
    }

    @Test
    void getCourseMenu_shouldThrowException_whenClassNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> classSectionService.getCourseMenu(classId));
    }

    // -- enrichWithTeacherName --

    @Test
    void enrichWithTeacherName_shouldEnrich_whenTeacherIdPresent() {
        UserResponse userResponse = new UserResponse();
        userResponse.setId(teacherId);
        userResponse.setFirstName("John");
        userResponse.setLastName("Doe");

        when(userServiceClient.getUserById(teacherId)).thenReturn(userResponse);

        ClassSectionResponse result = classSectionService.enrichWithTeacherName(classSectionResponse);

        assertEquals("John Doe", result.getTeacherName());
    }

    @Test
    void enrichWithTeacherName_shouldReturnUnchanged_whenTeacherIdNull() {
        ClassSectionResponse noTeacher = ClassSectionResponse.builder().id(classId).build();

        ClassSectionResponse result = classSectionService.enrichWithTeacherName(noTeacher);

        assertNull(result.getTeacherName());
        verify(userServiceClient, never()).getUserById(any());
    }

    // -- getClassStatus --

    @Test
    void getClassStatus_shouldReturnStatus_whenClassExists() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));

        ClassStatusResponse result = classSectionService.getClassStatus(classId);

        assertNotNull(result);
        assertTrue(result.isExist());
        assertEquals("UP_COMING", result.getStatus());
    }

    @Test
    void getClassStatus_shouldThrowException_whenClassNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> classSectionService.getClassStatus(classId));
    }

    // -- openClass --

    @Test
    void openClass_shouldSetStatusToOpen() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);

        classSectionService.openClass(classId);

        assertEquals(ClassStatus.OPEN, classSection.getStatus());
        verify(classSectionRepository).save(classSection);
    }

    // -- getClassSectionsByIds --

    @Test
    void getClassSectionsByIds_shouldReturnList_whenNoFilters() {
        List<UUID> ids = List.of(classId);
        when(classSectionRepository.findByIdIn(ids)).thenReturn(List.of(classSection));
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        List<ClassSectionResponse> result = classSectionService.getClassSectionsByIds(
                new BatchClassLookupRequest(ids));

        assertEquals(1, result.size());
    }

    @Test
    void getClassSectionsByIds_shouldReturnList_whenWithFilters() {
        List<UUID> ids = List.of(classId);
        BatchClassLookupRequest request = new BatchClassLookupRequest(ids);
        request.setSemesterCode("HK221");
        request.setSearchTerm("Test");

        when(classSectionRepository.findByIdInWithFilters(ids, "HK221", "Test")).thenReturn(List.of(classSection));
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        List<ClassSectionResponse> result = classSectionService.getClassSectionsByIds(request);

        assertEquals(1, result.size());
    }

    // -- incrementCurrentStudents --

    @Test
    void incrementCurrentStudents_shouldIncrement_whenClassExists() {
        classSection.setCurrentStudents(5);
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);

        classSectionService.incrementCurrentStudents(classId);

        assertEquals(6, classSection.getCurrentStudents());
    }

    @Test
    void incrementCurrentStudents_shouldThrowException_whenClassNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.incrementCurrentStudents(classId));
    }

    // -- decrementCurrentStudents --

    @Test
    void decrementCurrentStudents_shouldDecrement_whenClassExists() {
        classSection.setCurrentStudents(5);
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(classSectionRepository.save(classSection)).thenReturn(classSection);

        classSectionService.decrementCurrentStudents(classId);

        assertEquals(4, classSection.getCurrentStudents());
    }

    @Test
    void decrementCurrentStudents_shouldNotGoNegative() {
        classSection.setCurrentStudents(0);
        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));

        classSectionService.decrementCurrentStudents(classId);

        assertEquals(0, classSection.getCurrentStudents());
        verify(classSectionRepository, never()).save(any());
    }

    @Test
    void decrementCurrentStudents_shouldThrowException_whenClassNotFound() {
        when(classSectionRepository.findById(classId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> classSectionService.decrementCurrentStudents(classId));
    }

    // -- getClassSectionsBySemesterAndSubject --

    @Test
    void getClassSectionsBySemesterAndSubject_shouldReturnList() {
        when(classSectionRepository.findBySemesterIdAndSubjectId(semesterId, subjectId))
                .thenReturn(List.of(classSection));
        when(classSectionMapper.toResponseDTO(classSection)).thenReturn(classSectionResponse);

        List<ClassSectionResponse> result =
                classSectionService.getClassSectionsBySemesterAndSubject(semesterId, subjectId);

        assertEquals(1, result.size());
    }

    // -- countNumberLecturesByClassId --

    @Test
    void countNumberLecturesByClassId_shouldReturnCount() {
        Chapter chapter = new Chapter();
        chapter.setId(UUID.randomUUID());

        VideoLectureForTest mandatoryLecture = new VideoLectureForTest("L1", "url", 100);
        mandatoryLecture.setIsMandatory(true);
        VideoLectureForTest optionalLecture = new VideoLectureForTest("L2", "url", 100);
        optionalLecture.setIsMandatory(false);

        chapter.setLectures(new ArrayList<>(List.of(mandatoryLecture, optionalLecture)));
        classSection.setChapters(new ArrayList<>(List.of(chapter)));

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));

        Integer count = classSectionService.countNumberLecturesByClassId(classId);

        assertEquals(1, count);
    }

    // -- getClassSectionsForDataset --

    @Test
    void getClassSectionsForDataset_shouldReturnList() {
        List<UUID> ids = List.of(classId);
        classSection.setSubject(new Subject());
        classSection.getSubject().setId(subjectId);
        classSection.getSubject().setCode("SUB101");
        classSection.getSubject().setCredits(3);
        classSection.getSubject().setCurriculumSubjects(new ArrayList<>());
        classSection.setSemester(new Semester());
        classSection.getSemester().setId(semesterId);
        classSection.getSemester().setSemesterCode("HK221");

        when(classSectionRepository.findByIdInWithAcademicYear(ids)).thenReturn(List.of(classSection));

        List<ClassSectionDatasetResponse> result = classSectionService.getClassSectionsForDataset(ids);

        assertEquals(1, result.size());
        assertEquals(classId, result.get(0).getClassId());
    }

    // -- getClassSectionsBySubjectWindow --

    @Test
    void getClassSectionsBySubjectWindow_shouldReturnFilteredList() {
        Semester sem = new Semester();
        sem.setId(semesterId);
        sem.setSemesterCode("HK221");

        classSection.setSubject(new Subject());
        classSection.getSubject().setId(subjectId);
        classSection.getSubject().setCode("SUB101");
        classSection.getSubject().setCredits(3);
        classSection.getSubject().setCurriculumSubjects(new ArrayList<>());
        classSection.setSemester(sem);

        when(classSectionRepository.findBySubjectIdWithAcademicYear(subjectId))
                .thenReturn(List.of(classSection));

        List<ClassSectionDatasetResponse> result =
                classSectionService.getClassSectionsBySubjectWindow(subjectId, 20221, 30);

        assertNotNull(result);
    }

    // -- getClassSectionReportMetadata --

    @Test
    void getClassSectionReportMetadata_shouldReturnMetadata_whenExists() {
        Chapter chapter = new Chapter();
        chapter.setId(UUID.randomUUID());
        chapter.setTitle("Chapter 1");
        chapter.setOrderIndex(1);

        VideoLectureForTest lecture = new VideoLectureForTest("Lecture 1", "url", 300);
        lecture.setOrderIndex(1);
        lecture.setEstimateTimeSpent(45);
        lecture.setViewCount(100);
        chapter.setLectures(new ArrayList<>(List.of(lecture)));

        when(classSectionRepository.findById(classId)).thenReturn(Optional.of(classSection));
        when(chapterRepository.findByClassSectionIdWithLectures(classId)).thenReturn(List.of(chapter));

        ClassSectionReportMetadataResponse result = classSectionService.getClassSectionReportMetadata(classId);

        assertNotNull(result);
        assertEquals(classId, result.getClassId());
        assertEquals(1, result.getLectures().size());
    }

    // -- getClassIdsByTeacherId --

    @Test
    void getClassIdsByTeacherId_shouldReturnIdList() {
        when(classSectionRepository.findIdsByTeacherId(teacherId)).thenReturn(List.of(classId));

        List<UUID> result = classSectionService.getClassIdsByTeacherId(teacherId);

        assertEquals(1, result.size());
        assertEquals(classId, result.get(0));
    }

    // -- ensureClassSection --

    @Test
    void ensureClassSection_shouldReturnExisting_whenAlreadyExists() {
        EnsureClassSectionRequest request = EnsureClassSectionRequest.builder()
                .subjectId(subjectId)
                .semesterId(semesterId)
                .createdBy(userId)
                .build();

        classSection.setSubject(new Subject());
        classSection.getSubject().setId(subjectId);
        classSection.setSemester(new Semester());
        classSection.getSemester().setId(semesterId);

        when(classSectionRepository.findBySemesterIdAndSubjectId(semesterId, subjectId))
                .thenReturn(List.of(classSection));

        ClassSectionTestDataResponse result = classSectionService.ensureClassSection(request);

        assertNotNull(result);
        assertEquals(classId, result.getId());
        verify(classSectionRepository, never()).save(any());
    }

    @Test
    void ensureClassSection_shouldCreateNew_whenNotExists() {
        EnsureClassSectionRequest request = EnsureClassSectionRequest.builder()
                .subjectId(subjectId)
                .semesterId(semesterId)
                .createdBy(userId)
                .build();

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setName("Test Subject");
        subject.setCode("SUB101");

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");

        when(classSectionRepository.findBySemesterIdAndSubjectId(semesterId, subjectId))
                .thenReturn(List.of());
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(classSectionRepository.save(any(ClassSection.class))).thenAnswer(inv -> {
            ClassSection cs = inv.getArgument(0);
            cs.setId(classId);
            cs.setCode("SUB101_HK221_TEST");
            return cs;
        });

        ClassSectionTestDataResponse result = classSectionService.ensureClassSection(request);

        assertNotNull(result);
        assertEquals(classId, result.getId());
    }

    @Test
    void ensureClassSection_shouldRetryOnDataIntegrityViolation() {
        EnsureClassSectionRequest request = EnsureClassSectionRequest.builder()
                .subjectId(subjectId)
                .semesterId(semesterId)
                .createdBy(userId)
                .build();

        Subject subject = new Subject();
        subject.setId(subjectId);
        subject.setName("Test Subject");
        subject.setCode("SUB101");

        Semester semester = new Semester();
        semester.setId(semesterId);
        semester.setSemesterCode("HK221");

        ClassSection existing = new ClassSection();
        existing.setId(classId);
        existing.setCode("SUB101_HK221_TEST");
        existing.setSubject(subject);
        existing.setSemester(semester);

        when(classSectionRepository.findBySemesterIdAndSubjectId(semesterId, subjectId))
                .thenReturn(List.of())
                .thenReturn(List.of(existing));
        when(subjectRepository.findById(subjectId)).thenReturn(Optional.of(subject));
        when(semesterRepository.findById(semesterId)).thenReturn(Optional.of(semester));
        when(classSectionRepository.save(any(ClassSection.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        ClassSectionTestDataResponse result = classSectionService.ensureClassSection(request);

        assertNotNull(result);
        assertEquals(classId, result.getId());
    }

    // -- helper subclass for creating concrete Lecture in tests --

    @SuppressWarnings("unused")
    private static class VideoLectureForTest extends com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture {
        public VideoLectureForTest(String title, String videoUrl, Integer duration) {
            super(title, videoUrl, duration);
        }
    }
}
