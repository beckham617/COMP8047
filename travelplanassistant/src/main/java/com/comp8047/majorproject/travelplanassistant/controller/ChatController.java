package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.ChatMessageResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/{planId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long planId, @AuthenticationPrincipal User user) {
        try {
            List<ChatMessageResponse> messages = chatService.getMessages(planId, user);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{planId}/messages/recent")
    public ResponseEntity<?> getRecentMessages(@PathVariable Long planId, @RequestParam(defaultValue = "50") int limit, @AuthenticationPrincipal User user) {
        try {
            List<ChatMessageResponse> messages = chatService.getRecentMessages(planId, limit, user);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{planId}/messages/after")
    public ResponseEntity<?> getMessagesAfter(@PathVariable Long planId, @RequestParam("after") String afterIso, @AuthenticationPrincipal User user) {
        try {
            LocalDateTime after = LocalDateTime.parse(afterIso);
            List<ChatMessageResponse> messages = chatService.getMessagesAfter(planId, after, user);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId, @AuthenticationPrincipal User user) {
        try {
            chatService.deleteMessage(messageId, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}


