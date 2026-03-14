package com.hcmut.lms.personalization.application.service.impl;

import com.hcmut.lms.common.dto.PageResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationDetailResponse;
import com.hcmut.lms.personalization.application.dto.response.OccupationResponse;
import com.hcmut.lms.personalization.application.mapper.OccupationMapper;
import com.hcmut.lms.personalization.application.service.OccupationService;
import com.hcmut.lms.personalization.application.entity.OccupationData;
import com.hcmut.lms.personalization.repository.OccupationDataRepository;
import com.hcmut.lms.personalization.repository.OccupationSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OccupationServiceImpl implements OccupationService {

    private final OccupationDataRepository occupationDataRepository;
    private final OccupationSummaryRepository occupationSummaryRepository;
    private final OccupationMapper occupationMapper;

    @Override
    public PageResponse<OccupationResponse> getOccupations(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OccupationData> result = (keyword == null || keyword.isBlank())
                ? occupationDataRepository.findAll(pageable)
                : occupationDataRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return PageResponse.fromPage(result.map(occupationMapper::toResponse));
    }

    @Override
    public OccupationDetailResponse getOccupationDetail(String occupationCode) {
        log.info("Fetching occupation detail: {}", occupationCode);
        OccupationData data = occupationDataRepository.findByOnetsocCode(occupationCode)
                .orElseThrow(() -> new IllegalArgumentException("Occupation not found: " + occupationCode));

        return OccupationDetailResponse.builder()
                .onetsocCode(data.getOnetsocCode())
                .title(data.getTitle())
                .description(data.getDescription())
                .tasksCount(occupationSummaryRepository.countTasks(occupationCode))
                .dwaCount(occupationSummaryRepository.countDwa(occupationCode))
                .build();
    }
}
