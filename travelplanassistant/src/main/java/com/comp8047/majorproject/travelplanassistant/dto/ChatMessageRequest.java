package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.ChatMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ChatMessageRequest {
    
    @NotBlank(message = "Message content is required")
    private String content;
    
    @NotNull(message = "Message type is required")
    private ChatMessage.MessageType messageType;
    
    // Constructors
    public ChatMessageRequest() {}
    
    public ChatMessageRequest(String content, ChatMessage.MessageType messageType) {
        this.content = content;
        this.messageType = messageType;
    }
    
    // Getters and Setters
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public ChatMessage.MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(ChatMessage.MessageType messageType) {
        this.messageType = messageType;
    }
} 