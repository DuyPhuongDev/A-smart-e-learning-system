package com.hcmut.lms.assessment.repository;

import com.hcmut.lms.assessment.domain.entity.answer.EssayAcceptedFileType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EssayAcceptFileTypeRepository extends JpaRepository<EssayAcceptedFileType, UUID> {
    void deleteAllByEssayQuestion_Id(UUID essayId);
}
