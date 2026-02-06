package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionImportData;
import com.hcmut.lms.coursemanagement.application.dto.response.ImportResultResponse;
import com.hcmut.lms.coursemanagement.application.service.ImportService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportServiceImpl implements ImportService {

    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final ClassSectionRepository classSectionRepository;

    @Override
    @Transactional
    public ImportResultResponse importSubjectsFromExcel(MultipartFile file) {
        log.info("Importing class sections from Excel file: {}", file.getOriginalFilename());

        // Parse Excel file - each row is a class section
        List<ClassSectionImportData> importDataList = ExcelUtil.parseClassSectionsFromExcel(file);

        List<ImportResultResponse.ValidationError> errors = new ArrayList<>();
        List<ClassSection> validClassSections = new ArrayList<>();

        // Cache for subjects and semesters to avoid repeated DB lookups
        Map<String, Subject> subjectCache = new HashMap<>();
        Map<String, Semester> semesterCache = new HashMap<>();

        // Track class codes within file to detect duplicates
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

            if (data.getSemesterCode() == null || data.getSemesterCode().isEmpty()) {
                errors.add(ImportResultResponse.ValidationError.builder()
                        .row(data.getRowNumber())
                        .field("Semester Code")
                        .message("Thiếu mã học kỳ")
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

            // Get or create subject
            Subject subject = subjectCache.get(data.getSubjectCode());
            if (subject == null) {
                Optional<Subject> existingSubject = subjectRepository.findByCode(data.getSubjectCode());
                if (existingSubject.isPresent()) {
                    subject = existingSubject.get();
                } else {
                    // Create new subject if it doesn't exist
                    subject = Subject.builder()
                            .code(data.getSubjectCode())
                            .name(data.getSubjectName() != null ? data.getSubjectName() : data.getSubjectCode())
                            .credits(data.getCredits())
                            .build();
                    subject = subjectRepository.save(subject);
                    log.info("Created new subject: {}", data.getSubjectCode());
                }
                subjectCache.put(data.getSubjectCode(), subject);
            }

            // Get semester
            Semester semester = semesterCache.get(data.getSemesterCode());
            if (semester == null) {
                Optional<Semester> existingSemester = semesterRepository.findBySemesterCode(data.getSemesterCode());
                if (existingSemester.isEmpty()) {
                    errors.add(ImportResultResponse.ValidationError.builder()
                            .row(data.getRowNumber())
                            .field("Semester Code")
                            .message("Học kỳ không tồn tại: " + data.getSemesterCode())
                            .build());
                    continue;
                }
                semester = existingSemester.get();
                semesterCache.put(data.getSemesterCode(), semester);
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

            // Parse teacherId
            UUID teacherId = null;
            if (data.getTeacherId() != null && !data.getTeacherId().isEmpty()) {
                try {
                    teacherId = UUID.fromString(data.getTeacherId());
                } catch (IllegalArgumentException e) {
                    // Invalid UUID format, skip teacher assignment
                    log.warn("Invalid teacher ID format at row {}: {}", data.getRowNumber(), data.getTeacherId());
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
                    .currentStudents(data.getCurrentStudents() != null ? data.getCurrentStudents() : 0)
                    .teacherId(teacherId)
                    .isOfficial(true)
                    .createdBy(UUID.randomUUID()) // TODO: Get from security context
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
