package com.hcmut.lms.learning.service.impl;

import com.hcmut.lms.learning.client.dto.LectureResponse;
import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.entity.lecturenote.LectureNote;
import com.hcmut.lms.learning.mapper.LectureNoteMapper;
import com.hcmut.lms.learning.repository.LectureNoteRepository;
import com.hcmut.lms.learning.service.LecturetNoteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LectureNoteServiceImpl implements LecturetNoteService {

    private final LectureNoteRepository lectureNoteRepository;
    private final LectureNoteMapper lectureNoteMapper;


    @Override
    @Transactional
    public LectureNoteResponse createLectureNote(LectureNoteRequest request) {
        log.info("Creating Lecture Note");
        LectureNote lectureNote = lectureNoteMapper.toEntity(request);
        return lectureNoteMapper.toResponse(lectureNoteRepository.save(lectureNote));
    }

    @Override
    public void deleteLectureNote(UUID id) {
        log.info("Deleting Lecture Note with ID {}", id);
        lectureNoteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lecture Note with ID " + id + " not found"));
        lectureNoteRepository.deleteById(id);
    }

    @Override
    public LectureNoteResponse updateLectureNote(UUID id, LectureNoteRequest request) {
        LectureNote lectureNote = lectureNoteRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Lecture Note with ID " + id + " not found"));
        lectureNoteMapper.updateEntityFromRequest(request, lectureNote);
        return lectureNoteMapper.toResponse(lectureNoteRepository.save(lectureNote));
    }

    @Override
    public List<LectureNoteResponse> getLectureNotesByLectureIdAndStudentId(UUID lectureId, UUID studentId) {
        List<LectureNote> lectureNotes = lectureNoteRepository.findByLectureIdAndStudentIdOrderByContentPositionAsc(lectureId, studentId);

        return  lectureNotes.stream().map(lectureNoteMapper::toResponse).collect(Collectors.toList());
    }
}
