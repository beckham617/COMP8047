package com.comp8047.majorproject.travelplanassistant.dto;

import java.time.LocalDateTime;

public class PollVoteResponse {
    
    private Long id;
    private String optionText;
    private Long optionId;
    private LocalDateTime votedAt;
    
    // Constructors
    public PollVoteResponse() {}
    
    public PollVoteResponse(Long id, String optionText, Long optionId, LocalDateTime votedAt) {
        this.id = id;
        this.optionText = optionText;
        this.optionId = optionId;
        this.votedAt = votedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getOptionText() {
        return optionText;
    }
    
    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }
    
    public Long getOptionId() {
        return optionId;
    }
    
    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }
    
    public LocalDateTime getVotedAt() {
        return votedAt;
    }
    
    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
}
