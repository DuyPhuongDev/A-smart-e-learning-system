package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.ChapterRequest;
import com.hcmut.lms.coursemanagement.application.dto.request.ReorderRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.ChapterResponse;
import com.hcmut.lms.coursemanagement.application.mapper.ChapterMapper;
import com.hcmut.lms.coursemanagement.application.service.ChapterService;
import com.hcmut.lms.coursemanagement.domain.entity.chapter.Chapter;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.repository.ChapterRepository;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChapterServiceImpl implements ChapterService {
    
    private final ChapterRepository chapterRepository;
    private final ClassSectionRepository classSectionRepository;
    private final ChapterMapper chapterMapper;
    
    @Override
    public ChapterResponse createChapter(ChapterRequest requestDTO) {
        log.info("Creating chapter: {}", requestDTO.getTitle());
        
        ClassSection classSection = classSectionRepository.findById(requestDTO.getClassSectionId())
                .orElseThrow(() -> new EntityNotFoundException("Class section not found with id: " + requestDTO.getClassSectionId()));
        
        Chapter chapter = chapterMapper.toEntity(requestDTO);
        chapter.setClassSection(classSection);
        
        // Tự động gán order_index nếu không được cung cấp
        if (chapter.getOrderIndex() == null) {
            List<Chapter> existingChapters = chapterRepository.findByClassSectionIdOrderByOrderIndex(
                    requestDTO.getClassSectionId());
            int maxOrderIndex = existingChapters.stream()
                    .mapToInt(Chapter::getOrderIndex)
                    .max()
                    .orElse(-1);
            chapter.setOrderIndex(maxOrderIndex + 1);
        }
        
        Chapter savedChapter = chapterRepository.save(chapter);
        
        log.info("Chapter created successfully with id: {}", savedChapter.getId());
        return chapterMapper.toResponseDTO(savedChapter);
    }
    
    @Override
    public ChapterResponse updateChapter(UUID id, ChapterRequest requestDTO) {
        log.info("Updating chapter with id: {}", id);
        
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        
        chapterMapper.updateEntityFromDTO(requestDTO, chapter);
        
        if (requestDTO.getClassSectionId() != null) {
            ClassSection classSection = classSectionRepository.findById(requestDTO.getClassSectionId())
                    .orElseThrow(() -> new RuntimeException("Class section not found with id: " + requestDTO.getClassSectionId()));
            chapter.setClassSection(classSection);
        }
        
        Chapter updatedChapter = chapterRepository.save(chapter);
        
        log.info("Chapter updated successfully with id: {}", id);
        return chapterMapper.toResponseDTO(updatedChapter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ChapterResponse getChapterById(UUID id) {
        log.info("Getting chapter with id: {}", id);
        
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chapter not found with id: " + id));
        
        return chapterMapper.toResponseDTO(chapter);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ChapterResponse> getChaptersByClassSectionId(UUID classSectionId) {
        log.info("Getting chapters by class section id: {}", classSectionId);
        
        return chapterRepository.findByClassSectionIdOrderByOrderIndex(classSectionId).stream()
                .map(chapterMapper::toResponseDTO)
                .toList();
    }
    
    @Override
    public void deleteChapter(UUID id) {
        log.info("Deleting chapter with id: {}", id);
        
        if (!chapterRepository.existsById(id)) {
            throw new EntityNotFoundException("Chapter not found with id: " + id);
        }
        
        chapterRepository.deleteById(id);
        log.info("Chapter deleted successfully with id: {}", id);
    }

    @Transactional
    public List<ChapterResponse> reorderChapters(ReorderRequest request) {
        log.info("Reordering chapter with id: {} to new order: {}", request.getId(), request.getNewOrderIndex());

        Chapter target = chapterRepository.findById(request.getId())
                .orElseThrow(() -> new EntityNotFoundException("Chapter not found with id: " + request.getId()));

        int oldOrder = target.getOrderIndex();
        List<Chapter> chapters = getSortedChapters(target);
        int newOrder = validateNewOrder(request.getNewOrderIndex(), chapters.size());

        // If order doesn't change, return early
        if (oldOrder == newOrder) {
            log.info("Order unchanged, returning current state");
            return chapters.stream().map(chapterMapper::toResponseDTO).toList();
        }

        // Step 1: Set target chapter to temporary order index (-1) to avoid constraint violation
        target.setOrderIndex(-1);
        chapterRepository.saveAndFlush(target);
        log.debug("Set target chapter to temporary order index: -1");

        // Step 2: Shift other chapters
        if (newOrder > oldOrder) {
            // Moving down: shift chapters between oldOrder and newOrder up
            for (int i = oldOrder + 1; i <= newOrder; i++) {
                chapters.get(i).setOrderIndex(i - 1);
            }
        } else {
            // Moving up: shift chapters between newOrder and oldOrder down
            for (int i = newOrder; i < oldOrder; i++) {
                chapters.get(i).setOrderIndex(i + 1);
            }
        }
        
        // Step 3: Save shifted chapters
        chapterRepository.saveAllAndFlush(chapters);
        log.debug("Shifted chapters saved");

        // Step 4: Set target chapter to final position
        target.setOrderIndex(newOrder);
        chapterRepository.saveAndFlush(target);
        log.info("Target chapter moved to new order index: {}", newOrder);

        // Return all chapters in sorted order
        List<Chapter> result = getSortedChapters(target);
        return result.stream().map(chapterMapper::toResponseDTO).toList();
    }

    private List<Chapter> getSortedChapters(Chapter target) {
        List<Chapter> chapters = new ArrayList<>(
                target.getClassSection().getChapters()
        );
        chapters.sort(Comparator.comparing(Chapter::getOrderIndex));
        return chapters;
    }

    private Integer validateNewOrder(int newOrder, int size) {
        if (newOrder > size) return size-1;
        if (newOrder < 0) return 0;
        return newOrder;
    }
}

