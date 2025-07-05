package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.ChatMessage;

import java.time.LocalDateTime;

public class ChatMessageResponse {
    
    private Long id;
    private String content;
    private ChatMessage.MessageType messageType;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long travelPlanId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public ChatMessageResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserAvatar() {
        return userAvatar;
    }
    
    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }
    
    public Long getTravelPlanId() {
        return travelPlanId;
    }
    
    public void setTravelPlanId(Long travelPlanId) {
        this.travelPlanId = travelPlanId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 