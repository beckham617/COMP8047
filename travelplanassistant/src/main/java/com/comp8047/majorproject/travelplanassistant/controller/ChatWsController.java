package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.ChatMessageRequest;
import com.comp8047.majorproject.travelplanassistant.dto.ChatMessageResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWsController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Client sends to /app/chat/{planId}
    @MessageMapping("/chat/{planId}")
    public void handleChatMessage(@DestinationVariable Long planId,
                                  @Payload ChatMessageRequest request,
                                  @Header("user") User user) {
        if (user != null) {
            ChatMessageResponse response = chatService.sendMessage(planId, request, user);
            // Broadcast to subscribers of /topic/chat/{planId}
            messagingTemplate.convertAndSend("/topic/chat/" + planId, response);
        } else {
            // Handle unauthenticated user
            throw new IllegalStateException("User not authenticated");
        }
    }
}


