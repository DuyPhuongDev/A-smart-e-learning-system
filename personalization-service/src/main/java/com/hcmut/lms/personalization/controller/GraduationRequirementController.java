package com.hcmut.lms.personalization.controller;

import com.hcmut.lms.common.helper.CurrentUser;
import com.hcmut.lms.common.helper.CurrentUserInfo;
import com.hcmut.lms.personalization.application.dto.request.UpdateGraduationRequirementStatusRequest;
import com.hcmut.lms.personalization.application.dto.response.GraduationRequirementStatusResponse;
import com.hcmut.lms.personalization.application.service.GraduationRequirementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${prefix-api}/graduation-requirements")
@RequiredArgsConstructor
public class GraduationRequirementController {

  private final GraduationRequirementService graduationRequirementService;

  @GetMapping("/me")
  public ResponseEntity<List<GraduationRequirementStatusResponse>> getMyGraduationRequirements(
      @CurrentUser CurrentUserInfo currentUser) {
    return ResponseEntity.ok(graduationRequirementService.getMyGraduationRequirements(currentUser.getId()));
  }

  @GetMapping("/{studentId}")
  public ResponseEntity<List<GraduationRequirementStatusResponse>> getMyGraduationRequirementsByStudentId(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(graduationRequirementService.getMyGraduationRequirements(studentId));
  }

  @PutMapping("/{graduationRequirementStatusId}")
  public ResponseEntity<GraduationRequirementStatusResponse> updateGraduationRequirementStatus(
      @PathVariable UUID graduationRequirementStatusId,
      @Valid @RequestBody UpdateGraduationRequirementStatusRequest request, @CurrentUser CurrentUserInfo currentUser) {
    return ResponseEntity.ok(graduationRequirementService.updateGraduationRequirementStatus(
        currentUser.getId(),
        graduationRequirementStatusId, request));
  }

}
