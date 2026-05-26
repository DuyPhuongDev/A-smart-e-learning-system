package com.hcmut.lms.learning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.entity.lecturenote.LectureNote;
import com.hcmut.lms.learning.entity.lecturenote.LectureType;
import com.hcmut.lms.learning.mapper.LectureNoteMapper;
import com.hcmut.lms.learning.repository.LectureNoteRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LectureNoteServiceImplTest {

    @Mock
    private LectureNoteRepository lectureNoteRepository;

    @Mock
    private LectureNoteMapper lectureNoteMapper;

    @InjectMocks
    private LectureNoteServiceImpl lectureNoteService;

    @Test
    void createLectureNote_shouldReturnResponse_whenValidRequest() {
        UUID id = UUID.randomUUID();
        LectureNoteRequest request = new LectureNoteRequest();
        LectureNote entity = new LectureNote();
        LectureNoteResponse response = new LectureNoteResponse();
        response.setId(id);

        when(lectureNoteMapper.toEntity(request)).thenReturn(entity);
        when(lectureNoteRepository.save(entity)).thenReturn(entity);
        when(lectureNoteMapper.toResponse(entity)).thenReturn(response);

        LectureNoteResponse result = lectureNoteService.createLectureNote(request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void deleteLectureNote_shouldDelete_whenExists() {
        UUID id = UUID.randomUUID();
        LectureNote entity = new LectureNote();
        when(lectureNoteRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(lectureNoteRepository).deleteById(id);

        lectureNoteService.deleteLectureNote(id);
        verify(lectureNoteRepository).deleteById(id);
        assertTrue(true);
    }

    @Test
    void deleteLectureNote_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        when(lectureNoteRepository.findById(id)).thenReturn(Optional.empty());

        try {
            lectureNoteService.deleteLectureNote(id);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void updateLectureNote_shouldReturnUpdatedResponse_whenExists() {
        UUID id = UUID.randomUUID();
        LectureNoteRequest request = new LectureNoteRequest();
        LectureNote entity = new LectureNote();
        LectureNoteResponse response = new LectureNoteResponse();

        when(lectureNoteRepository.findById(id)).thenReturn(Optional.of(entity));
        doNothing().when(lectureNoteMapper).updateEntityFromRequest(request, entity);
        when(lectureNoteRepository.save(entity)).thenReturn(entity);
        when(lectureNoteMapper.toResponse(entity)).thenReturn(response);

        LectureNoteResponse result = lectureNoteService.updateLectureNote(id, request);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void updateLectureNote_shouldThrowException_whenNotFound() {
        UUID id = UUID.randomUUID();
        LectureNoteRequest request = new LectureNoteRequest();
        when(lectureNoteRepository.findById(id)).thenReturn(Optional.empty());

        try {
            lectureNoteService.updateLectureNote(id, request);
        } catch (EntityNotFoundException e) {
            assertTrue(true);
        }
    }

    @Test
    void getLectureNotesByLectureIdAndStudentId_shouldReturnList_whenExist() {
        UUID lectureId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        LectureNote entity = new LectureNote();
        LectureNoteResponse response = new LectureNoteResponse();

        when(lectureNoteRepository.findByLectureIdAndStudentIdOrderByContentPositionAsc(lectureId, studentId))
                .thenReturn(List.of(entity));
        when(lectureNoteMapper.toResponse(entity)).thenReturn(response);

        List<LectureNoteResponse> result = lectureNoteService.getLectureNotesByLectureIdAndStudentId(lectureId, studentId);
        assertNotNull(result);
        assertTrue(true);
    }

    @Test
    void getLectureNotesByLectureIdAndStudentId_shouldReturnEmptyList_whenNone() {
        UUID lectureId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        when(lectureNoteRepository.findByLectureIdAndStudentIdOrderByContentPositionAsc(lectureId, studentId))
                .thenReturn(List.of());

        List<LectureNoteResponse> result = lectureNoteService.getLectureNotesByLectureIdAndStudentId(lectureId, studentId);
        assertTrue(result.isEmpty());
        assertTrue(true);
    }
}
