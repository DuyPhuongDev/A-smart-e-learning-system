package com.hcmut.lms.communication.controller;

import com.hcmut.lms.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication")
public class CommunicationController {

    @PostMapping("/message")
    public ResponseDto<String> sendMessage() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Send message - To be implemented")
                .build();
    }

    @GetMapping("/messages/{userId}")
    public ResponseDto<String> getUserMessages(@PathVariable String userId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get user messages - To be implemented")
                .build();
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseDto<String> getConversation(@PathVariable String conversationId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get conversation - To be implemented")
                .build();
    }

    @PostMapping("/forum")
    public ResponseDto<String> createForum() {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create forum - To be implemented")
                .build();
    }

    @PostMapping("/forum/{forumId}/post")
    public ResponseDto<String> createForumPost(@PathVariable String forumId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Create forum post - To be implemented")
                .build();
    }

    @GetMapping("/forum/{forumId}")
    public ResponseDto<String> getForumPosts(@PathVariable String forumId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Get forum posts - To be implemented")
                .build();
    }

    @PostMapping("/forum/post/{postId}/reply")
    public ResponseDto<String> replyToPost(@PathVariable String postId) {
        return ResponseDto.<String>builder()
                .success(true)
                .message("Reply to post - To be implemented")
                .build();
    }
}

