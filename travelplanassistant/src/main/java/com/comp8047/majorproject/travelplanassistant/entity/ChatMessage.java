package com.comp8047.majorproject.travelplanassistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Message content is required")
    @Column(nullable = false, length = 2000)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @NotNull(message = "Sender is required")
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id", nullable = false)
    @NotNull(message = "Travel plan is required")
    private TravelPlan travelPlan;
    
    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;
    
    @Column(name = "is_system_message")
    private Boolean isSystemMessage = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "edited_at")
    private LocalDateTime editedAt;
    
    @Column(name = "is_edited")
    private Boolean isEdited = false;
    
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    // Relationships for read receipts
    @OneToMany(mappedBy = "chatMessage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageReadReceipt> readReceipts = new ArrayList<>();
    
    // Constructors
    public ChatMessage() {
        this.createdAt = LocalDateTime.now();
    }
    
    public ChatMessage(String content, User sender, TravelPlan travelPlan) {
        this();
        this.content = content;
        this.sender = sender;
        this.travelPlan = travelPlan;
    }
    
    public ChatMessage(String content, User sender, TravelPlan travelPlan, MessageType messageType) {
        this(content, sender, travelPlan);
        this.messageType = messageType;
    }
    
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
        if (this.isEdited) {
            this.editedAt = LocalDateTime.now();
        }
    }
    
    public User getSender() {
        return sender;
    }
    
    public void setSender(User sender) {
        this.sender = sender;
    }
    
    public TravelPlan getTravelPlan() {
        return travelPlan;
    }
    
    public void setTravelPlan(TravelPlan travelPlan) {
        this.travelPlan = travelPlan;
    }
    
    public MessageType getMessageType() {
        return messageType;
    }
    
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
    
    public Boolean getIsSystemMessage() {
        return isSystemMessage;
    }
    
    public void setIsSystemMessage(Boolean isSystemMessage) {
        this.isSystemMessage = isSystemMessage;
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
    
    public LocalDateTime getEditedAt() {
        return editedAt;
    }
    
    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }
    
    public Boolean getIsEdited() {
        return isEdited;
    }
    
    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
        if (isEdited) {
            this.editedAt = LocalDateTime.now();
        }
    }
    
    public Boolean getIsDeleted() {
        return isDeleted;
    }
    
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
        if (isDeleted) {
            this.deletedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public List<MessageReadReceipt> getReadReceipts() {
        return readReceipts;
    }
    
    public void setReadReceipts(List<MessageReadReceipt> readReceipts) {
        this.readReceipts = readReceipts;
    }
    
    // Helper methods
    public boolean isTextMessage() {
        return messageType == MessageType.TEXT;
    }
    
    public boolean isSystemMessage() {
        return isSystemMessage;
    }
    
    public boolean isEdited() {
        return isEdited;
    }
    
    public boolean isDeleted() {
        return isDeleted;
    }
    
    public int getReadCount() {
        return readReceipts.size();
    }
    
    public boolean isReadByUser(User user) {
        return readReceipts.stream()
                .anyMatch(receipt -> receipt.getUser().getId().equals(user.getId()));
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Message type enum
    public enum MessageType {
        TEXT, SYSTEM, NOTIFICATION
    }
    
    // Inner class for read receipts
    @Entity
    @Table(name = "message_read_receipts")
    public static class MessageReadReceipt {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "message_id", nullable = false)
        private ChatMessage chatMessage;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        
        @Column(name = "read_at", nullable = false)
        private LocalDateTime readAt;
        
        public MessageReadReceipt() {
            this.readAt = LocalDateTime.now();
        }
        
        public MessageReadReceipt(ChatMessage chatMessage, User user) {
            this();
            this.chatMessage = chatMessage;
            this.user = user;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public ChatMessage getChatMessage() {
            return chatMessage;
        }
        
        public void setChatMessage(ChatMessage chatMessage) {
            this.chatMessage = chatMessage;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public LocalDateTime getReadAt() {
            return readAt;
        }
        
        public void setReadAt(LocalDateTime readAt) {
            this.readAt = readAt;
        }
    }
} 