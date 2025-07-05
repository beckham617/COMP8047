package com.comp8047.majorproject.travelplanassistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_plan_status")
public class UserPlanStatus {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id", nullable = false)
    @NotNull(message = "Travel plan is required")
    private TravelPlan travelPlan;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    
    @Column(name = "applied_at")
    private LocalDateTime appliedAt;
    
    @Column(name = "invited_at")
    private LocalDateTime invitedAt;
    
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "refused_at")
    private LocalDateTime refusedAt;
    
    @Column(name = "response_message", length = 500)
    private String responseMessage;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserPlanStatus() {
        this.createdAt = LocalDateTime.now();
    }
    
    public UserPlanStatus(User user, TravelPlan travelPlan, Status status) {
        this();
        this.user = user;
        this.travelPlan = travelPlan;
        this.status = status;
        
        // Set appropriate timestamp based on status
        switch (status) {
            case APPLIED:
                this.appliedAt = LocalDateTime.now();
                break;
            case INVITED:
                this.invitedAt = LocalDateTime.now();
                break;
            case APPLIED_CANCELLED:
                this.refusedAt = LocalDateTime.now();
                break;
            case APPLIED_ACCEPTED:
                this.acceptedAt = LocalDateTime.now();
                break;
            case APPLIED_REFUSED:
                this.refusedAt = LocalDateTime.now();
                break;
            case INVITED_ACCEPTED:
                this.acceptedAt = LocalDateTime.now();
                break;
            case INVITED_REFUSED:
                this.refusedAt = LocalDateTime.now();
                break;
            default:
                break;
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public TravelPlan getTravelPlan() {
        return travelPlan;
    }
    
    public void setTravelPlan(TravelPlan travelPlan) {
        this.travelPlan = travelPlan;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        
        // Set appropriate timestamp based on status
        switch (status) {
            case OWNED:
                this.acceptedAt = LocalDateTime.now();
                break;
            case APPLIED:
                this.appliedAt = LocalDateTime.now();
                break;
            case INVITED:
                this.invitedAt = LocalDateTime.now();
                break;
            case APPLIED_CANCELLED:
                this.refusedAt = LocalDateTime.now();
                break;
            case APPLIED_ACCEPTED:
                this.acceptedAt = LocalDateTime.now();
                break;
            case APPLIED_REFUSED:
                this.refusedAt = LocalDateTime.now();
                break;
            case INVITED_ACCEPTED:
                this.acceptedAt = LocalDateTime.now();
                break;
            case INVITED_REFUSED:
                this.refusedAt = LocalDateTime.now();
                break;
            default:
                break;
        }
    }
    
    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }
    
    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
    
    public LocalDateTime getInvitedAt() {
        return invitedAt;
    }
    
    public void setInvitedAt(LocalDateTime invitedAt) {
        this.invitedAt = invitedAt;
    }
    
    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }
    
    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }
    
    public LocalDateTime getRefusedAt() {
        return refusedAt;
    }
    
    public void setRefusedAt(LocalDateTime refusedAt) {
        this.refusedAt = refusedAt;
    }
    
    public String getResponseMessage() {
        return responseMessage;
    }
    
    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
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
    
    // Helper methods
    public boolean isOwner() {
        return status == Status.OWNED;
    }
    
    public boolean isApplied() {
        return status == Status.APPLIED;
    }
    
    public boolean isInvited() {
        return status == Status.INVITED;
    }
    
    public boolean isAppliedAccepted() {
        return status == Status.APPLIED_ACCEPTED;
    }
    
    public boolean isAppliedRefused() {
        return status == Status.APPLIED_REFUSED;
    }
    
    public boolean isInvitedAccepted() {
        return status == Status.INVITED_ACCEPTED;
    }
    
    public boolean isInvitedRefused() {
    
    public boolean isActive() {
        return status == Status.OWNED || status == Status.ACCEPTED;
    }
    
    public boolean isPending() {
        return status == Status.APPLIED || status == Status.INVITED;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Status enum
    public enum Status {
        OWNED,      // User owns/created the travel plan
        APPLIED,    // User applied to join the travel plan
        APPLIED_CANCELLED,    // User applied to join the travel plan, and the applicant cancelled the application
        APPLIED_ACCEPTED,    // User applied to join the travel plan, and the application was accepted
        APPLIED_REFUSED,    // User applied to join the travel plan, and the application was refused
        INVITED,    // User was invited to join the travel plan
        INVITED_ACCEPTED,    // User was invited to join the travel plan, and the invitee has accepted the invitation
        INVITED_REFUSED    // User was invited to join the travel plan, and the invitee has refused the invitation
    }
} 