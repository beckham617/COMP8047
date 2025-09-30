package com.comp8047.majorproject.travelplanassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PollRequest {
    
    @NotBlank(message = "Poll question is required")
    @Size(max = 500, message = "Question must be less than 500 characters")
    private String question;
    
    @NotEmpty(message = "Poll options are required")
    @Size(min = 2, max = 10, message = "Poll must have between 2 and 10 options")
    private List<String> options;
    
    // Constructors
    public PollRequest() {}
    
    public PollRequest(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }
    
    // Getters and Setters
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public List<String> getOptions() {
        return options;
    }
    
    public void setOptions(List<String> options) {
        this.options = options;
    }
}