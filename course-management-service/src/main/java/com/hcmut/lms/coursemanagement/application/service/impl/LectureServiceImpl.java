package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.BaseLectureRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.LectureResponse;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapper;
import com.hcmut.lms.coursemanagement.application.mapper.LectureMapperHelper;
import com.hcmut.lms.coursemanagement.application.service.FileService;
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

import java.util.*;
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
    private final FileService fileService;
    private static final String DOCUMENT_FOLDER = "lecture/document";
    
    @Override
    public LectureResponse createLecture(BaseLectureRequest request) {
        log.info("Creating lecture: {} of type {}", request.getTitle(), request.getLectureType());
        
        // Tìm chapter
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + request.getChapterId()));


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

    @Transactional
    public List<LectureResponse> reorderLectures(ReorderRequest request) {
        log.info("Reordering lecture with id: {} to new order: {}", request.getId(), request.getNewOrderIndex());

        Lecture target = lectureRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Lecture not found with id: " + request.getId()));

        int oldOrder = target.getOrderIndex();
        List<Lecture> lectures = getSortedLectures(target);
        int newOrder = validateNewOrder(request.getNewOrderIndex(), lectures.size());

        // If order doesn't change, return early
        if (oldOrder == newOrder) {
            log.info("Order unchanged, returning current state");
            return lectures.stream().map(lectureMapper::toResponseDTO).toList();
        }

        // Step 1: Set target lecture to temporary order index (-1) to avoid constraint violation
        target.setOrderIndex(-1);
        lectureRepository.saveAndFlush(target);
        log.debug("Set target lecture to temporary order index: -1");

        // Step 2: Shift other lecture
        if (newOrder > oldOrder) {
            // Moving down: shift lectures between oldOrder and newOrder up
            for (int i = oldOrder + 1; i <= newOrder; i++) {
                lectures.get(i).setOrderIndex(i - 1);
            }
        } else {
            // Moving up: shift lectures between newOrder and oldOrder down
            for (int i = newOrder; i < oldOrder; i++) {
                lectures.get(i).setOrderIndex(i + 1);
            }
        }

        // Step 3: Save shifted lectures
        lectureRepository.saveAllAndFlush(lectures);
        log.debug("Shifted chapters saved");

        // Step 4: Set target lecture to final position
        target.setOrderIndex(newOrder);
        lectureRepository.saveAndFlush(target);
        log.info("Target lecture moved to new order index: {}", newOrder);

        // Return all lectures in sorted order
        List<Lecture> result = getSortedLectures(target);
        return result.stream().map(lectureMapper::toResponseDTO).toList();
    }

    private List<Lecture> getSortedLectures(Lecture target) {
        List<Lecture> lectures = new ArrayList<>(
                target.getChapter().getLectures()
        );
        lectures.sort(Comparator.comparing(Lecture::getOrderIndex));
        return lectures;
    }

    private Integer validateNewOrder(int newOrder, int size) {
        if (newOrder > size) return size-1;
        if (newOrder < 0) return 0;
        return newOrder;
    }
}

