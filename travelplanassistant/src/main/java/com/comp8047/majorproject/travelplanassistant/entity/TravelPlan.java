package com.comp8047.majorproject.travelplanassistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
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
    
    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be less than 2000 characters")
    @Column(nullable = false, length = 2000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;
    
    @Column(name = "origin_location")
    private String originLocation;
    
    @NotBlank(message = "Destination is required")
    @Column(nullable = false)
    private String destination;
    
    @Column(name = "destination_timezone")
    private String destinationTimezone;
    
    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    private TransportType transportType;
    
    @Column(name = "accommodation_type")
    private String accommodationType;
    
    @Column(name = "accommodation_details", length = 1000)
    private String accommodationDetails;
    
    @Positive(message = "Budget must be positive")
    @Column(precision = 10, scale = 2)
    private BigDecimal estimatedBudget;
    
    @Column(name = "optional_expenses", length = 1000)
    private String optionalExpenses;
    
    @Positive(message = "Maximum members must be positive")
    @Column(name = "max_members", nullable = false)
    private Integer maxMembers;
    
    @Column(name = "min_members")
    private Integer minMembers = 1;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "gender_preference")
    private GenderPreference genderPreference;
    
    @Column(name = "min_age")
    private Integer minAge;
    
    @Column(name = "max_age")
    private Integer maxAge;
    
    @Column(name = "required_languages")
    private String requiredLanguages;
    
    @Column(name = "communication_languages")
    private String communicationLanguages;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_status", nullable = false)
    private PlanStatus planStatus = PlanStatus.NEW;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility = Visibility.PUBLIC;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
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
    
    public TravelPlan(String title, String description, PlanType planType, String destination, 
                     LocalDateTime startDate, LocalDateTime endDate, Integer maxMembers, User owner) {
        this();
        this.title = title;
        this.description = description;
        this.planType = planType;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.maxMembers = maxMembers;
        this.owner = owner;
    }
    
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
    
    public PlanType getPlanType() {
        return planType;
    }
    
    public void setPlanType(PlanType planType) {
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
    
    public TransportType getTransportType() {
        return transportType;
    }
    
    public void setTransportType(TransportType transportType) {
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
    
    public GenderPreference getGenderPreference() {
        return genderPreference;
    }
    
    public void setGenderPreference(GenderPreference genderPreference) {
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
    
    public PlanStatus getPlanStatus() {
        return planStatus;
    }
    
    public void setPlanStatus(PlanStatus planStatus) {
        this.planStatus = planStatus;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }
    
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
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
    
    public List<UserPlanStatus> getUserPlanStatuses() {
        return userPlanStatuses;
    }
    
    public void setUserPlanStatuses(List<UserPlanStatus> userPlanStatuses) {
        this.userPlanStatuses = userPlanStatuses;
    }
    
    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }
    
    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
    
    public List<Poll> getPolls() {
        return polls;
    }
    
    public void setPolls(List<Poll> polls) {
        this.polls = polls;
    }
    
    public List<SharedExpense> getSharedExpenses() {
        return sharedExpenses;
    }
    
    public void setSharedExpenses(List<SharedExpense> sharedExpenses) {
        this.sharedExpenses = sharedExpenses;
    }
    
    // Helper methods
    public boolean isActive() {
        return planStatus == PlanStatus.IN_PROGRESS;
    }
    
    public boolean isPending() {
        return planStatus == PlanStatus.NEW;
    }
    
    public boolean isCompleted() {
        return planStatus == PlanStatus.COMPLETED;
    }
    
    public boolean isCancelled() {
        return planStatus == PlanStatus.CANCELLED;
    }
    
    public boolean isPublic() {
        return visibility == Visibility.PUBLIC;
    }
    
    public boolean isPrivate() {
        return visibility == Visibility.PRIVATE;
    }
    
    public int getCurrentMemberCount() {
        return (int) userPlanStatuses.stream()
                .filter(ups -> ups.getStatus() == UserPlanStatus.Status.OWNED || 
                              ups.getStatus() == UserPlanStatus.Status.ACCEPTED)
                .count();
    }
    
    public boolean isFull() {
        return getCurrentMemberCount() >= maxMembers;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum PlanType {
        TRIP, CONCERT, SPORTS_GAME, WEDDING, BUSINESS, OTHER
    }
    
    public enum TransportType {
        PLANE, TRAIN, BUS, CAR, BOAT, WALKING, OTHER
    }
    
    public enum GenderPreference {
        ANY, MALE_ONLY, FEMALE_ONLY, MIXED
    }
    
    public enum PlanStatus {
        NEW, IN_PROGRESS, COMPLETED, CANCELLED
    }
    
    public enum Visibility {
        PUBLIC, PRIVATE
    }
} 