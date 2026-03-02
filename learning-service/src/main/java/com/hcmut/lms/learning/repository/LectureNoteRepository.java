package com.hcmut.lms.learning.repository;

import com.hcmut.lms.learning.entity.lecturenote.LectureNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LectureNoteRepository extends JpaRepository<LectureNote, UUID> {
    List<LectureNote>  findByLectureIdAndStudentIdOrderByContentPositionAsc(UUID lectureId, UUID studentId);
}
