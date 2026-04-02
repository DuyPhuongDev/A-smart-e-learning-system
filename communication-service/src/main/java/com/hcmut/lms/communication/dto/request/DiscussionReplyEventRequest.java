package com.hcmut.lms.communication.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DiscussionReplyEventRequest {
    private String eventId;
    private String classId;
    private String courseId;
    private String userId;
    private List<String> userIds;
    private String replyByUserId;
    private String replyContent;
}
