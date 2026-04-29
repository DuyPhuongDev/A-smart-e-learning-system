package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.BatchClassLookupRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.*;
import com.hcmut.lms.coursemanagement.application.service.ClassGradingService;
import com.hcmut.lms.coursemanagement.application.mapper.ClassSectionMapper;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.CourseLevel;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.hcmut.lms.coursemanagement.util.SemesterUtil.computeSemKeyFromSemesterCode;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ClassSectionServiceImpl implements ClassSectionService {

  private final ClassSectionRepository classSectionRepository;
  private final SubjectRepository subjectRepository;
  private final SemesterRepository semesterRepository;
  private final ChapterRepository chapterRepository;
  private final ClassSectionMapper classSectionMapper;
  private final UserServiceClient userServiceClient;

  @Override
  public ClassSectionResponse createClassSection(CurrentUserInfo currentUser, ClassSectionRequest request) {
    log.info("Creating class section: {}", request.getSectionName());

    ClassSection classSection = classSectionMapper.toEntity(request);
    StringBuilder classCode = new StringBuilder();

    classSection.setCreatedBy(currentUser.getId());

    // Set subject if provided
    if (request.getSubjectId() != null) {
      Subject subject = subjectRepository.findById(request.getSubjectId())
          .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + request.getSubjectId()));
      classSection.setSubject(subject);
      classSection.setLevel(CourseLevel.valueOf(subject.getCategory().name()));
      classCode.append(subject.getCode());
    }

    // Set semester if provided
    if (request.getSemesterId() != null) {
      Semester semester = semesterRepository.findById(request.getSemesterId())
          .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + request.getSemesterId()));
      classSection.setSemester(semester);
      classCode.append("_").append(semester.getSemesterCode());
    }

    // Set isOfficial: false if subject and semester are provided (official class),
    // true otherwise (teacher's own class)
    classSection.setIsOfficial(request.getSubjectId() == null || request.getSemesterId() == null);

    if (request.getCode() != null) {
      // check code exist
      if (classSectionRepository.existsByCode(request.getCode())) {
        throw new EntityExistsException("Code already exists");
      }
      classSection.setCode(request.getCode());
    } else {
      classSection.setCode(classCode.append("_").append(classSection.getSectionName().toUpperCase()).toString());
    }

    if (request.getThumbnail() != null) {
      classSection.setThumbnailUrl(request.getThumbnail());
    }

    if (request.getIntroVideo() != null) {
      classSection.setIntroVideo(request.getIntroVideo());
    }

    classSection.setStatus(ClassStatus.UP_COMING);
    Integer maxStudents = request.getMaxStudents() != null ? request.getMaxStudents() : -1;
    // set default current student is 0
    classSection.setCurrentStudents(0);
    classSection.setMaxStudents(maxStudents);

    ClassSection savedClassSection = classSectionRepository.save(classSection);

    log.info("Class section created successfully with id: {}", savedClassSection.getId());
    return enrichWithTeacherName(classSectionMapper.toResponseDTO(savedClassSection));
  }

    @Override
    public ClassSectionResponse updateClassSection(UUID id, ClassSectionRequest request) {
        log.info("Updating class section with id: {}", id);

    ClassSection classSection = classSectionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));

    // Update basic fields using mapper
    classSectionMapper.updateEntityFromDTO(request, classSection);

    // Update subject if provided
    if (request.getSubjectId() != null) {
      Subject subject = subjectRepository.findById(request.getSubjectId())
          .orElseThrow(() -> new EntityNotFoundException("Subject not found with id: " + request.getSubjectId()));
      classSection.setSubject(subject);
    }

    // Update semester if provided
    if (request.getSemesterId() != null) {
      Semester semester = semesterRepository.findById(request.getSemesterId())
          .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + request.getSemesterId()));
      classSection.setSemester(semester);
    }

    if (request.getThumbnail() != null) {
      classSection.setThumbnailUrl(request.getThumbnail());
    }

    if (request.getIntroVideo() != null) {
      classSection.setIntroVideo(request.getIntroVideo());
    }

    ClassSection updatedClassSection = classSectionRepository.save(classSection);

    log.info("Class section updated successfully with id: {}", id);
    return enrichWithTeacherName(classSectionMapper.toResponseDTO(updatedClassSection));
  }

  @Override
  @Transactional(readOnly = true)
  public ClassSectionResponse getClassSectionById(UUID id) {
    log.info("Getting class section with id: {}", id);

    ClassSection classSection = classSectionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));

    return enrichWithTeacherName(classSectionMapper.toResponseDTO(classSection));
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ClassSectionResponse> getAllClassSections(int page, int size, String semester, UUID teacherId) {
    log.info(
        "Getting all class sections with filters - page: {}, size: {}, semester: {}, teacherId: {}", page, size,
        semester, teacherId);

    if (semester != null && semester.isBlank()) semester = null;
    Page<ClassSection> classSectionPage = classSectionRepository.findByFilters(
        semester, teacherId,
        PageRequest.of(page, size));

    Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO)
        .map(this::enrichWithTeacherName);
    return PageResponse.fromPage(responsePage);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<ClassSectionResponse> getClassSectionsByTeacherId(
      UUID teacherId, int page, int size,
      String semester) {
    log.info(
        "Getting class sections by teacher id: {} with pagination and filters - page: {}, size: {}, semester: {}",
        teacherId, page, size, semester);

    if (semester.isBlank()) semester = null;
    Page<ClassSection> classSectionPage = classSectionRepository.findByFilters(
        semester, teacherId,
        PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "semester.semesterCode")));

    Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO)
        .map(this::enrichWithTeacherName);
    return PageResponse.fromPage(responsePage);
  }

  @Override
  public void deleteClassSection(UUID id) {
    log.info("Deleting class section with id: {}", id);

    if (!classSectionRepository.existsById(id)) {
      throw new EntityNotFoundException("Class section not found with id: " + id);
    }

    classSectionRepository.deleteById(id);
    log.info("Class section deleted successfully with id: {}", id);
  }

  @Override
  public ClassSectionResponse assignTeacherToClassSection(UUID id, UUID teacherId) {
    log.info("Assign teacher {} to class section {}", teacherId, id);

    ClassSection classSection = classSectionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));

    classSection.setTeacherId(teacherId);

    ClassSection updatedClassSection = classSectionRepository.save(classSection);

    log.info("Teacher assigned successfully to class section {}", id);
    return enrichWithTeacherName(classSectionMapper.toResponseDTO(updatedClassSection));
  }

  @Override
  @Transactional(readOnly = true)
  public CourseMenuResponse getCourseMenu(UUID classSectionId) {
    log.info("Getting course menu for class section id: {}", classSectionId);

    // Fetch ClassSection first
    ClassSection classSection = classSectionRepository.findById(classSectionId)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + classSectionId));

    // Fetch chapters with lectures separately to avoid multiple bag fetch exception
    List<Chapter> chapters = chapterRepository.findByClassSectionIdWithLectures(classSectionId);

    // Map chapters with lectures
    List<CourseMenuChapterDTO> chapterDTOs = chapters.stream()
        .sorted(Comparator.comparing(Chapter::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
        .map(chapter -> {
          List<CourseMenuLectureDTO> lectures = chapter.getLectures()
              .stream()
              .sorted(Comparator.comparing(Lecture::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
              .map(lecture -> CourseMenuLectureDTO.builder()
                  .lectureId(lecture.getId())
                  .title(lecture.getTitle())
                  .order(lecture.getOrderIndex())
                  .type(lecture.getLectureType() != null ? lecture.getLectureType().name().toLowerCase() : null)
                  .build())
              .collect(Collectors.toList());

          return CourseMenuChapterDTO.builder()
              .chapterId(chapter.getId())
              .title(chapter.getTitle())
              .order(chapter.getOrderIndex())
              .lectures(lectures)
              .build();
        })
        .collect(Collectors.toList());

    return CourseMenuResponse.builder()
        .courseId(classSection.getId())
        .title(classSection.getSectionName())
        .chapters(chapterDTOs)
        .build();
  }

  /**
   * Enrich ClassSectionResponse with teacher name from user-management-service
   * Fallback at here
   */

  public ClassSectionResponse enrichWithTeacherName(ClassSectionResponse response) {
    if (response.getTeacherId() == null) {
      return response;
    }

    UserResponse userResponse = userServiceClient.getUserById(response.getTeacherId());

    if (userResponse != null) {
      response.setTeacherName(userResponse.getFullName());
    }

    return response;
  }

  @Override
  public ClassStatusResponse getClassStatus(UUID id) {
    ClassSection classSection = classSectionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));

    return ClassStatusResponse.builder()
        .id(id)
        .exist(true)
        .status(classSection.getStatus().toString())
        .isOfficial(classSection.getIsOfficial())
        .maxStudents(classSection.getMaxStudents())
        .currentStudents(classSection.getCurrentStudents())
        .build();
  }

  @Override
  public void openClass(UUID id) {
    ClassSection originalClass = classSectionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + id));

    originalClass.setStatus(ClassStatus.OPEN);
    classSectionRepository.save(originalClass);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClassSectionResponse> getClassSectionsByIds(BatchClassLookupRequest request) {
    log.info("Getting class sections by batch IDs: {}", request.getClassIds().size());

    List<ClassSection> classSections;

    // If filters are provided, use filter query
    if (request.getSemesterCode() != null || request.getSearchTerm() != null) {
      String semesterCode = request.getSemesterCode() != null && request.getSemesterCode()
          .isBlank() ? null : request.getSemesterCode();
      String searchTerm = request.getSearchTerm() != null && request.getSearchTerm()
          .isBlank() ? null : request.getSearchTerm();

      classSections = classSectionRepository.findByIdInWithFilters(request.getClassIds(), semesterCode, searchTerm);
    } else {
      classSections = classSectionRepository.findByIdIn(request.getClassIds());
    }

    return classSections.stream().map(classSectionMapper::toResponseDTO).map(this::enrichWithTeacherName).toList();
  }

  @Override
  public void incrementCurrentStudents(UUID classId) {
    log.info("Incrementing current students for class: {}", classId);

    ClassSection classSection = classSectionRepository.findById(classId)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + classId));

    int currentStudents = classSection.getCurrentStudents() != null ? classSection.getCurrentStudents() : 0;
    classSection.setCurrentStudents(currentStudents + 1);

    classSectionRepository.save(classSection);
    log.info("Current students incremented to {} for class: {}", classSection.getCurrentStudents(), classId);
  }

  @Override
  public void decrementCurrentStudents(UUID classId) {
    log.info("Decrementing current students for class: {}", classId);

    ClassSection classSection = classSectionRepository.findById(classId)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + classId));

    int currentStudents = classSection.getCurrentStudents() != null ? classSection.getCurrentStudents() : 0;
    if (currentStudents > 0) {
      classSection.setCurrentStudents(currentStudents - 1);
      classSectionRepository.save(classSection);
      log.info("Current students decremented to {} for class: {}", classSection.getCurrentStudents(), classId);
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClassSectionResponse> getClassSectionsBySemesterAndSubject(UUID semesterId, UUID subjectId) {
    log.info("Getting class sections by semester id: {} and subject id: {}", semesterId, subjectId);

    List<ClassSection> classSections = classSectionRepository.findBySemesterIdAndSubjectId(semesterId, subjectId);

    return classSections.stream().map(classSectionMapper::toResponseDTO).map(this::enrichWithTeacherName).toList();
  }

  @Override
  public Integer countNumberLecturesByClassId(UUID classId) {
    log.info("Counting number of mandatory lectures for class: {}", classId);

    ClassSection classSection = classSectionRepository.findById(classId)
        .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + classId));

    return (int) classSection.getChapters()
        .stream()
        .flatMap(chapter -> chapter.getLectures().stream())
        .filter(Lecture::getIsMandatory)
        .count();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClassSectionDatasetResponse> getClassSectionsForDataset(List<UUID> classIds) {
    log.info("Fetching class section dataset metadata for {} classIds", classIds.size());
    List<ClassSection> sections = classSectionRepository.findByIdInWithAcademicYear(classIds);
    return sections.stream().map(this::toDatasetResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ClassSectionDatasetResponse> getClassSectionsBySubjectWindow(
      UUID subjectId, Integer targetSemKey,
      Integer windowSpan) {
    int span = windowSpan != null ? windowSpan : 30;
    int windowLow = targetSemKey - span;
    log.info("Fetching class sections for subject={} in semKey window [{} , {})", subjectId, windowLow, targetSemKey);

    List<ClassSection> sections = classSectionRepository.findBySubjectIdWithAcademicYear(subjectId);
    return sections.stream()
        .map(this::toDatasetResponse)
        .filter(r -> r.getSemKey() != null && r.getSemKey() >= windowLow && r.getSemKey() < targetSemKey)
        .toList();
  }

    @Override
    @Transactional(readOnly = true)
    public ClassSectionReportMetadataResponse getClassSectionReportMetadata(UUID classId) {
        ClassSection classSection = classSectionRepository.findById(classId)
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + classId));

        List<Chapter> chapters = chapterRepository.findByClassSectionIdWithLectures(classId);

        AtomicInteger displayOrder = new AtomicInteger(1);
        List<LectureReportMetadataResponse> lectures = chapters.stream()
                .sorted(Comparator.comparing(Chapter::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder())))
                .flatMap(chapter -> chapter.getLectures().stream()
                        .sorted(Comparator.comparing(Lecture::getOrderIndex, Comparator.nullsLast(Comparator.naturalOrder()))))
                .map(lecture -> LectureReportMetadataResponse.builder()
                        .lectureId(lecture.getId())
                        .title(lecture.getTitle())
                        .order(displayOrder.getAndIncrement())
                        .estimateTimeSpent(lecture.getEstimateTimeSpent())
                        .viewCount(lecture.getViewCount())
                        .build())
                .toList();

        return ClassSectionReportMetadataResponse.builder()
                .classId(classSection.getId())
                .teacherId(classSection.getTeacherId())
                .lectures(lectures)
                .build();
    }


    private ClassSectionDatasetResponse toDatasetResponse(ClassSection cs) {
    String semesterCode = cs.getSemester() != null ? cs.getSemester().getSemesterCode() : null;
    Integer semKey = computeSemKeyFromSemesterCode(semesterCode);

    // Extract curriculumSectionId from subject's first CurriculumSubject (if any)
    UUID curriculumSectionId = null;
    if (cs.getSubject() != null && cs.getSubject().getCurriculumSubjects() != null && !cs.getSubject()
        .getCurriculumSubjects()
        .isEmpty()) {
      curriculumSectionId = cs.getSubject().getCurriculumSubjects().getFirst().getId().getCurriculumSectionId();
    }

    return ClassSectionDatasetResponse.builder()
        .classId(cs.getId())
        .subjectId(cs.getSubject() != null ? cs.getSubject().getId() : null)
        .subjectCode(cs.getSubject() != null ? cs.getSubject().getCode() : null)
        .curriculumSectionId(curriculumSectionId)
        .credits(cs.getSubject() != null ? cs.getSubject().getCredits() : null)
        .semesterId(cs.getSemester() != null ? cs.getSemester().getId() : null)
        .semesterCode(semesterCode)
        .semKey(semKey)
        .build();
  }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getClassIdsByTeacherId(UUID teacherId) {
      log.info("Getting class IDs for teacher: {}", teacherId);
      return classSectionRepository.findIdsByTeacherId(teacherId);
    }
}
