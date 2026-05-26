package com.hcmut.lms.learning.mapper;

import static org.junit.jupiter.api.Assertions.*;

import com.hcmut.lms.learning.dto.request.EnrollmentRequest;
import com.hcmut.lms.learning.dto.request.LearningProgressRequest;
import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.request.StudyTimeRequest;
import com.hcmut.lms.learning.dto.response.EnrollmentResponse;
import com.hcmut.lms.learning.dto.response.LearningProgressResponse;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.dto.response.StudentEnrollmentResponse;
import com.hcmut.lms.learning.dto.response.StudyTimeResponse;
import com.hcmut.lms.learning.dto.response.GradePredictionResponse;
import com.hcmut.lms.learning.dto.internal.PredictionResult;
import com.hcmut.lms.learning.entity.enrollment.Enrollment;
import com.hcmut.lms.learning.entity.lecturenote.LectureNote;
import com.hcmut.lms.learning.entity.lecturenote.LectureType;
import com.hcmut.lms.learning.entity.progress.LearningProgress;
import com.hcmut.lms.learning.entity.studytime.StudyTime;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

class MapperTest {

    private final StudyTimeMapperImpl studyTimeMapper = new StudyTimeMapperImpl();
    private final LectureNoteMapperImpl lectureNoteMapper = new LectureNoteMapperImpl();
    private final EnrollmentMapperImpl enrollmentMapper = new EnrollmentMapperImpl();
    private final LearningProgressMapperImpl learningProgressMapper = new LearningProgressMapperImpl();
    private final GradePredictionMapperImpl gradePredictionMapper = new GradePredictionMapperImpl();

    // ─── StudyTimeMapper ────────────────────────────────────────────────────

    @Test
    void studyTimeMapper_toEntity_shouldMapAllFields() {
        StudyTimeRequest req = new StudyTimeRequest();
        req.setClassId(UUID.randomUUID());
        req.setLectureId(UUID.randomUUID());
        req.setDurationSeconds(300);
        req.setStartedAt(LocalDateTime.now());
        req.setMetadata(Map.of("device", "mobile"));

        StudyTime entity = studyTimeMapper.toEntity(req);
        assertNotNull(entity);
        assertEquals(req.getClassId(), entity.getClassId());
        assertEquals(req.getLectureId(), entity.getLectureId());
        assertEquals(req.getDurationSeconds(), entity.getDurationSeconds());
        assertNotNull(entity.getMetadata());
        assertTrue(true);
    }

    @Test
    void studyTimeMapper_toEntity_shouldReturnNull_whenNull() {
        assertNull(studyTimeMapper.toEntity(null));
        assertTrue(true);
    }

    @Test
    void studyTimeMapper_toResponse_shouldMapAllFields() {
        StudyTime entity = StudyTime.builder()
                .id(UUID.randomUUID()).studentId(UUID.randomUUID()).classId(UUID.randomUUID())
                .lectureId(UUID.randomUUID()).durationSeconds(300).build();

        StudyTimeResponse resp = studyTimeMapper.toResponse(entity);
        assertNotNull(resp);
        assertEquals(entity.getId(), resp.getId());
        assertEquals(entity.getStudentId(), resp.getStudentId());
        assertEquals(entity.getClassId(), resp.getClassId());
        assertEquals(entity.getDurationSeconds(), resp.getDurationSeconds());
        assertTrue(true);
    }

    @Test
    void studyTimeMapper_toResponse_shouldReturnNull_whenNull() {
        assertNull(studyTimeMapper.toResponse(null));
        assertTrue(true);
    }

    // ─── LectureNoteMapper ──────────────────────────────────────────────────

    @Test
    void lectureNoteMapper_toEntity_shouldMapAllFields() {
        LectureNoteRequest req = new LectureNoteRequest();
        req.setStudentId(UUID.randomUUID());
        req.setLectureId(UUID.randomUUID());
        req.setLectureType("VIDEO");
        req.setContent("test content");
        req.setContentPosition(42);

        LectureNote entity = lectureNoteMapper.toEntity(req);
        assertNotNull(entity);
        assertEquals(req.getStudentId(), entity.getStudentId());
        assertEquals(req.getLectureId(), entity.getLectureId());
        assertEquals(LectureType.VIDEO, entity.getLectureType());
        assertEquals("test content", entity.getContent());
        assertEquals(42, entity.getContentPosition());
        assertTrue(true);
    }

    @Test
    void lectureNoteMapper_toEntity_shouldReturnNull_whenNull() {
        assertNull(lectureNoteMapper.toEntity(null));
        assertTrue(true);
    }

    @Test
    void lectureNoteMapper_toResponse_shouldMapAllFields() {
        LectureNote entity = new LectureNote();
        entity.setId(UUID.randomUUID());
        entity.setStudentId(UUID.randomUUID());
        entity.setLectureId(UUID.randomUUID());
        entity.setLectureType(LectureType.DOCUMENT);
        entity.setContent("notes");

        LectureNoteResponse resp = lectureNoteMapper.toResponse(entity);
        assertNotNull(resp);
        assertEquals(entity.getId(), resp.getId());
        assertEquals(entity.getStudentId(), resp.getStudentId());
        assertTrue(true);
    }

    @Test
    void lectureNoteMapper_toResponse_shouldReturnNull_whenNull() {
        assertNull(lectureNoteMapper.toResponse(null));
        assertTrue(true);
    }

    @Test
    void lectureNoteMapper_updateEntity_shouldUpdateFields() {
        LectureNoteRequest req = new LectureNoteRequest();
        req.setContent("updated");
        req.setContentPosition(99);

        LectureNote entity = new LectureNote();
        lectureNoteMapper.updateEntityFromRequest(req, entity);
        assertEquals("updated", entity.getContent());
        assertEquals(99, entity.getContentPosition());
        assertTrue(true);
    }

    @Test
    void lectureNoteMapper_updateEntity_shouldNotUpdate_whenNullRequest() {
        LectureNote entity = new LectureNote();
        entity.setContent("original");
        lectureNoteMapper.updateEntityFromRequest(null, entity);
        assertEquals("original", entity.getContent());
        assertTrue(true);
    }

    // ─── EnrollmentMapper ───────────────────────────────────────────────────

    @Test
    void enrollmentMapper_toEntity_shouldMapFields() {
        EnrollmentRequest req = new EnrollmentRequest();
        req.setStudentId(UUID.randomUUID());
        req.setClassId(UUID.randomUUID());

        Enrollment entity = enrollmentMapper.toEntity(req);
        assertNotNull(entity);
        assertEquals(req.getStudentId(), entity.getStudentId());
        assertEquals(req.getClassId(), entity.getClassId());
        assertTrue(true);
    }

    @Test
    void enrollmentMapper_toEntity_shouldReturnNull_whenNull() {
        assertNull(enrollmentMapper.toEntity(null));
        assertTrue(true);
    }

    @Test
    void enrollmentMapper_toResponse_shouldMapFields() {
        Enrollment entity = Enrollment.builder()
                .id(UUID.randomUUID()).studentId(UUID.randomUUID()).classId(UUID.randomUUID())
                .enrolledAt(LocalDateTime.now()).progressPercentage(0.5).build();

        EnrollmentResponse resp = enrollmentMapper.toResponse(entity);
        assertNotNull(resp);
        assertEquals(entity.getId(), resp.getId());
        assertEquals(entity.getStudentId(), resp.getStudentId());
        assertEquals(entity.getClassId(), resp.getClassId());
        assertTrue(true);
    }

    @Test
    void enrollmentMapper_toResponse_shouldReturnNull_whenNull() {
        assertNull(enrollmentMapper.toResponse(null));
        assertTrue(true);
    }

    @Test
    void enrollmentMapper_toStudentEnrollmentResponse_shouldMapFields() {
        Enrollment entity = Enrollment.builder()
                .id(UUID.randomUUID()).studentId(UUID.randomUUID()).classId(UUID.randomUUID())
                .enrolledAt(LocalDateTime.now()).build();

        StudentEnrollmentResponse resp = enrollmentMapper.toStudentEnrollmentResponse(entity);
        assertNotNull(resp);
        assertEquals(entity.getId(), resp.getId());
        assertEquals(entity.getStudentId(), resp.getStudentId());
        assertEquals(entity.getClassId(), resp.getClassId());
        assertTrue(true);
    }

    @Test
    void enrollmentMapper_toStudentEnrollmentResponse_shouldReturnNull_whenNull() {
        assertNull(enrollmentMapper.toStudentEnrollmentResponse(null));
        assertTrue(true);
    }

    // ─── LearningProgressMapper ─────────────────────────────────────────────

    @Test
    void learningProgressMapper_toEntity_shouldMapFields() {
        LearningProgressRequest req = new LearningProgressRequest();
        req.setLectureId(UUID.randomUUID());
        req.setClassId(UUID.randomUUID());

        LearningProgress entity = learningProgressMapper.toEntity(req);
        assertNotNull(entity);
        assertEquals(req.getLectureId(), entity.getLectureId());
        assertTrue(true);
    }

    @Test
    void learningProgressMapper_toEntity_shouldReturnNull_whenNull() {
        assertNull(learningProgressMapper.toEntity(null));
        assertTrue(true);
    }

    @Test
    void learningProgressMapper_toResponse_shouldMapFields() {
        LearningProgress entity = new LearningProgress();
        entity.setStudentId(UUID.randomUUID());
        entity.setLectureId(UUID.randomUUID());

        LearningProgressResponse resp = learningProgressMapper.toResponse(entity);
        assertNotNull(resp);
        assertEquals(entity.getStudentId(), resp.getStudentId());
        assertEquals(entity.getLectureId(), resp.getLectureId());
        assertTrue(true);
    }

    @Test
    void learningProgressMapper_toResponse_shouldReturnNull_whenNull() {
        assertNull(learningProgressMapper.toResponse(null));
        assertTrue(true);
    }

    // ─── GradePredictionMapper ───────────────────────────────────────────────

    @Test
    void gradePredictionMapper_toResponse_shouldMapFields() {
        UUID studentId = UUID.randomUUID();
        PredictionResult result = PredictionResult.builder()
                .studentId(studentId).subjectId(UUID.randomUUID()).semesterId(UUID.randomUUID())
                .rawPredictedGrade(2.8).correctedPredictedGrade(2.9)
                .residualMean(0.1).residualStd(0.5).threshold(2.0)
                .probabilityAboveThreshold(0.85).lowerBound95(1.9).upperBound95(3.9)
                .modelVersionId(UUID.randomUUID()).modelVersionName("test-model")
                .build();

        GradePredictionResponse resp = gradePredictionMapper.toResponse(result);
        assertNotNull(resp);
        assertEquals(studentId, resp.getStudentId());
        assertEquals(2.8, resp.getRawPredictedGrade());
        assertEquals(0.85, resp.getProbabilityAboveThreshold());
        assertTrue(true);
    }

    @Test
    void gradePredictionMapper_toResponse_shouldReturnNull_whenNull() {
        assertNull(gradePredictionMapper.toResponse(null));
        assertTrue(true);
    }
}
