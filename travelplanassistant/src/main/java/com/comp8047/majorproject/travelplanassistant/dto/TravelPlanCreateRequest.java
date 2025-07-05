package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TravelPlanCreateRequest {
    
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;
    
    @NotNull(message = "Plan type is required")
    private TravelPlan.PlanType planType;
    
    private String originLocation;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    private String destinationTimezone;
    
    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    private LocalDateTime endDate;
    
    private TravelPlan.TransportType transportType;
    
    private String accommodationType;
    
    private String accommodationDetails;
    
    @Positive(message = "Budget must be positive")
    private BigDecimal estimatedBudget;
    
    private String optionalExpenses;
    
    @Positive(message = "Maximum members must be positive")
    private Integer maxMembers;
    
    private Integer minMembers = 1;
    
    private TravelPlan.GenderPreference genderPreference;
    
    private Integer minAge;
    
    private Integer maxAge;
    
    private String requiredLanguages;
    
    private String communicationLanguages;
    
    @NotNull(message = "Visibility is required")
    private TravelPlan.Visibility visibility;
    
    // Constructors
    public TravelPlanCreateRequest() {}
    
    // Getters and Setters
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
    
    public TravelPlan.Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(TravelPlan.Visibility visibility) {
        this.visibility = visibility;
    }
} 