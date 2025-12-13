package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.ApiResponse;
import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.coursemanagement.application.dto.request.ClassSectionRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseMenuChapterDTO;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseMenuLectureDTO;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseMenuResponse;
import com.hcmut.lms.coursemanagement.application.mapper.ClassSectionMapper;
import com.hcmut.lms.coursemanagement.application.service.ClassSectionService;
import com.hcmut.lms.coursemanagement.application.service.FileService;
import com.hcmut.lms.coursemanagement.client.UserManagementClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


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
    private final UserManagementClient userManagementClient;
    private final FileService fileService;
    
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
            classCode.append(subject.getCode());
        }

        // Set semester if provided
        if (request.getSemesterId() != null) {
            Semester semester = semesterRepository.findById(request.getSemesterId())
                    .orElseThrow(() -> new EntityNotFoundException("Semester not found with id: " + request.getSemesterId()));
            classSection.setSemester(semester);
            classCode.append("_").append(semester.getSemesterCode());
        }

        // Set isOfficial: false if subject and semester are provided (official class), true otherwise (teacher's own class)
        if (request.getSubjectId() != null && request.getSemesterId() != null) {
            classSection.setIsOfficial(false);
        } else {
            classSection.setIsOfficial(true);
        }

        if(request.getCode()!=null){
            classSection.setCode(request.getCode());
        }else{
            classSection.setCode(classCode.append("_").append(classSection.getSectionName().toUpperCase()).toString());
        }

        if(request.getThumbnail()!=null){
           classSection.setThumbnailUrl(request.getThumbnail());
        }

        if(request.getIntroVideo()!=null){
            classSection.setIntroVideo(request.getIntroVideo());
        }

        classSection.setStatus(ClassStatus.UP_COMING);
        
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

        if(request.getThumbnail()!=null){
            classSection.setThumbnailUrl(request.getThumbnail());
        }

        if(request.getIntroVideo()!=null){
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
    public List<ClassSectionResponse> getAllClassSections() {
        log.info("Getting all class sections");
        
        return classSectionRepository.findAll().stream()
                .map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getAllClassSections(int page, int size) {
        log.info("Getting all class sections with pagination - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findAll(pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getClassSectionsBySubjectId(UUID subjectId) {
        log.info("Getting class sections by subject id: {}", subjectId);
        
        return classSectionRepository.findBySubjectId(subjectId).stream()
                .map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getClassSectionsBySubjectId(UUID subjectId, int page, int size) {
        log.info("Getting class sections by subject id: {} with pagination - page: {}, size: {}", subjectId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findBySubjectId(subjectId, pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getClassSectionsBySemesterId(UUID semesterId) {
        log.info("Getting class sections by semester id: {}", semesterId);
        
        return classSectionRepository.findBySemesterId(semesterId).stream()
                .map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getClassSectionsBySemesterId(UUID semesterId, int page, int size) {
        log.info("Getting class sections by semester id: {} with pagination - page: {}, size: {}", semesterId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findBySemesterId(semesterId, pageable);
        
        Page<ClassSectionResponse> responsePage = classSectionPage.map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName);
        return PageResponse.fromPage(responsePage);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId) {
        log.info("Getting class sections by teacher id: {}", teacherId);
        
        return classSectionRepository.findByTeacherId(teacherId).stream()
                .map(classSectionMapper::toResponseDTO)
                .map(this::enrichWithTeacherName)
                .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClassSectionResponse> getClassSectionsByTeacherId(UUID teacherId, int page, int size) {
        log.info("Getting class sections by teacher id: {} with pagination - page: {}, size: {}", teacherId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ClassSection> classSectionPage = classSectionRepository.findByTeacherId(teacherId, pageable);
        
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
                    List<CourseMenuLectureDTO> lectures = chapter.getLectures().stream()
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
     */
    private ClassSectionResponse enrichWithTeacherName(ClassSectionResponse response) {
        if (response.getTeacherId() == null) {
            return response;
        }
        
        try {
            ApiResponse<UserResponse> apiResponse = userManagementClient.getUserById(response.getTeacherId());
            if (apiResponse != null && apiResponse.getData() != null) {
                UserResponse userResponse = apiResponse.getData();
                String teacherName = userResponse.getFullName();
                response.setTeacherName(teacherName);
            }
        } catch (Exception e) {
            log.warn("Failed to fetch teacher name for teacherId: {}. Error: {}", response.getTeacherId(), e.getMessage());
            // Continue without teacher name if service call fails
        }
        
        return response;
    }
}

