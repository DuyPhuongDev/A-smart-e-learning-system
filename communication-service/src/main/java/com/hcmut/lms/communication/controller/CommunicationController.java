package com.hcmut.lms.communication.controller;

import com.hcmut.lms.communication.dto.request.DiscussionReplyEventRequest;
import com.hcmut.lms.communication.dto.request.MaintenanceScheduleEventRequest;
import com.hcmut.lms.communication.event.CommunicationEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/communication")
@RequiredArgsConstructor
public class CommunicationController {

    private final CommunicationEventPublisher communicationEventPublisher;

    @PostMapping("/message")
    public String sendMessage() {
        // TODO: Implement send message logic
        return "Send message - To be implemented";
    }

    @GetMapping("/messages/{userId}")
    public String getUserMessages(@PathVariable String userId) {
        // TODO: Implement get user messages logic
        return "Get user messages - To be implemented";
    }

    @GetMapping("/conversation/{conversationId}")
    public String getConversation(@PathVariable String conversationId) {
        // TODO: Implement get conversation logic
        return "Get conversation - To be implemented";
    }

    @PostMapping("/forum")
    public String createForum() {
        // TODO: Implement create forum logic
        return "Create forum - To be implemented";
    }

    @PostMapping("/forum/{forumId}/post")
    public String createForumPost(@PathVariable String forumId) {
        // TODO: Implement create forum post logic
        return "Create forum post - To be implemented";
    }

    @GetMapping("/forum/{forumId}")
    public String getForumPosts(@PathVariable String forumId) {
        // TODO: Implement get forum posts logic
        return "Get forum posts - To be implemented";
    }

    @PostMapping("/forum/post/{postId}/reply")
    public Map<String, String> replyToPost(
            @PathVariable String postId,
            @RequestBody(required = false) DiscussionReplyEventRequest request
    ) {
        communicationEventPublisher.publishDiscussionReplyCreated(postId, request);
        return Map.of(
                "message", "Reply event accepted",
                "postId", postId,
                "eventType", "communication.discussion.reply.created"
        );
    }

    @PostMapping("/system/maintenance/schedule")
    public Map<String, String> publishMaintenanceSchedule(
            @RequestBody(required = false) MaintenanceScheduleEventRequest request
    ) {
        communicationEventPublisher.publishSystemMaintenanceScheduled(request);
        return Map.of(
                "message", "Maintenance schedule event accepted",
                "eventType", "system.maintenance.scheduled"
        );
    }
}
