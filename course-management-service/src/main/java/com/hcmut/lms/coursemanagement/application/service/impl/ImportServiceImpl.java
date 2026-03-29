package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionImportData;
import com.hcmut.lms.coursemanagement.application.dto.response.ImportResultResponse;
import com.hcmut.lms.coursemanagement.application.service.ImportService;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassStatus;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import com.hcmut.lms.coursemanagement.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportServiceImpl implements ImportService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final ClassSectionRepository classSectionRepository;
    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public ImportResultResponse importClassSectionsFromExcel(MultipartFile file, UUID semesterId, UUID currentUserId) {
        log.info("Importing class sections from Excel file: {}", file.getOriginalFilename());

        // 1. Validate semester exists
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Học kỳ không tồn tại với ID: " + semesterId));

        // 2. Parse Excel file
        List<ClassSectionImportData> importDataList = ExcelUtil.parseClassSectionsFromExcel(file);

        List<ImportResultResponse.ValidationError> errors = new ArrayList<>();
        List<ClassSection> validClassSections = new ArrayList<>();

        // 3. Cache subjects to avoid repeated DB lookups
        Map<String, Subject> subjectCache = new HashMap<>();

        // 4. Fetch all teachers once and build a lookup map by teacher code
        Map<String, UUID> teacherCodeToIdMap = new HashMap<>();
        try {
            List<UserResponse> teachers = userServiceClient.getAllTeachers();
            for (UserResponse teacher : teachers) {
                if (teacher.getTeacherCode() != null && !teacher.getTeacherCode().isEmpty()) {
                    teacherCodeToIdMap.put(teacher.getTeacherCode().toUpperCase(), teacher.getId());
                }
            }
        } catch (Exception e) {
            log.warn("Could not fetch teachers list: {}", e.getMessage());
        }

        // 5. Track class codes within file to detect duplicates
        Set<String> classCodesInFile = new HashSet<>();

        for (ClassSectionImportData data : importDataList) {
            // Validate required fields
            if (data.getClassCode() == null || data.getClassCode().isEmpty()) {
                errors.add(ImportResultResponse.ValidationError.builder()
                        .row(data.getRowNumber())
                        .field("Class Code")
                        .message("Thiếu mã lớp")
                        .build());
                continue;
            }

            if (data.getSubjectCode() == null || data.getSubjectCode().isEmpty()) {
                errors.add(ImportResultResponse.ValidationError.builder()
                        .row(data.getRowNumber())
                        .field("Subject Code")
                        .message("Thiếu mã môn học")
                        .build());
                continue;
            }

            // Check duplicate class code in file
            if (classCodesInFile.contains(data.getClassCode().toUpperCase())) {
                errors.add(ImportResultResponse.ValidationError.builder()
                        .row(data.getRowNumber())
                        .field("Class Code")
                        .message("Mã lớp trùng lặp trong file: " + data.getClassCode())
                        .build());
                continue;
            }
            classCodesInFile.add(data.getClassCode().toUpperCase());

            // Check if class code already exists in database
            if (classSectionRepository.existsByCode(data.getClassCode())) {
                errors.add(ImportResultResponse.ValidationError.builder()
                        .row(data.getRowNumber())
                        .field("Class Code")
                        .message("Mã lớp đã tồn tại: " + data.getClassCode())
                        .build());
                continue;
            }

            // Get subject - must exist, do NOT auto-create
            Subject subject = subjectCache.get(data.getSubjectCode());
            if (subject == null) {
                Optional<Subject> existingSubject = subjectRepository.findByCode(data.getSubjectCode());
                if (existingSubject.isPresent()) {
                    subject = existingSubject.get();
                    subjectCache.put(data.getSubjectCode(), subject);
                } else {
                    errors.add(ImportResultResponse.ValidationError.builder()
                            .row(data.getRowNumber())
                            .field("Subject Code")
                            .message("Môn học không tồn tại: " + data.getSubjectCode())
                            .build());
                    continue;
                }
            }

            // Parse status
            ClassStatus status = ClassStatus.UP_COMING;
            if (data.getStatus() != null && !data.getStatus().isEmpty()) {
                try {
                    status = ClassStatus.valueOf(data.getStatus().toUpperCase().replace(" ", "_"));
                } catch (IllegalArgumentException e) {
                    // Keep default status
                }
            }

            // Resolve teacher by teacher code
            UUID teacherId = null;
            if (data.getTeacherCode() != null && !data.getTeacherCode().isEmpty()) {
                UUID resolvedId = teacherCodeToIdMap.get(data.getTeacherCode().toUpperCase());
                if (resolvedId != null) {
                    teacherId = resolvedId;
                } else {
                    errors.add(ImportResultResponse.ValidationError.builder()
                            .row(data.getRowNumber())
                            .field("Teacher Code")
                            .message("Mã giảng viên không tồn tại: " + data.getTeacherCode())
                            .build());
                    continue;
                }
            }

            // Create class section
            ClassSection classSection = ClassSection.builder()
                    .code(data.getClassCode())
                    .sectionName(data.getSectionName())
                    .subject(subject)
                    .semester(semester)
                    .status(status)
                    .maxStudents(data.getMaxStudents() != null ? data.getMaxStudents() : 50)
                    .currentStudents(0)
                    .teacherId(teacherId)
                    .isOfficial(true)
                    .createdBy(currentUserId)
                    .build();

            validClassSections.add(classSection);
        }

        // Save all valid class sections
        if (!validClassSections.isEmpty()) {
            classSectionRepository.saveAll(validClassSections);
            log.info("Successfully imported {} class sections", validClassSections.size());
        }

        return ImportResultResponse.builder()
                .successCount(validClassSections.size())
                .failedCount(errors.size())
                .errors(errors.isEmpty() ? null : errors)
                .build();
    }
}
