package com.hcmut.lms.personalization.client;

import com.hcmut.lms.personalization.client.dto.CurriculumFullResponse;
import com.hcmut.lms.personalization.client.dto.CurriculumResolutionResponse;
import com.hcmut.lms.personalization.client.dto.GraduationRequirementResponse;
import com.hcmut.lms.personalization.client.dto.PrerequisiteChainRequest;
import com.hcmut.lms.personalization.client.dto.PrerequisiteChainResponse;
import com.hcmut.lms.personalization.client.dto.SemesterResponse;
import com.hcmut.lms.personalization.client.dto.StudentLearningProgressResponse;
import com.hcmut.lms.personalization.client.dto.SubjectLearningOutcomeResponse;
import com.hcmut.lms.personalization.client.dto.SubjectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "course-management-service", path = "/api/courses/internal")
public interface CourseManagementClient {

    @GetMapping("/subjects/{subjectId}")
    SubjectResponse getSubjectById(@PathVariable UUID subjectId);

    @GetMapping("/subjects/{subjectId}/learning-outcomes")
    List<SubjectLearningOutcomeResponse> getLearningOutcomes(@PathVariable UUID subjectId);

    @GetMapping("/graduation-requirements/students/{studentId}")
    List<GraduationRequirementResponse> getGraduationRequirementsByStudentId(@PathVariable UUID studentId);

    @PostMapping("/curriculums/prerequisite-chains")
    PrerequisiteChainResponse getPrerequisiteChain(@RequestBody PrerequisiteChainRequest request);

    @GetMapping("/student-progress/internal/{studentId}")
    StudentLearningProgressResponse getStudentProgress(@PathVariable UUID studentId);

    @GetMapping("/semesters/remaining/{studentId}")
    List<SemesterResponse> getRemainingSemesters(@PathVariable UUID studentId);

    @GetMapping("/curriculums/{curriculumCode}/full")
    CurriculumFullResponse getCurriculumFull(@PathVariable String curriculumCode);

    @GetMapping("/curriculums/resolve")
    CurriculumResolutionResponse resolveCurriculum(
        @RequestParam UUID specializationId,
        @RequestParam Integer intakeYear);
}
