package com.hcmut.lms.learning.service;

import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.entity.lecturenote.LectureNote;

import java.util.List;
import java.util.UUID;

public interface LecturetNoteService {
    LectureNoteResponse createLectureNote(LectureNoteRequest request);

    void deleteLectureNote(UUID id);

    LectureNoteResponse updateLectureNote(UUID id, LectureNoteRequest request);

    List<LectureNoteResponse> getLectureNotesByLectureIdAndStudentId(UUID lectureId, UUID studentId);
}
