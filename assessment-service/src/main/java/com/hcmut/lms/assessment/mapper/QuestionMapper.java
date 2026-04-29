package com.hcmut.lms.assessment.mapper;

import com.hcmut.lms.assessment.domain.entity.answer.AnswerOption;
import com.hcmut.lms.assessment.domain.entity.answer.EssayAcceptedFileType;
import com.hcmut.lms.assessment.domain.entity.answer.TestCase;
import com.hcmut.lms.assessment.domain.entity.question.*;
import com.hcmut.lms.assessment.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

    @Mapping(target = "answerOptions", source = "answerOptions")
    @Mapping(target = "point", ignore = true)
    @Mapping(target = "orderIndex",  ignore = true)
    McqQuestionResponse toResponse(McqQuestion question);

    @Mapping(target = "testCases", source = "testCases")
    @Mapping(target = "point", ignore = true)
    @Mapping(target = "orderIndex",  ignore = true)
    CodingQuestionResponse toResponse(CodingQuestion question);

    @Mapping(target = "point", ignore = true)
    @Mapping(target = "orderIndex",  ignore = true)
    EssayQuestionResponse toResponse(EssayQuestion question);

    AnswerOptionResponse toResponse(AnswerOption option);

    TestCaseResponse toResponse(TestCase testCase);

    @Mapping(target = "fileType", source = "fileType")
    default String toFileType(EssayAcceptedFileType fileType) {
        return fileType.getFileType();
    }


    default QuestionResponse toResponse(Question question) {
        if (question instanceof McqQuestion mcq) return toResponse(mcq);
        if (question instanceof CodingQuestion coding) return toResponse(coding);
        if (question instanceof EssayQuestion essay) return toResponse(essay);
        throw new IllegalArgumentException("Unknown question type: " + question.getClass().getSimpleName());
    }
}
