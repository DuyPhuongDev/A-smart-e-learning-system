package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderListRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapper;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapperHelper;
import com.hcmut.lms.coursemanagement.application.service.LectureService;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategy;
import com.hcmut.lms.coursemanagement.application.strategy.LectureUpdateStrategyProvider;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.lecture.*;
import com.hcmut.lms.coursemanagement.domain.factory.LectureFactoryProvider;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.LectureRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LectureServiceImpl implements LectureService {
    
    private final LectureRepository lectureRepository;
    private final ChapterRepository chapterRepository;
    private final LectureFactoryProvider lectureFactoryProvider;
    private final LectureMapper lectureMapper;
    private final LectureMapperHelper lectureMapperHelper;
    private final LectureUpdateStrategyProvider strategyProvider;
    
    @Override
    public LectureResponse createLecture(BaseLectureRequest request) {
        log.info("Creating lecture: {} of type {}", request.getTitle(), request.getLectureType());
        
        // Tìm chapter
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + request.getChapterId()));
        
        // Sử dụng factory để tạo lecture
        Lecture lecture = lectureFactoryProvider.createLecture(request);
        lecture.setChapter(chapter);
        
        // Tự động gán order_index nếu không được cung cấp
        if (lecture.getOrderIndex() == null) {
            List<Lecture> existingLectures = lectureRepository.findByChapterIdOrderByOrderIndex(
                    request.getChapterId());
            int maxOrderIndex = existingLectures.stream()
                    .mapToInt(Lecture::getOrderIndex)
                    .max()
                    .orElse(-1);
            lecture.setOrderIndex(maxOrderIndex + 1);
        }
        
        // Khởi tạo các giá trị mặc định
        if (lecture.getViewCount() == null) {
            lecture.setViewCount(0);
        }
        if (lecture.getCompletionRate() == null) {
            lecture.setCompletionRate(0.0f);
        }
        
        Lecture savedLecture = lectureRepository.save(lecture);
        
        log.info("Lecture created successfully with id: {}", savedLecture.getId());
        return lectureMapperHelper.toResponseDTO(savedLecture);
    }
    
    @Override
    public LectureResponse updateLecture(UUID id, BaseLectureRequest request) {
        log.info("Updating lecture with id: {} of type {}", id, request.getLectureType());
        
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecture not found with id: " + id));
        
        // Validate lecture type matches
        if (!lecture.getLectureType().equals(request.getLectureType())) {
            throw new IllegalArgumentException(
                    String.format("Lecture type mismatch. Expected: %s, but got: %s", 
                            lecture.getLectureType(), request.getLectureType()));
        }
        
        // Validate request using strategy
        LectureUpdateStrategy strategy = strategyProvider.getStrategy(request.getLectureType());
        strategy.validate(request);
        
        // Cập nhật các thuộc tính chung
        lectureMapper.updateEntityFromDTO(request, lecture);
        
        // Cập nhật chapter nếu thay đổi
        if (request.getChapterId() != null && !request.getChapterId().equals(lecture.getChapter().getId())) {
            Chapter newChapter = chapterRepository.findById(request.getChapterId())
                    .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + request.getChapterId()));
            lecture.setChapter(newChapter);
        }
        
        // Cập nhật các thuộc tính cụ thể theo loại lecture sử dụng Strategy Pattern
        strategy.updateSpecificFields(lecture, request);
        
        Lecture updatedLecture = lectureRepository.save(lecture);
        
        log.info("Lecture updated successfully with id: {}", id);
        return lectureMapperHelper.toResponseDTO(updatedLecture);
    }
    
    @Override
    @Transactional(readOnly = true)
    public LectureResponse getLectureById(UUID id) {
        log.info("Getting lecture with id: {}", id);
        
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lecture not found with id: " + id));
        
        return lectureMapperHelper.toResponseDTO(lecture);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LectureResponse> getLecturesByChapterId(UUID chapterId) {
        log.info("Getting lectures by chapter id: {}", chapterId);
        
        return lectureRepository.findByChapterIdOrderByOrderIndex(chapterId).stream()
                .map(lectureMapperHelper::toResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteLecture(UUID id) {
        log.info("Deleting lecture with id: {}", id);
        
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecture not found with id: " + id));
        
        // Use strategy pattern for pre-delete operations
        try {
            LectureUpdateStrategy strategy = strategyProvider.getStrategy(lecture.getLectureType());
            strategy.beforeDelete(lecture);
        } catch (IllegalArgumentException e) {
            log.warn("No strategy found for lecture type: {}. Skipping pre-delete operations.", 
                    lecture.getLectureType());
        }
        
        lectureRepository.deleteById(id);
        
        // Use strategy pattern for post-delete operations
        try {
            LectureUpdateStrategy strategy = strategyProvider.getStrategy(lecture.getLectureType());
            strategy.afterDelete(id);
        } catch (IllegalArgumentException e) {
            log.warn("No strategy found for lecture type: {}. Skipping post-delete operations.", 
                    lecture.getLectureType());
        }
        
        log.info("Lecture deleted successfully with id: {}", id);
    }
    
    @Override
    public List<LectureResponse> reorderLectures(ReorderListRequest request) {
        log.info("Reordering lectures");
        
        Map<UUID, Integer> orderMap = request.getItems().stream()
                .collect(Collectors.toMap(ReorderRequest::getId, ReorderRequest::getNewOrderIndex));
        
        List<Lecture> lectures = lectureRepository.findAllById(orderMap.keySet());
        
        if (lectures.size() != orderMap.size()) {
            throw new RuntimeException("Some lectures not found");
        }
        
        lectures.forEach(lecture -> {
            Integer newOrderIndex = orderMap.get(lecture.getId());
            lecture.setOrderIndex(newOrderIndex);
        });
        
        List<Lecture> savedLectures = lectureRepository.saveAll(lectures);
        
        log.info("Lectures reordered successfully");
        return savedLectures.stream()
                .sorted((l1, l2) -> l1.getOrderIndex().compareTo(l2.getOrderIndex()))
                .map(lectureMapperHelper::toResponseDTO)
                .collect(Collectors.toList());
    }
}

