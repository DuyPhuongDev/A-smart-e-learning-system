package com.hcmut.lms.assessment.domain.entity.questionBank;

import com.hcmut.lms.assessment.domain.entity.BaseEntity;
import com.hcmut.lms.assessment.domain.entity.question.Question;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "question_banks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "owner_id"})
})
public class QuestionBank extends BaseEntity {
    private String name;
    private String description;
    private boolean isPublic;
    private UUID ownerId;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "question_banks_questions",
            joinColumns = @JoinColumn(name = "question_banks_id"),
            inverseJoinColumns = @JoinColumn(name = "questions_id")
    )
    @Builder.Default
    private Set<Question> questions = new HashSet<>();

    // Helper Method: Giúp đồng bộ dữ liệu 2 chiều khi thêm câu hỏi vào ngân hàng
    public void addQuestion(Question question) {
        this.questions.add(question);
        question.getBanks().add(this);
    }

    public void removeQuestion(Question question) {
        this.questions.remove(question);
        question.getBanks().remove(this);
    }
}
