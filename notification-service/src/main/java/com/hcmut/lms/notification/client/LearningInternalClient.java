package com.hcmut.lms.notification.client;

import com.hcmut.lms.notification.client.dto.BatchClassStudentIdsRequest;
import com.hcmut.lms.notification.client.dto.BatchClassStudentIdsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "learning-service")
public interface LearningInternalClient {

    @GetMapping("/api/learning/internal/enrollments/class/{classId}/students")
    List<UUID> resolveStudentsByClass(@PathVariable UUID classId);

    @PostMapping("/api/learning/internal/enrollments/classes/students")
    BatchClassStudentIdsResponse resolveStudentsByClassBatch(@RequestBody BatchClassStudentIdsRequest request);

    @GetMapping("/api/learning/internal/enrollments/course/{courseId}/students")
    List<UUID> resolveStudentsByCourse(@PathVariable UUID courseId);
}
