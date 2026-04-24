package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.GradeHistoryRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.SqlGenerationResponse;
import com.hcmut.lms.coursemanagement.application.service.SqlGenerationService;
import com.hcmut.lms.coursemanagement.client.UserManagementClient;
import com.hcmut.lms.coursemanagement.client.dto.UserResponse;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.semester.Semester;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.domain.entity.subject.Subject;
import com.hcmut.lms.coursemanagement.repository.AcademicYearRepository;
import com.hcmut.lms.coursemanagement.repository.SemesterRepository;
import com.hcmut.lms.coursemanagement.repository.SpecializationRepository;
import com.hcmut.lms.coursemanagement.repository.SubjectRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SqlGenerationServiceImpl implements SqlGenerationService {

  private final SpecializationRepository specializationRepository;
  private final AcademicYearRepository academicYearRepository;
  private final SemesterRepository semesterRepository;
  private final SubjectRepository subjectRepository;
  private final UserManagementClient userManagementClient;

  @Override
  public SqlGenerationResponse generateInitSqlFromGradeHistory(GradeHistoryRequest request) {
    log.info("Generating SQL initialization file for student: {}", request.getStudentInfo().getStudentCode());

    // Step 1: Validate all required data exists (avoid N+1 by batch loading)
    validateRequiredData(request);

    // Step 2: Check if student exists
    UUID userId;
    boolean isNewStudent = false;
    try {
      UserResponse existingStudent = userManagementClient.getStudentByStudentCode(
          request.getStudentInfo().getStudentCode());
      userId = existingStudent.getId();
      log.info("Student exists with ID: {}", userId);
    } catch (Exception e) {
      userId = UUID.randomUUID();
      isNewStudent = true;
      log.info("Student does not exist, will create new student with ID: {}", userId);
    }

    // Step 3: Batch load all required entities to avoid N+1
    Map<String, UUID> subjectCodeToId = loadSubjectIds(request);
    Map<String, UUID> semesterCodeToId = loadSemesterIds(request);
    Map<UUID, UUID> subjectIdToTeacherId = loadTeachersBySubjects(subjectCodeToId.values());

    Specialization specialization = specializationRepository.findByCode(
            request.getStudentInfo().getSpecializationCode())
        .orElseThrow(() -> new IllegalArgumentException(
            "Specialization not found: " + request.getStudentInfo().getSpecializationCode()));

    AcademicYear intakeYear = academicYearRepository.findByYearCode(request.getStudentInfo().getIntakeYearCode())
        .orElseThrow(() -> new IllegalArgumentException(
            "Academic year not found: " + request.getStudentInfo().getIntakeYearCode()));

    // Step 4: Generate SQL
    StringBuilder sqlBuilder = new StringBuilder();
    int totalRecords = 0;

    sqlBuilder.append("-- Generated SQL initialization file\n");
    sqlBuilder.append("-- Generated at: ")
        .append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        .append("\n");
    sqlBuilder.append("-- Student: ").append(request.getStudentInfo().getStudentCode()).append("\n\n");

    if (isNewStudent) {
      UUID roleId = UUID.fromString("e5909d1e-492d-4419-9766-ce69821e28ec");
      sqlBuilder.append("-- Step 1: Create new user and student\n");
      sqlBuilder.append(generateUserSql(userId, roleId, request.getStudentInfo(), specialization.getId()));
      sqlBuilder.append("\n");
      sqlBuilder.append(generateUserCredentialSql(userId, request.getStudentInfo().getEmail()));
      sqlBuilder.append("\n");
      sqlBuilder.append(generateStudentSql(userId, request.getStudentInfo().getStudentCode(), intakeYear.getId()));
      sqlBuilder.append("\n\n");
      totalRecords += 3;
    } else {
      sqlBuilder.append("-- Step 1: Using existing student with ID: ").append(userId).append("\n\n");
    }

    sqlBuilder.append("-- Step 2: Create class sections\n");
    StringBuilder classSectionSqlBuilder = new StringBuilder();
    StringBuilder enrollmentSqlBuilder = new StringBuilder();

    for (GradeHistoryRequest.GradeRecord record : request.getGradeRecords()) {
      UUID classId = UUID.randomUUID();
      UUID enrollmentId = UUID.randomUUID();

      UUID subjectId = subjectCodeToId.get(record.getSubjectCode());
      UUID semesterId = semesterCodeToId.get(record.getSemesterCode());
      UUID teacherId = subjectIdToTeacherId.get(subjectId);

      classSectionSqlBuilder.append(generateClassSectionSql(classId, record, subjectId, semesterId, userId, teacherId));
      classSectionSqlBuilder.append("\n");

      enrollmentSqlBuilder.append(generateEnrollmentSql(enrollmentId, userId, classId, record));
      enrollmentSqlBuilder.append("\n");

      totalRecords += 2;
    }

    sqlBuilder.append(classSectionSqlBuilder);
    sqlBuilder.append("\n");
    sqlBuilder.append("-- Step 3: Create enrollments\n");
    sqlBuilder.append(enrollmentSqlBuilder);
    sqlBuilder.append("\n");

    String fileName = String.format(
        "init_student_%s_%s.sql", request.getStudentInfo().getStudentCode(),
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));

    String sqlContent = sqlBuilder.toString();
    String migrationFileName = "V12__learning_progress_data.sql";
    writeSqlToFile(sqlContent, migrationFileName);
    log.info("SQL generation completed. Total records: {}", totalRecords);

    return SqlGenerationResponse.builder()
        .fileName(fileName)
        .sqlContent(sqlBuilder.toString())
        .message("SQL initialization file generated successfully")
        .totalRecords(totalRecords)
        .build();
  }

  private void validateRequiredData(GradeHistoryRequest request) {
    List<String> errors = new ArrayList<>();

    if (!specializationRepository.existsByCode(request.getStudentInfo().getSpecializationCode())) {
      errors.add("Specialization not found: " + request.getStudentInfo().getSpecializationCode());
    }

    if (academicYearRepository.findByYearCode(request.getStudentInfo().getIntakeYearCode()).isEmpty()) {
      errors.add("Academic year not found: " + request.getStudentInfo().getIntakeYearCode());
    }

    for (GradeHistoryRequest.GradeRecord record : request.getGradeRecords()) {
      if (!subjectRepository.existsByCode(record.getSubjectCode())) {
        errors.add("Subject not found: " + record.getSubjectCode());
      }
      if (semesterRepository.findBySemesterCode(record.getSemesterCode()).isEmpty()) {
        errors.add("Semester not found: " + record.getSemesterCode());
      }
    }

    if (!errors.isEmpty()) {
      throw new IllegalArgumentException("Validation failed: " + String.join(", ", errors));
    }
  }

  private Map<String, UUID> loadSubjectIds(GradeHistoryRequest request) {
    List<String> subjectCodes = request.getGradeRecords()
        .stream()
        .map(GradeHistoryRequest.GradeRecord::getSubjectCode)
        .distinct()
        .toList();

    return subjectRepository.findAll()
        .stream()
        .filter(s -> subjectCodes.contains(s.getCode()))
        .collect(Collectors.toMap(Subject::getCode, Subject::getId));
  }

  private Map<String, UUID> loadSemesterIds(GradeHistoryRequest request) {
    List<String> semesterCodes = request.getGradeRecords()
        .stream()
        .map(GradeHistoryRequest.GradeRecord::getSemesterCode)
        .distinct()
        .toList();

    return semesterRepository.findAll()
        .stream()
        .filter(s -> semesterCodes.contains(s.getSemesterCode()))
        .collect(Collectors.toMap(Semester::getSemesterCode, Semester::getId));
  }

  private Map<UUID, UUID> loadTeachersBySubjects(Collection<UUID> subjectIds) {
    // Load all subjects to get their subject_group_ids
    Map<UUID, UUID> subjectToGroupMap = subjectRepository.findAll()
        .stream()
        .filter(s -> subjectIds.contains(s.getId()))
        .collect(Collectors.toMap(Subject::getId, Subject::getSubjectGroupId));

    // Get unique subject_group_ids
    Set<UUID> subjectGroupIds = new HashSet<>(subjectToGroupMap.values());
    subjectGroupIds.remove(null); // Remove null values if any

    // Load teachers for each subject_group_id
    Map<UUID, UUID> groupToTeacherMap = new HashMap<>();
    for (UUID subjectGroupId : subjectGroupIds) {
      try {
        List<UserResponse> teachers = userManagementClient.getTeachersBySubjectGroupId(subjectGroupId);
        if (!teachers.isEmpty()) {
          groupToTeacherMap.put(subjectGroupId, teachers.getFirst().getId());
        }
      } catch (Exception e) {
        log.warn("No teacher found for subject group: {}", subjectGroupId);
      }
    }

    // Map subject_id to teacher_id
    Map<UUID, UUID> subjectToTeacher = new HashMap<>();
    for (Map.Entry<UUID, UUID> entry : subjectToGroupMap.entrySet()) {
      UUID subjectId = entry.getKey();
      UUID subjectGroupId = entry.getValue();
      if (subjectGroupId != null && groupToTeacherMap.containsKey(subjectGroupId)) {
        subjectToTeacher.put(subjectId, groupToTeacherMap.get(subjectGroupId));
      }
    }
    return subjectToTeacher;
  }

  private String generateUserSql(
      UUID userId, UUID roleId, GradeHistoryRequest.StudentInfo studentInfo, UUID specializationId) {
    return String.format(
        """
            INSERT INTO user_management.users (id, email, avatar_url, first_name, last_name, phone, last_login, specialization_id, role_id, created_at, updated_at)
            VALUES ('%s', '%s', NULL, '%s', '%s', %s, NULL, '%s', '%s', now(), now())
            ON CONFLICT (email) DO NOTHING;""", userId, studentInfo.getEmail(),
        studentInfo.getFirstName() != null ? studentInfo.getFirstName() : "",
        studentInfo.getLastName() != null ? studentInfo.getLastName() : "",
        studentInfo.getPhone() != null ? "'" + studentInfo.getPhone() + "'" : "NULL", specializationId, roleId);
  }

  private String generateUserCredentialSql(UUID userId, String email) {
    UUID credentialId = UUID.randomUUID();
    return String.format(
        """
            INSERT INTO authentication.user_credentials (id, user_id, email, password_hash, is_account_locked, failed_login_attempts, locked_until, last_login_at, created_at, updated_at)
            VALUES ('%s', '%s', '%s', '$2a$12$sUpyBvT89YMX.ibZTh9ZT.33qJq0DxhgVBFKKxL0XBsKhM0Vt1YqC', false, 0, NULL, NULL, now(), now())
            ON CONFLICT (email) DO NOTHING;""", credentialId, userId, email);
  }

  private static final UUID DEFAULT_DEPARTMENT_ID = UUID.fromString("f6f1f3bd-cb99-4ec2-8a45-16d59c2c1371");

  private String generateStudentSql(UUID userId, String studentCode, UUID intakeYearId) {
    return String.format(
        """
            INSERT INTO user_management.students (user_id, student_code, intake_year_id, department_id)
            VALUES ('%s', '%s', '%s', '%s')
            ON CONFLICT (student_code) DO NOTHING;""", userId, studentCode, intakeYearId, DEFAULT_DEPARTMENT_ID);
  }

  private String generateClassSectionSql(
      UUID classId, GradeHistoryRequest.GradeRecord record, UUID subjectId,
      UUID semesterId, UUID createdBy, UUID teacherId) {
    return String.format(
        "INSERT INTO course_management.class_sections (id, section_name, status, is_official, subject_id, " +
            "semester_id, created_by, teacher_id, code, max_student, current_student, created_at, updated_at)\n" +
            "VALUES ('%s', '%s', 'CLOSED', true, '%s', '%s', '%s', %s, '%s', 50, 1, now(), now());",
        classId, record.getSubjectCode().replace("'", "''"), subjectId, semesterId, createdBy,
        teacherId != null ? "'" + teacherId + "'" : "NULL", record.getSubjectCode() + "_" + record.getSemesterCode());
  }

  private String generateEnrollmentSql(
      UUID enrollmentId, UUID studentId, UUID classId,
      GradeHistoryRequest.GradeRecord record) {
    return String.format(
        "INSERT INTO learning.enrollments (id, student_id, class_id, enrolled_at, final_grade, is_passed, " +
            "progress_percentage, created_at, updated_at)\n" + "VALUES ('%s', '%s', '%s', now(), %.2f, %b, 100.0, " +
            "now" + "(), now());",
        enrollmentId, studentId, classId, record.getGradeNumeric(),
        record.getIsPassed() // This maps to the %b placeholder
                        );
  }

  private void writeSqlToFile(String content, String fileName) {
    try {
      // Path to the target directory relative to the project root
      Path path = Paths.get("course-management-service", "src", "main", "resources", "db", "migration", fileName);

      // Create directories if they don't exist
      Files.createDirectories(path.getParent());

      // Write the file (CREATE if not exists, TRUNCATE if it does)
      Files.writeString(
          path, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
          StandardOpenOption.TRUNCATE_EXISTING);

      log.info("Successfully wrote migration file to: {}", path.toAbsolutePath());
    } catch (IOException e) {
      log.error("Failed to write SQL migration file", e);
      throw new RuntimeException("Could not save migration file: " + e.getMessage());
    }
  }
}
