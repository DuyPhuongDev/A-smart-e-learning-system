package com.hcmut.lms.coursemanagement.domain.repository;

import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VideoLectureRepository extends JpaRepository<VideoLecture, UUID> {
}
