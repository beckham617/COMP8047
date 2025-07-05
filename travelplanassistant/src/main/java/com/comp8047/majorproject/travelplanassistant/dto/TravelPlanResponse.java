package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TravelPlanResponse {
    
    private Long id;
    private String title;
    private String description;
    private TravelPlan.PlanType planType;
    private String originLocation;
    private String destination;
    private String destinationTimezone;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private TravelPlan.TransportType transportType;
    private String accommodationType;
    private String accommodationDetails;
    private BigDecimal estimatedBudget;
    private String optionalExpenses;
    private Integer maxMembers;
    private Integer minMembers;
    private TravelPlan.GenderPreference genderPreference;
    private Integer minAge;
    private Integer maxAge;
    private String requiredLanguages;
    private String communicationLanguages;
    private TravelPlan.PlanStatus planStatus;
    private TravelPlan.Visibility visibility;
    private Long ownerId;
    private String ownerName;
    private String ownerAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private Integer currentMemberCount;
    private UserPlanStatus.Status userStatus;
    
    // Constructors
    public TravelPlanResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public TravelPlan.PlanType getPlanType() {
        return planType;
    }
    
    public void setPlanType(TravelPlan.PlanType planType) {
        this.planType = planType;
    }
    
    public String getOriginLocation() {
        return originLocation;
    }
    
    public void setOriginLocation(String originLocation) {
        this.originLocation = originLocation;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public String getDestinationTimezone() {
        return destinationTimezone;
    }
    
    public void setDestinationTimezone(String destinationTimezone) {
        this.destinationTimezone = destinationTimezone;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public TravelPlan.TransportType getTransportType() {
        return transportType;
    }
    
    public void setTransportType(TravelPlan.TransportType transportType) {
        this.transportType = transportType;
    }
    
    public String getAccommodationType() {
        return accommodationType;
    }
    
    public void setAccommodationType(String accommodationType) {
        this.accommodationType = accommodationType;
    }
    
    public String getAccommodationDetails() {
        return accommodationDetails;
    }
    
    public void setAccommodationDetails(String accommodationDetails) {
        this.accommodationDetails = accommodationDetails;
    }
    
    public BigDecimal getEstimatedBudget() {
        return estimatedBudget;
    }
    
    public void setEstimatedBudget(BigDecimal estimatedBudget) {
        this.estimatedBudget = estimatedBudget;
    }
    
    public String getOptionalExpenses() {
        return optionalExpenses;
    }
    
    public void setOptionalExpenses(String optionalExpenses) {
        this.optionalExpenses = optionalExpenses;
    }
    
    public Integer getMaxMembers() {
        return maxMembers;
    }
    
    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }
    
    public Integer getMinMembers() {
        return minMembers;
    }
    
    public void setMinMembers(Integer minMembers) {
        this.minMembers = minMembers;
    }
    
    public TravelPlan.GenderPreference getGenderPreference() {
        return genderPreference;
    }
    
    public void setGenderPreference(TravelPlan.GenderPreference genderPreference) {
        this.genderPreference = genderPreference;
    }
    
    public Integer getMinAge() {
        return minAge;
    }
    
    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }
    
    public Integer getMaxAge() {
        return maxAge;
    }
    
    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }
    
    public String getRequiredLanguages() {
        return requiredLanguages;
    }
    
    public void setRequiredLanguages(String requiredLanguages) {
        this.requiredLanguages = requiredLanguages;
    }
    
    public String getCommunicationLanguages() {
        return communicationLanguages;
    }
    
    public void setCommunicationLanguages(String communicationLanguages) {
        this.communicationLanguages = communicationLanguages;
    }
    
    public TravelPlan.PlanStatus getPlanStatus() {
        return planStatus;
    }
    
    public void setPlanStatus(TravelPlan.PlanStatus planStatus) {
        this.planStatus = planStatus;
    }
    
    public TravelPlan.Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(TravelPlan.Visibility visibility) {
        this.visibility = visibility;
    }
    
    public Long getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getOwnerAvatar() {
        return ownerAvatar;
    }
    
    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
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
    
    public LocalDateTime getStartedAt() {
        return startedAt;
    }
    
    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }
    
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
    
    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
    
    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }
    
    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
    
    public String getCancellationReason() {
        return cancellationReason;
    }
    
    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
    
    public Integer getCurrentMemberCount() {
        return currentMemberCount;
    }
    
    public void setCurrentMemberCount(Integer currentMemberCount) {
        this.currentMemberCount = currentMemberCount;
    }
    
    public UserPlanStatus.Status getUserStatus() {
        return userStatus;
    }
    
    public void setUserStatus(UserPlanStatus.Status userStatus) {
        this.userStatus = userStatus;
    }
} 