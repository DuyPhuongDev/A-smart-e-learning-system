package com.hcmut.lms.learning.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.learning.dto.request.LectureNoteRequest;
import com.hcmut.lms.learning.dto.response.LectureNoteResponse;
import com.hcmut.lms.learning.service.LecturetNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${prefix-api}/notes")
public class LectureNoteController {
    private final LecturetNoteService lecturetNoteService;

    @GetMapping()
    public ResponseEntity<List<LectureNoteResponse>> getLectureNotesByLectureIdOfCurrentStudent(
            @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestParam UUID lectureId
            ){
        return ResponseEntity.ok(lecturetNoteService.getLectureNotesByLectureIdAndStudentId(lectureId, currentUserInfo.getId()));
    }

    @PostMapping
    public ResponseEntity<LectureNoteResponse> createLectureNote(
            @RequestBody LectureNoteRequest lectureNoteRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(
                lecturetNoteService.createLectureNote(lectureNoteRequest)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<LectureNoteResponse> updateLectureNote(@PathVariable UUID id, @RequestBody LectureNoteRequest lectureNoteRequest){
        return ResponseEntity.ok(lecturetNoteService.updateLectureNote(id, lectureNoteRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LectureNoteResponse> deleteLectureNote(
            @PathVariable UUID id
    ){
        lecturetNoteService.deleteLectureNote(id);
        return ResponseEntity.noContent().build();
    }
}
