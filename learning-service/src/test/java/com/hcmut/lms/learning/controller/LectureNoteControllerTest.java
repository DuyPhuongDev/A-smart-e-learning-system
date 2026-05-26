package com.hcmut.lms.learning.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.service.LecturetNoteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class LectureNoteControllerTest {

    @Mock
    private LecturetNoteService lecturetNoteService;

    @InjectMocks
    private LectureNoteController controller;

    private final UUID lectureId = UUID.randomUUID();
    private final UUID noteId = UUID.randomUUID();
    private final UUID studentId = UUID.randomUUID();
    private final CurrentUserInfo currentUser = CurrentUserInfo.builder()
            .id(studentId).email("test@hcmut.edu.vn").role("STUDENT").build();

    @Test
    void getLectureNotes_shouldReturnList() {
        when(lecturetNoteService.getLectureNotesByLectureIdAndStudentId(lectureId, studentId))
                .thenReturn(List.of(new LectureNoteResponse()));

        ResponseEntity<List<LectureNoteResponse>> result = controller.getLectureNotesByLectureIdOfCurrentStudent(currentUser, lectureId);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(true);
    }

    @Test
    void createLectureNote_shouldReturnCreated() {
        LectureNoteRequest request = new LectureNoteRequest();
        request.setStudentId(studentId);
        request.setLectureId(lectureId);
        request.setContent("test content");

        when(lecturetNoteService.createLectureNote(any())).thenReturn(new LectureNoteResponse());

        ResponseEntity<LectureNoteResponse> result = controller.createLectureNote(request);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void updateLectureNote_shouldReturnOk() {
        LectureNoteRequest request = new LectureNoteRequest();
        when(lecturetNoteService.updateLectureNote(noteId, request)).thenReturn(new LectureNoteResponse());

        ResponseEntity<LectureNoteResponse> result = controller.updateLectureNote(noteId, request);
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(true);
    }

    @Test
    void deleteLectureNote_shouldReturnNoContent() {
        doNothing().when(lecturetNoteService).deleteLectureNote(noteId);

        ResponseEntity<LectureNoteResponse> result = controller.deleteLectureNote(noteId);
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(true);
    }
}
