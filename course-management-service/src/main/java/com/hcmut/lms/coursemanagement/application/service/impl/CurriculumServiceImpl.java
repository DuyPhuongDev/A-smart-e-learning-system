package com.hcmut.lms.coursemanagement.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.coursemanagement.application.config.CurriculumFallbackConfig;
import com.hcmut.lms.coursemanagement.application.dto.request.CurriculumRequest;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumFullResponse;
import com.hcmut.lms.coursemanagement.application.dto.response.CurriculumResponse;
import com.hcmut.lms.coursemanagement.application.mapper.CurriculumMapper;
import com.hcmut.lms.coursemanagement.application.service.CurriculumService;
import com.hcmut.lms.coursemanagement.domain.entity.academicYear.AcademicYear;
import com.hcmut.lms.coursemanagement.domain.entity.curriculum.*;
import com.hcmut.lms.coursemanagement.domain.entity.specialization.Specialization;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectParallel;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectPrerequisite;
import com.hcmut.lms.coursemanagement.domain.entity.subject.SubjectRecommendation;
import com.hcmut.lms.coursemanagement.repository.*;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CurriculumServiceImpl implements CurriculumService {

  private final CurriculumRepository curriculumRepository;
  private final SpecializationRepository specializationRepository;
  private final CurriculumMapper curriculumMapper;
  private final AcademicYearRepository academicYearRepository;
  private final CurriculumSectionRepository curriculumSectionRepository;
  private final CurriculumSubjectRepository curriculumSubjectRepository;
  private final CurriculumSubjectPriorityRepository curriculumSubjectPriorityRepository;
  private final CurriculumFallbackConfig curriculumFallbackConfig;

  @Override
  public CurriculumResponse createCurriculum(CurriculumRequest request) {
    log.info("Creating curriculum: {}", request.getCode());

    CurriculumId curriculumId = new CurriculumId(
        request.getCode(), request.getSpecializationId(),
        request.getIntakeYearId());

    if (curriculumRepository.existsById(curriculumId)) {
      throw new EntityExistsException("Curriculum already exists with the given composite key");
    }

    Curriculum curriculum = curriculumMapper.toEntity(request);
    curriculum.setId(curriculumId);

    if (request.getSpecializationId() != null) {
      Specialization specialization = specializationRepository.findById(request.getSpecializationId())
          .orElseThrow(
              () -> new EntityNotFoundException("Specialization not found with id: " + request.getSpecializationId()));
      curriculum.setSpecialization(specialization);
    }

    if (request.getIntakeYearId() != null) {
      AcademicYear intakeYear = academicYearRepository.findById(request.getIntakeYearId())
          .orElseThrow(
              () -> new EntityNotFoundException("Intake year not found with id: " + request.getIntakeYearId()));
      curriculum.setIntakeYear(intakeYear);
    }

    Curriculum savedCurriculum = curriculumRepository.save(curriculum);

    log.info("Curriculum created successfully");
    return curriculumMapper.toResponse(savedCurriculum);
  }

  @Override
  public CurriculumResponse updateCurriculum(
      String code, UUID specializationId, UUID intakeYearId,
      CurriculumRequest request) {
    log.info("Updating curriculum: {}", code);

    CurriculumId curriculumId = new CurriculumId(code, specializationId, intakeYearId);

    Curriculum curriculum = curriculumRepository.findById(curriculumId)
        .orElseThrow(() -> new EntityNotFoundException("Curriculum not found"));

    curriculumMapper.updateEntityFromRequest(request, curriculum);

    Curriculum updatedCurriculum = curriculumRepository.save(curriculum);

    log.info("Curriculum updated successfully");
    return curriculumMapper.toResponse(updatedCurriculum);
  }

  @Override
  @Transactional(readOnly = true)
  public CurriculumResponse getCurriculumById(String code, UUID specializationId, UUID intakeYearId) {
    log.info("Getting curriculum: {}", code);

    CurriculumId curriculumId = new CurriculumId(code, specializationId, intakeYearId);

    Curriculum curriculum = curriculumRepository.findById(curriculumId)
        .orElseThrow(() -> new EntityNotFoundException("Curriculum not found"));

    return curriculumMapper.toResponse(curriculum);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurriculumResponse> getAllCurriculums() {
    log.info("Getting all curriculums");

    return curriculumRepository.findAll().stream().map(curriculumMapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<CurriculumResponse> getAllCurriculums(int page, int size) {
    log.info("Getting all curriculums with pagination - page: {}, size: {}", page, size);

    Pageable pageable = PageRequest.of(page, size);
    Page<Curriculum> curriculumPage = curriculumRepository.findAll(pageable);

    Page<CurriculumResponse> responsePage = curriculumPage.map(curriculumMapper::toResponse);
    return PageResponse.fromPage(responsePage);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurriculumResponse> getCurriculumsBySpecializationId(UUID specializationId) {
    log.info("Getting curriculums by specialization id: {}", specializationId);

    return curriculumRepository.findByIdSpecializationId(specializationId)
        .stream()
        .map(curriculumMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<CurriculumResponse> getCurriculumsBySpecializationId(UUID specializationId, int page, int size) {
    log.info(
        "Getting curriculums by specialization id: {} with pagination - page: {}, size: {}", specializationId, page,
        size);

    Pageable pageable = PageRequest.of(page, size);
    Page<Curriculum> curriculumPage = curriculumRepository.findByIdSpecializationId(specializationId, pageable);

    Page<CurriculumResponse> responsePage = curriculumPage.map(curriculumMapper::toResponse);
    return PageResponse.fromPage(responsePage);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CurriculumResponse> getCurriculumsByIntakeYearId(UUID intakeYearId) {
    log.info("Getting curriculums by intake year id: {}", intakeYearId);

    return curriculumRepository.findByIdIntakeYearId(intakeYearId).stream().map(curriculumMapper::toResponse).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<CurriculumResponse> getCurriculumsByIntakeYearId(UUID intakeYearId, int page, int size) {
    log.info(
        "Getting curriculums by intake year id: {} with pagination - page: {}, size: {}", intakeYearId, page, size);

    Pageable pageable = PageRequest.of(page, size);
    Page<Curriculum> curriculumPage = curriculumRepository.findByIdIntakeYearId(intakeYearId, pageable);

    Page<CurriculumResponse> responsePage = curriculumPage.map(curriculumMapper::toResponse);
    return PageResponse.fromPage(responsePage);
  }

  @Override
  @Transactional(readOnly = true)
  public CurriculumResponse resolveCurriculumBySpecializationAndIntakeYear(UUID specializationId, Integer intakeYear) {
    log.info("Resolving curriculum by specializationId={} and intakeYear={}", specializationId, intakeYear);

    if (specializationId == null || intakeYear == null) {
      throw new IllegalArgumentException("specializationId and intakeYear are required");
    }

    List<Curriculum> matches = curriculumRepository.findByIdSpecializationId(specializationId)
        .stream()
        .filter(curriculum -> matchesIntakeYear(curriculum, intakeYear))
        .toList();

    if (matches.isEmpty()) {
      Integer fallbackYear = resolveFallbackIntakeYear();
      log.warn("No curriculum found for specializationId={}, intakeYear={}. Falling back to intake year {}",
          specializationId, intakeYear, fallbackYear);
      matches = curriculumRepository.findByIdSpecializationId(specializationId)
          .stream()
          .filter(curriculum -> matchesIntakeYear(curriculum, fallbackYear))
          .toList();
    }

    if (matches.isEmpty()) {
      throw new EntityNotFoundException(
          "Curriculum not found for specializationId=" + specializationId + " and intakeYear=" + intakeYear
              + " (including fallback)");
    }

    if (matches.size() > 1) {
      throw new IllegalStateException(
          "Multiple curriculums found for specializationId=" + specializationId + " and intakeYear=" + intakeYear);
    }

    return curriculumMapper.toResponse(matches.getFirst());
  }

  @Override
  public void deleteCurriculum(String code, UUID specializationId, UUID intakeYearId) {
    log.info("Deleting curriculum: {}", code);

    CurriculumId curriculumId = new CurriculumId(code, specializationId, intakeYearId);

    if (!curriculumRepository.existsById(curriculumId)) {
      throw new EntityNotFoundException("Curriculum not found");
    }

    curriculumRepository.deleteById(curriculumId);
    log.info("Curriculum deleted successfully");
  }

  @Override
  @Transactional(readOnly = true)
  public CurriculumFullResponse getCurriculumFull(String curriculumCode) {
    log.info("Fetching full curriculum for curriculumCode={}", curriculumCode);

    Curriculum curriculum = curriculumRepository.findAll()
        .stream()
        .filter(c -> c.getId() != null && curriculumCode.equals(c.getId().getCode()))
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException("Curriculum not found with code: " + curriculumCode));

    UUID specializationId = curriculum.getId().getSpecializationId();
    UUID intakeYearId = curriculum.getId().getIntakeYearId();

    List<CurriculumSection> sections = curriculumSectionRepository.findByCurriculumId(
        curriculumCode, specializationId, intakeYearId);

    List<CurriculumSubject> subjects = curriculumSubjectRepository.findAll()
        .stream()
        .filter(cs -> cs.getCurriculumSection() != null && cs.getCurriculumSection()
            .getCurriculum() != null && cs.getCurriculumSection()
            .getCurriculum()
            .getId() != null && curriculumCode.equals(cs.getCurriculumSection().getCurriculum().getId().getCode()))
        .toList();

    Map<UUID, List<CurriculumSubject>> subjectsBySection = subjects.stream()
        .collect(Collectors.groupingBy(cs -> cs.getCurriculumSection().getId()));

    Map<String, CurriculumSubjectPriority> priorityByComposite = curriculumSubjectPriorityRepository.findAll()
        .stream()
        .collect(Collectors.toMap(
            p -> compositeKey(p.getCurriculumSectionId(), p.getSubjectId(), p.getCurriculumSubjectId()), p -> p,
            (left, right) -> left));

    List<CurriculumFullResponse.CurriculumSectionFull> sectionResponses = new ArrayList<>();
    for (CurriculumSection section : sections) {
      List<CurriculumSubject> sectionSubjects = subjectsBySection.getOrDefault(
          section.getId(), Collections.emptyList());
      List<CurriculumFullResponse.CurriculumSubjectFull> subjectResponses = new ArrayList<>();

      for (CurriculumSubject curriculumSubject : sectionSubjects) {
        CurriculumFullResponse.SubjectRelation prereq = null;
        List<CurriculumFullResponse.SubjectRelation> prerequisites = curriculumSubject.getPrerequisites()
            .stream()
            .map(SubjectPrerequisite::getPrerequisiteCurriculumSubject)
            .filter(Objects::nonNull)
            .map(this::toRelation)
            .filter(Objects::nonNull)
            .toList();

        List<CurriculumFullResponse.SubjectRelation> recommendations = curriculumSubject.getRecommendations()
            .stream()
            .map(SubjectRecommendation::getRecommendedCurriculumSubject)
            .filter(Objects::nonNull)
            .map(this::toRelation)
            .filter(Objects::nonNull)
            .toList();

        List<CurriculumFullResponse.SubjectRelation> parallels = curriculumSubject.getParallels()
            .stream()
            .map(SubjectParallel::getParallelCurriculumSubject)
            .filter(Objects::nonNull)
            .map(this::toRelation)
            .filter(Objects::nonNull)
            .toList();

        CurriculumSubjectPriority priority = priorityByComposite.get(
            compositeKey(section.getId(), curriculumSubject.getSubject().getId(), curriculumSubject.getId().getId()));

        subjectResponses.add(CurriculumFullResponse.CurriculumSubjectFull.builder()
            .curriculumSubjectId(curriculumSubject.getId().getId())
            .subjectId(curriculumSubject.getSubject().getId())
            .subjectCode(curriculumSubject.getSubject().getCode())
            .subjectName(curriculumSubject.getSubject().getName())
            .credits(curriculumSubject.getSubject().getCredits())
            .isRequired(curriculumSubject.getIsRequired())
            .displayOrder(curriculumSubject.getDisplayOrder())
            .recommendedYear(priority != null ? priority.getRecommendedYear() : null)
            .recommendedSemesterInYear(priority != null ? priority.getRecommendedSemesterInYear() : null)
            .prerequisites(prerequisites)
            .recommendations(recommendations)
            .parallels(parallels)
            .build());
      }

      sectionResponses.add(CurriculumFullResponse.CurriculumSectionFull.builder()
          .sectionId(section.getId())
          .sectionName(section.getName())
          .requiredCredits(section.getRequiredCredits())
          .displayOrder(section.getDisplayOrder())
          .priorityWeight(section.getPriorityWeight())
          .subjects(subjectResponses)
          .build());
    }

    return CurriculumFullResponse.builder()
        .curriculumCode(curriculumCode)
        .specializationId(specializationId)
        .intakeYearId(intakeYearId)
        .name(curriculum.getName())
        .totalCredits(curriculum.getTotalCredits())
        .sections(sectionResponses)
        .build();
  }

  private String compositeKey(UUID sectionId, UUID subjectId, Integer curriculumSubjectId) {
    return sectionId + ":" + subjectId + ":" + curriculumSubjectId;
  }

  private CurriculumFullResponse.SubjectRelation toRelation(CurriculumSubject curriculumSubject) {
    if (curriculumSubject == null || curriculumSubject.getSubject() == null) {
      return null;
    }
    return CurriculumFullResponse.SubjectRelation.builder()
        .subjectId(curriculumSubject.getSubject().getId())
        .subjectCode(curriculumSubject.getSubject().getCode())
        .subjectName(curriculumSubject.getSubject().getName())
        .build();
  }

  private boolean matchesIntakeYear(Curriculum curriculum, Integer intakeYear) {
    if (curriculum == null || curriculum.getIntakeYear() == null) {
      return false;
    }

    if (curriculum.getIntakeYear().getStartDate() != null && curriculum.getIntakeYear()
        .getStartDate()
        .getYear() == intakeYear) {
      return true;
    }

    String yearCode = curriculum.getIntakeYear().getYearCode();
    if (yearCode != null) {
      try {
        int parsedYear = Integer.parseInt(yearCode.trim());
        int fullYear = parsedYear < 100 ? parsedYear + 2000 : parsedYear;
        return fullYear == intakeYear;
      } catch (NumberFormatException e) {
        return false;
      }
    }
    return false;
  }

  private Integer resolveFallbackIntakeYear() {
    return academicYearRepository.findByYearCode(curriculumFallbackConfig.getFallbackIntakeYearCode())
        .map(ay -> {
          if (ay.getStartDate() != null) {
            return ay.getStartDate().getYear();
          }
          try {
            int parsed = Integer.parseInt(ay.getYearCode().trim());
            return parsed < 100 ? parsed + 2000 : parsed;
          } catch (NumberFormatException e) {
            throw new EntityNotFoundException("Cannot parse fallback yearCode: " + ay.getYearCode());
          }
        })
        .orElseThrow(() -> new EntityNotFoundException(
            "Fallback academic year not found with yearCode=" + curriculumFallbackConfig.getFallbackIntakeYearCode()));
  }
}

