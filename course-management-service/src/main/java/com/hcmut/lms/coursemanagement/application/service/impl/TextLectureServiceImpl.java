package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.response.TextLectureContentResponse;
import com.hcmut.lms.coursemanagement.application.service.TextLectureService;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.TextLecture;
import com.hcmut.lms.coursemanagement.repository.TextLectureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implementation of TextLectureService
 * Handles text content retrieval for text-based lectures
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TextLectureServiceImpl implements TextLectureService {

    private final TextLectureRepository textLectureRepository;

    @Override
    @Transactional(readOnly = true)
    public TextLectureContentResponse getContent(UUID lectureId) {
        log.info("Getting content for text lecture: {}", lectureId);

        TextLecture textLecture = textLectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Text lecture not found with id: " + lectureId));

        String content = textLecture.getContent();
        if (content == null || content.isBlank()) {
            log.warn("Text content is empty for lecture: {}", lectureId);
        }

        return TextLectureContentResponse.builder()
                .lectureId(textLecture.getId())
                .content(content)
                .wordCount(textLecture.getWordCount())
                .formatType(textLecture.getFormatType())
                .title(textLecture.getTitle())
                .description(textLecture.getDescription())
                .build();
    }
}
