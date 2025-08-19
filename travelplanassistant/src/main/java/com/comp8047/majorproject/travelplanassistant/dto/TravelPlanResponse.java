package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import java.time.LocalDateTime;
import java.util.List;

public class TravelPlanResponse {
    private Long id;
    private String title;
    private TravelPlan.PlanType planType;
    private TravelPlan.Category category;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String originCountry;
    private String originCity;
    private String destinationCountry;
    private String destinationCity;
    private TravelPlan.TransportType transportation;
    private TravelPlan.AccommodationType accommodation;
    private Integer maxMembers;
    private String description;
    private List<String> images;
    private TravelPlan.GenderPreference gender;
    private Integer ageMin;
    private Integer ageMax;
    private String language;
    private TravelPlan.Status status;
    private Long ownerId;
    private String ownerName;
    private String ownerAvatar;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private Integer currentMemberCount;
    private UserPlanStatus.Status userPlanStatus;

    public TravelPlanResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TravelPlan.PlanType getPlanType() { return planType; }
    public void setPlanType(TravelPlan.PlanType planType) { this.planType = planType; }

    public TravelPlan.Category getCategory() { return category; }
    public void setCategory(TravelPlan.Category category) { this.category = category; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }

    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public TravelPlan.TransportType getTransportation() { return transportation; }
    public void setTransportation(TravelPlan.TransportType transportation) { this.transportation = transportation; }

    public TravelPlan.AccommodationType getAccommodation() { return accommodation; }
    public void setAccommodation(TravelPlan.AccommodationType accommodation) { this.accommodation = accommodation; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public TravelPlan.GenderPreference getGender() { return gender; }
    public void setGender(TravelPlan.GenderPreference gender) { this.gender = gender; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public TravelPlan.Status getStatus() { return status; }
    public void setStatus(TravelPlan.Status status) { this.status = status; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public String getOwnerAvatar() { return ownerAvatar; }
    public void setOwnerAvatar(String ownerAvatar) { this.ownerAvatar = ownerAvatar; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public Integer getCurrentMemberCount() { return currentMemberCount; }
    public void setCurrentMemberCount(Integer currentMemberCount) { this.currentMemberCount = currentMemberCount; }

    public UserPlanStatus.Status getUserPlanStatus() { return userPlanStatus; }
    public void setUserPlanStatus(UserPlanStatus.Status userPlanStatus) { this.userPlanStatus = userPlanStatus; }
} 