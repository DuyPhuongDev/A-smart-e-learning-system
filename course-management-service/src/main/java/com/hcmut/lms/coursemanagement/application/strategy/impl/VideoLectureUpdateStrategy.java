package com.hcmut.lms.coursemanagement.application.strategy.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.VideoLectureRequest;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategy;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.Lecture;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.LectureType;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.VideoLecture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Strategy implementation for updating VideoLecture entities.
 */
@Component
@Slf4j
public class VideoLectureUpdateStrategy implements LectureUpdateStrategy {
    
    @Override
    public void updateSpecificFields(Lecture lecture, BaseLectureRequest request) {
        if (!(lecture instanceof VideoLecture)) {
            throw new IllegalArgumentException("Expected VideoLecture but got: " + lecture.getClass().getSimpleName());
        }
        
        if (!(request instanceof VideoLectureRequest)) {
            throw new IllegalArgumentException("Expected VideoLectureRequest but got: " + request.getClass().getSimpleName());
        }
        
        VideoLecture videoLecture = (VideoLecture) lecture;
        VideoLectureRequest videoRequest = (VideoLectureRequest) request;
        
        log.debug("Updating VideoLecture specific fields for lecture id: {}", lecture.getId());
        
        if (videoRequest.getVideoUrl() != null) {
            videoLecture.setVideoUrl(videoRequest.getVideoUrl());
        }
        
        if (videoRequest.getDuration() != null) {
            videoLecture.setDuration(videoRequest.getDuration());
        }
    }
    
    @Override
    public LectureType getSupportedLectureType() {
        return LectureType.VIDEO;
    }
    
    @Override
    public void validate(BaseLectureRequest request) {
        if (!(request instanceof VideoLectureRequest)) {
            return;
        }
        
        VideoLectureRequest videoRequest = (VideoLectureRequest) request;
        
        // Add specific validation for video lecture if needed
        if (videoRequest.getDuration() != null && videoRequest.getDuration() < 0) {
            throw new IllegalArgumentException("Duration cannot be negative");
        }
    }
    
    @Override
    public void beforeDelete(Lecture lecture) {
        if (lecture instanceof VideoLecture) {
            log.info("Performing pre-delete cleanup for VideoLecture id: {}", lecture.getId());
            // Add specific cleanup logic for video lecture if needed
            // For example: delete video files from storage, remove transcripts, etc.
            VideoLecture videoLecture = (VideoLecture) lecture;
            if (videoLecture.getVideoUrl() != null) {
                log.debug("Video URL to be cleaned up: {}", videoLecture.getVideoUrl());
            }
        }
    }
    
    @Override
    public void afterDelete(UUID lectureId) {
        log.info("Performing post-delete operations for VideoLecture id: {}", lectureId);
        // Add specific post-delete logic if needed
        // For example: update statistics, send notifications, etc.
    }
}

