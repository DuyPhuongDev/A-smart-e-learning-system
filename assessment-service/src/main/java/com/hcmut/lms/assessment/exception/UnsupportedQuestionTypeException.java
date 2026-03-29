package com.hcmut.lms.assessment.exception;

import com.hcmut.lms.assessment.domain.entity.question.QuestionType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UnsupportedQuestionTypeException extends RuntimeException {
    public UnsupportedQuestionTypeException(QuestionType questionType) {
        super("No handler registered for question type: " + questionType);
    }
}
