package com.hcmut.lms.assessment.mapper;

import com.hcmut.lms.assessment.domain.entity.questionBank.QuestionBank;
import com.hcmut.lms.assessment.dto.request.questionbank.QuestionBankRequest;
import com.hcmut.lms.assessment.dto.response.QuestionBankResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface QuestionBankMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId",  ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "questions", ignore = true)
    QuestionBank toEntity(QuestionBankRequest request);

    @Mapping(target = "questionCount", expression = "java(bank.getQuestions().size())")
    QuestionBankResponse toResponse(QuestionBank bank);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "questions", ignore = true)
    void updateEntity(QuestionBankRequest request, @MappingTarget QuestionBank bank);
}
