package com.hcmut.lms.assessment.domain.entity.question;

import com.hcmut.lms.assessment.domain.entity.answer.EssayAcceptedFileType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "essay_questions")
@PrimaryKeyJoinColumn(name = "id")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EssayQuestion extends Question {

    @Column(name = "sample_answer", columnDefinition = "text")
    private String sampleAnswer;

    @Column(name = "max_file_size")
    private int maxFileSize;

    @OneToMany(mappedBy = "essayQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EssayAcceptedFileType> acceptedFileTypes = new ArrayList<>();

    public void addAcceptedFileType(EssayAcceptedFileType fileType) {
        acceptedFileTypes.add(fileType);
        fileType.setEssayQuestion(this);
    }
}
