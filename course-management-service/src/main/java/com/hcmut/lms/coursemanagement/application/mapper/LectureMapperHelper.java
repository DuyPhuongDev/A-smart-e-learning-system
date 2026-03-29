package com.hcmut.lms.coursemanagement.application.mapper;

import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.*;
import org.springframework.stereotype.Component;

@Component
public class LectureMapperHelper {
    
    public LectureResponse toResponseDTO(Lecture lecture) {
        if (lecture == null) {
            return null;
        }
        
        LectureResponse.LectureResponseBuilder builder = LectureResponse.builder()
                .id(lecture.getId())
                .orderIndex(lecture.getOrderIndex())
                .title(lecture.getTitle())
                .description(lecture.getDescription())
                .isMandatory(lecture.getIsMandatory())
                .lectureType(lecture.getLectureType())
                .completionRate(lecture.getCompletionRate())
                .estimateTimeSpent(lecture.getEstimateTimeSpent())
                .viewCount(lecture.getViewCount())
                .allowPreview(lecture.getAllowPreview())
                .isDownloadable(lecture.getIsDownloadable())
                .chapterId(lecture.getChapter() != null ? lecture.getChapter().getId() : null)
                .createdAt(lecture.getCreatedAt())
                .updatedAt(lecture.getUpdatedAt());
        
        // Thêm thông tin cụ thể theo loại lecture
        if (lecture instanceof VideoLecture videoLecture) {
            builder.videoUrl(videoLecture.getVideoUrl())
                   .duration(videoLecture.getDuration());
        } else if (lecture instanceof DocumentLecture documentLecture) {
            builder.fileUrl(documentLecture.getFileUrl())
                   .numPages(documentLecture.getNumPages())
                   .fileFormat(documentLecture.getFileFormat());
        } else if (lecture instanceof TextLecture textLecture) {
            builder.content(textLecture.getContent())
                   .wordCount(textLecture.getWordCount())
                   .formatType(textLecture.getFormatType());
        }
        
        return builder.build();
    }
}

