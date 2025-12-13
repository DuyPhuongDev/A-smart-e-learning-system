package com.hcmut.lms.coursemanagement.domain.entity.lecture;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Table(name = "video_lectures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "lecture_id")
public class VideoLecture extends Lecture {
    
    @Column(name = "video_url")
    private String videoUrl;
    
    @Column(name = "duration")
    private Integer duration;
    
    @OneToMany(mappedBy = "videoLecture", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VideoTranscript> transcripts = new ArrayList<>();

    public VideoLecture(String title, String videoUrl, Integer duration) {
        this.setTitle(title);
        this.setLectureType(LectureType.VIDEO);
        this.videoUrl = videoUrl;
        this.duration = duration;
    }
}

