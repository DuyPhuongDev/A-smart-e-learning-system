package com.hcmut.lms.coursemanagement.controller;

import com.hcmut.lms.coursemanagement.application.dto.request.CourseInfoRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CourseInfoResponse;
import com.hcmut.lms.coursemanagement.application.service.CourseInfoService;
import com.hcmut.lms.coursemanagement.domain.entity.course.CourseInfoId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/course-info")
@RequiredArgsConstructor
public class CourseInfoController {

    private final CourseInfoService courseInfoService;

    @GetMapping("/class/{id}")
    public ResponseEntity<CourseInfoResponse> getCourseInfo(@PathVariable UUID id) {
        return ResponseEntity.ok(courseInfoService.getCourseInfosByClassId(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseInfoResponse> createCourseInfo(
            @RequestPart("course") @Valid CourseInfoRequest request,
            @RequestPart(value = "introVideo", required = false) MultipartFile introVideo,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(courseInfoService.createCourseInfo(request, thumbnail, introVideo));
    }

    @PutMapping(value = "/{name}/class/{classId}", consumes =  MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseInfoResponse> updateCourseInfo(
            @PathVariable String name,
            @PathVariable UUID classId,
            @RequestPart("course") @Valid CourseInfoRequest request,
            @RequestPart(value = "introVideo", required = false) MultipartFile introVideo,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        return ResponseEntity.ok(courseInfoService.updateCourseInfo(new CourseInfoId(name, classId), request, thumbnail, introVideo));
    }

    @DeleteMapping("/{name}/class/{classId}")
    public ResponseEntity<CourseInfoResponse> deleteCourseInfo(@PathVariable String name, @PathVariable UUID classId) {
        courseInfoService.deleteCourseInfo(new CourseInfoId(name, classId));
        return ResponseEntity.noContent().build();
    }

}
