package com.hcmut.lms.communication.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

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
    public String replyToPost(@PathVariable String postId) {
        // TODO: Implement reply to post logic
        return "Reply to post - To be implemented";
    }
}
