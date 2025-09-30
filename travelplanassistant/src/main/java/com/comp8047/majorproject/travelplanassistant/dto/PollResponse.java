package com.comp8047.majorproject.travelplanassistant.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class PollResponse {
    
    private Long id;
    private String question;
    private Map<Long, String> options;
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
    private Long travelPlanId;
    private Boolean isActive;
    private Map<String, Integer> voteCounts;
    private PollVoteResponse userVote; // The current user's vote with full details
    private Boolean hasUserVoted; // Whether the current user has voted on this poll
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public PollResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public Map<Long, String> getOptions() {
        return options;
    }
    
    public void setOptions(Map<Long, String> options) {
        this.options = options;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorName() {
        return creatorName;
    }
    
    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
    
    public String getCreatorAvatar() {
        return creatorAvatar;
    }
    
    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }
    
    public Long getTravelPlanId() {
        return travelPlanId;
    }
    
    public void setTravelPlanId(Long travelPlanId) {
        this.travelPlanId = travelPlanId;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Map<String, Integer> getVoteCounts() {
        return voteCounts;
    }
    
    public void setVoteCounts(Map<String, Integer> voteCounts) {
        this.voteCounts = voteCounts;
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
    
    public PollVoteResponse getUserVote() {
        return userVote;
    }
    
    public void setUserVote(PollVoteResponse userVote) {
        this.userVote = userVote;
    }
    
    public Boolean getHasUserVoted() {
        return hasUserVoted;
    }
    
    public void setHasUserVoted(Boolean hasUserVoted) {
        this.hasUserVoted = hasUserVoted;
    }
} 