package com.hcmut.lms.assessment.domain.entity.submission;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mcq_submissions")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class McqSubmission extends QuestionSubmission {

    @Column(name = "selected_count")
    private Integer selectedCount;

    @OneToMany(mappedBy = "mcqSubmission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AnswerOptionMcqSubmission> selectedOptions = new ArrayList<>();

    public void addSelectedOption(AnswerOptionMcqSubmission selectedOption) {
        selectedOptions.add(selectedOption);
        selectedOption.setMcqSubmission(this);
    }
}
