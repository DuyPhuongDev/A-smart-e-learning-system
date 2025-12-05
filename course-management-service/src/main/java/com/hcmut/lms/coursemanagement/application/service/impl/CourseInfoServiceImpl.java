package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.coursemanagement.application.dto.request.CourseInfoRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseInfoResponse;
import com.hcmut.lms.coursemanagement.application.mapper.CourseInfoMapper;
import com.hcmut.lms.coursemanagement.application.service.CourseInfoService;
import com.hcmut.lms.coursemanagement.application.service.FileService;
import com.hcmut.lms.coursemanagement.domain.entity.classSection.ClassSection;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfo;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfoId;
import com.hcmut.lms.coursemanagement.repository.ClassSectionRepository;
import com.hcmut.lms.coursemanagement.repository.CourseInfoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CourseInfoServiceImpl implements CourseInfoService {

    private final CourseInfoRepository courseInfoRepository;
    private final ClassSectionRepository classSectionRepository;
    private final CourseInfoMapper courseInfoMapper;
    private final FileService fileService;

    private static final String THUMBNAILPATH = "thumbnail";
    private static final String INTROVIDEOPATH = "intro-video";

    @Override
    public CourseInfoResponse createCourseInfo(CourseInfoRequest request, MultipartFile thumbnail, MultipartFile introVideo) {
        log.info("Creating course info");
        CourseInfo courseInfo = courseInfoMapper.toEntity(request);

        if(request.getClassSectionId() != null) {
            ClassSection classSection = classSectionRepository.findById(request.getClassSectionId()).orElseThrow(() -> new EntityNotFoundException("Class section not found"));
            courseInfo.setId(new CourseInfoId(request.getCourseName(), request.getClassSectionId()));
            courseInfo.setClassSection(classSection);

        }



        if(thumbnail != null) {
            courseInfo.setThumbnailUrl(fileService.uploadFile(THUMBNAILPATH, thumbnail));
        }

        if(introVideo != null) {
            courseInfo.setIntroVideo(fileService.uploadFile(INTROVIDEOPATH, introVideo));
        }

        return courseInfoMapper.toResponseEntity(courseInfoRepository.save(courseInfo));
    }

    @Override
    public CourseInfoResponse updateCourseInfo(CourseInfoId id, CourseInfoRequest request, MultipartFile thumbnail, MultipartFile introVideo) {
        log.info("Updating course info");
        CourseInfo courseInfo = courseInfoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course info not found"));

        courseInfoMapper.updateEntityFromRequest(request, courseInfo);

        if(thumbnail != null) {
            courseInfo.setThumbnailUrl(fileService.replaceFile(courseInfo.getThumbnailUrl(), THUMBNAILPATH,thumbnail));
        }

        if(introVideo != null) {
            courseInfo.setIntroVideo(fileService.replaceFile(courseInfo.getIntroVideo(), INTROVIDEOPATH, introVideo));
        }

        return courseInfoMapper.toResponseEntity(courseInfoRepository.save(courseInfo));
    }

    @Override
    public void deleteCourseInfo(CourseInfoId id) {
        log.info("Deleting course info");
        CourseInfo courseInfo = courseInfoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Course info not found"));
        courseInfoRepository.deleteById(id);

        fileService.deleteFile(courseInfo.getThumbnailUrl());
        fileService.deleteFile(courseInfo.getIntroVideo());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseInfoResponse getCourseInfoById(CourseInfoId id) {
        return courseInfoMapper.toResponseEntity(courseInfoRepository.findById(id).orElseThrow(()  -> new EntityNotFoundException("Course info not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public CourseInfoResponse getCourseInfosByClassId(UUID classId) {
        return courseInfoMapper.toResponseEntity(courseInfoRepository.findFirstByClassSection_Id(classId).orElseThrow(()  -> new EntityNotFoundException("Course info not found with classId: " + classId)));
    }
}
