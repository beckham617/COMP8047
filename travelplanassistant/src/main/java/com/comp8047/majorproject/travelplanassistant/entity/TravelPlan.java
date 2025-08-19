package com.comp8047.majorproject.travelplanassistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travel_plans")
public class TravelPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    @Column(nullable = false)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "origin_country")
    private String originCountry;

    @Column(name = "origin_city")
    private String originCity;

    @Column(name = "destination_country")
    private String destinationCountry;

    @Column(name = "destination_city")
    private String destinationCity;

    @Enumerated(EnumType.STRING)
    @Column(name = "transportation")
    private TransportType transportation;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation")
    private AccommodationType accommodation;

    @Positive(message = "Maximum members must be positive")
    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be less than 2000 characters")
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "images", columnDefinition = "JSON")
    private String images; // JSON array of image paths

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private GenderPreference gender;

    @Column(name = "age_min")
    private Integer ageMin;

    @Column(name = "age_max")
    private Integer ageMax;

    @Column(name = "language")
    private String language;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.NEW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // Relationships
    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserPlanStatus> userPlanStatuses = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Poll> polls = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SharedExpense> sharedExpenses = new ArrayList<>();

    // Constructors
    public TravelPlan() {
        this.createdAt = LocalDateTime.now();
    }

    public TravelPlan(String title, Category category, LocalDateTime startDate, LocalDateTime endDate, String originCountry, String originCity, String destinationCountry, String destinationCity, TransportType transportation, AccommodationType accommodation, Integer maxMembers, String description, String images, GenderPreference gender, Integer ageMin, Integer ageMax, String language, Status status, User owner) {
        this.title = title;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.originCountry = originCountry;
        this.originCity = originCity;
        this.destinationCountry = destinationCountry;
        this.destinationCity = destinationCity;
        this.transportation = transportation;
        this.accommodation = accommodation;
        this.maxMembers = maxMembers;
        this.description = description;
        this.images = images;
        this.gender = gender;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.language = language;
        this.status = status;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public PlanType getPlanType() { return planType; }
    public void setPlanType(PlanType planType) { this.planType = planType; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

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

    public TransportType getTransportation() { return transportation; }
    public void setTransportation(TransportType transportation) { this.transportation = transportation; }

    public AccommodationType getAccommodation() { return accommodation; }
    public void setAccommodation(AccommodationType accommodation) { this.accommodation = accommodation; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImages() { return images; }
    public void setImages(String images) { this.images = images; }

    public GenderPreference getGender() { return gender; }
    public void setGender(GenderPreference gender) { this.gender = gender; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }

    public List<UserPlanStatus> getUserPlanStatuses() { return userPlanStatuses; }
    public void setUserPlanStatuses(List<UserPlanStatus> userPlanStatuses) { this.userPlanStatuses = userPlanStatuses; }

    public List<ChatMessage> getChatMessages() { return chatMessages; }
    public void setChatMessages(List<ChatMessage> chatMessages) { this.chatMessages = chatMessages; }

    public List<Poll> getPolls() { return polls; }
    public void setPolls(List<Poll> polls) { this.polls = polls; }

    public List<SharedExpense> getSharedExpenses() { return sharedExpenses; }
    public void setSharedExpenses(List<SharedExpense> sharedExpenses) { this.sharedExpenses = sharedExpenses; }

    // Helper methods
    public boolean isActive() { return status == Status.IN_PROGRESS; }
    public boolean isPending() { return status == Status.NEW; }
    public boolean isCompleted() { return status == Status.COMPLETED; }
    public boolean isCancelled() { return status == Status.CANCELLED; }
    public int getCurrentMemberCount() {
        return (int) userPlanStatuses.stream()
                .filter(ups -> ups.getStatus() == UserPlanStatus.Status.OWNED || 
                              ups.getStatus() == UserPlanStatus.Status.APPLIED_ACCEPTED ||
                              ups.getStatus() == UserPlanStatus.Status.INVITED_ACCEPTED)
                .count();
    }
    public boolean isFull() { return getCurrentMemberCount() >= maxMembers; }
    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // Enums
    public enum PlanType {
        PUBLIC, PRIVATE
    }
    
    public enum Category {
        TRIP, SPORTS, GAME, MATCH, EVENT, CONCERT, SHOW, FAMILY_TIME
    }
    public enum TransportType {
        FLIGHT, RAILWAY, CAR, CRUISE, OTHER
    }
    public enum AccommodationType {
        HOTEL, HOSTEL, AIRBNB, CAMPING, OTHER
    }
    public enum GenderPreference {
        ANY, MALE, FEMALE, OTHER
    }
    public enum Status {
        NEW, IN_PROGRESS, COMPLETED, CANCELLED
    }
} 