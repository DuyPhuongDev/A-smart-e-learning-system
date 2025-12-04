package com.hcmut.lms.coursemanagement.domain.entity.lecture;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "text_lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "lecture_id")
public class TextLecture extends Lecture {
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "word_count")
    private Integer wordCount;
    
    @Column(name = "format_type")
    private String formatType;

    public TextLecture(String title, String content, String formatType) {
        this.setTitle(title);
        this.setLectureType(LectureType.TEXT);
        this.content = content;
        this.formatType = formatType;
        this.wordCount = content != null ? content.split("\\s+").length : 0;
    }
}

