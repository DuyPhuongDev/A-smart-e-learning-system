package com.hcmut.lms.notification.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "course-management-service")
public interface CourseManagementInternalClient {

    @GetMapping("/api/courses/internal/class-sections/teacher/{teacherId}/class-ids")
    List<UUID> getClassIdsByTeacher(@PathVariable UUID teacherId);
}
