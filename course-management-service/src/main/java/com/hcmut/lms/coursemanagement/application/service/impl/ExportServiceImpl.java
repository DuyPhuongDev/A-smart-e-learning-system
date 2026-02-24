package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.ClassSectionExportData;
import com.hcmut.lms.coursemanagement.application.service.ExportService;
import com.hcmut.lms.coursemanagement.client.UserServiceClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExportServiceImpl implements ExportService {

    private final ClassSectionRepository classSectionRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public byte[] exportSubjectsAndClassesToExcel() {
        log.info("Exporting subjects and class sections to Excel");

        // Fetch all class sections with subject and semester
        List<ClassSection> classSections = classSectionRepository.findAllWithSubjectAndSemester();

        // Map to export data
        List<ClassSectionExportData> exportDataList = new ArrayList<>();

        for (ClassSection classSection : classSections) {
            String teacherName = null;

            // Fetch teacher name if teacherId exists
            if (classSection.getTeacherId() != null) {
                try {
                    UserResponse user = userServiceClient.getUserById(classSection.getTeacherId());
                    if (user != null) {
                        teacherName = user.getFullName();
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch teacher name for teacherId: {}", classSection.getTeacherId());
                }
            }

            ClassSectionExportData exportData = ClassSectionExportData.builder()
                    .subjectCode(classSection.getSubject() != null ? classSection.getSubject().getCode() : null)
                    .subjectName(classSection.getSubject() != null ? classSection.getSubject().getName() : null)
                    .credits(classSection.getSubject() != null ? classSection.getSubject().getCredits() : null)
                    .classCode(classSection.getCode())
                    .sectionName(classSection.getSectionName())
                    .semesterCode(
                            classSection.getSemester() != null ? classSection.getSemester().getSemesterCode() : null)
                    .status(classSection.getStatus() != null ? classSection.getStatus().name() : null)
                    .maxStudents(classSection.getMaxStudents())
                    .currentStudents(classSection.getCurrentStudents())
                    .teacherId(classSection.getTeacherId() != null ? classSection.getTeacherId().toString() : null)
                    .teacherName(teacherName)
                    .build();

            exportDataList.add(exportData);
        }

        log.info("Exporting {} class sections to Excel", exportDataList.size());
        return ExcelUtil.exportSubjectsWithClassSectionsToExcel(exportDataList);
    }
}
