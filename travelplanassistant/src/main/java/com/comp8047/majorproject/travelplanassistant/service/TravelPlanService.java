package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanCreateRequest;
import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanResponse;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TravelPlanService {
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private UserPlanStatusRepository userPlanStatusRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new travel plan
     */
    public TravelPlanResponse createTravelPlan(TravelPlanCreateRequest request, User owner) {
        // Check if user already has a current plan
        if (travelPlanRepository.userHasCurrentPlan(owner.getId())) {
            throw new IllegalStateException("User already has a current travel plan");
        }
        
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setTitle(request.getTitle());
        travelPlan.setDescription(request.getDescription());
        travelPlan.setPlanType(request.getPlanType());
        travelPlan.setOriginLocation(request.getOriginLocation());
        travelPlan.setDestination(request.getDestination());
        travelPlan.setDestinationTimezone(request.getDestinationTimezone());
        travelPlan.setStartDate(request.getStartDate());
        travelPlan.setEndDate(request.getEndDate());
        travelPlan.setTransportType(request.getTransportType());
        travelPlan.setAccommodationType(request.getAccommodationType());
        travelPlan.setAccommodationDetails(request.getAccommodationDetails());
        travelPlan.setEstimatedBudget(request.getEstimatedBudget());
        travelPlan.setOptionalExpenses(request.getOptionalExpenses());
        travelPlan.setMaxMembers(request.getMaxMembers());
        travelPlan.setMinMembers(request.getMinMembers());
        travelPlan.setGenderPreference(request.getGenderPreference());
        travelPlan.setMinAge(request.getMinAge());
        travelPlan.setMaxAge(request.getMaxAge());
        travelPlan.setRequiredLanguages(request.getRequiredLanguages());
        travelPlan.setCommunicationLanguages(request.getCommunicationLanguages());
        travelPlan.setVisibility(request.getVisibility());
        travelPlan.setOwner(owner);
        
        TravelPlan savedPlan = travelPlanRepository.save(travelPlan);
        
        // Create user plan status for owner
        UserPlanStatus ownerStatus = new UserPlanStatus(owner, savedPlan, UserPlanStatus.Status.OWNED);
        userPlanStatusRepository.save(ownerStatus);
        
        return convertToResponse(savedPlan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Get all public new plans not associated with the current user
     */
    public List<TravelPlanResponse> getPublicNewPlans(Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findPublicNewPlansNotAssociatedWithUser(userId);
        return plans.stream()
                .map(plan -> convertToResponse(plan, null))
                .collect(Collectors.toList());
    }
    
    /**
     * Search public plans by keyword
     */
    public List<TravelPlanResponse> searchPublicPlans(String keyword, Long userId) {
        List<TravelPlan> plans = travelPlanRepository.searchPublicPlans(keyword, userId);
        return plans.stream()
                .map(plan -> convertToResponse(plan, null))
                .collect(Collectors.toList());
    }
    
    /**
     * Get current plans for a user
     */
    public List<TravelPlanResponse> getCurrentPlans(Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findCurrentPlansForUser(userId);
        return plans.stream()
                .map(plan -> {
                    UserPlanStatus userStatus = userPlanStatusRepository.findByUserAndTravelPlan(
                            userRepository.findById(userId).orElse(null), plan).orElse(null);
                    return convertToResponse(plan, userStatus != null ? userStatus.getStatus() : null);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get history plans for a user
     */
    public List<TravelPlanResponse> getHistoryPlans(Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findHistoryPlansForUser(userId);
        return plans.stream()
                .map(plan -> {
                    UserPlanStatus userStatus = userPlanStatusRepository.findByUserAndTravelPlan(
                            userRepository.findById(userId).orElse(null), plan).orElse(null);
                    return convertToResponse(plan, userStatus != null ? userStatus.getStatus() : null);
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Get travel plan by ID
     */
    public TravelPlanResponse getTravelPlanById(Long planId, Long userId) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        UserPlanStatus userStatus = userPlanStatusRepository.findByUserAndTravelPlan(
                userRepository.findById(userId).orElse(null), plan).orElse(null);
        
        return convertToResponse(plan, userStatus != null ? userStatus.getStatus() : null);
    }
    
    /**
     * Apply to a travel plan
     */
    public TravelPlanResponse applyToPlan(Long planId, User user) {
        // Check if user already has a current plan
        if (travelPlanRepository.userHasCurrentPlan(user.getId())) {
            throw new IllegalStateException("User already has a current travel plan");
        }
        
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if plan is public and new
        if (plan.getVisibility() != TravelPlan.Visibility.PUBLIC || 
            plan.getPlanStatus() != TravelPlan.PlanStatus.NEW) {
            throw new IllegalStateException("Plan is not available for application");
        }
        
        // Check if user already has a status for this plan
        Optional<UserPlanStatus> existingStatus = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        if (existingStatus.isPresent()) {
            throw new IllegalStateException("User already has a relationship with this plan");
        }
        
        // Create application
        UserPlanStatus application = new UserPlanStatus(user, plan, UserPlanStatus.Status.APPLIED);
        userPlanStatusRepository.save(application);
        
        return convertToResponse(plan, UserPlanStatus.Status.APPLIED);
    }
    
    /**
     * Cancel application to a travel plan
     */
    public TravelPlanResponse cancelApplication(Long planId, User user) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        
        if (userStatusOpt.isEmpty()) {
            throw new IllegalArgumentException("No application found for this plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (userStatus.getStatus() != UserPlanStatus.Status.APPLIED) {
            throw new IllegalStateException("Can only cancel pending applications");
        }
        
        userStatus.setStatus(UserPlanStatus.Status.APPLIED_CANCELLED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan, UserPlanStatus.Status.APPLIED_CANCELLED);
    }
    
    /**
     * Accept application to a travel plan (owner only)
     */
    public TravelPlanResponse acceptApplication(Long planId, Long applicantId, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can accept applications");
        }
        
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Applicant not found"));
        
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(applicant, plan);
        if (userStatusOpt.isEmpty() || userStatusOpt.get().getStatus() != UserPlanStatus.Status.APPLIED) {
            throw new IllegalStateException("No pending application found");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        userStatus.setStatus(UserPlanStatus.Status.APPLIED_ACCEPTED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Refuse application to a travel plan (owner only)
     */
    public TravelPlanResponse refuseApplication(Long planId, Long applicantId, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can refuse applications");
        }
        
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Applicant not found"));
        
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(applicant, plan);
        if (userStatusOpt.isEmpty() || userStatusOpt.get().getStatus() != UserPlanStatus.Status.APPLIED) {
            throw new IllegalStateException("No pending application found");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        userStatus.setStatus(UserPlanStatus.Status.APPLIED_REFUSED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Invite user to travel plan (owner only)
     */
    public TravelPlanResponse inviteUser(Long planId, String inviteeEmail, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can send invitations");
        }
        
        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if invitee already has a current plan
        if (travelPlanRepository.userHasCurrentPlan(invitee.getId())) {
            throw new IllegalStateException("Invitee already has a current travel plan");
        }
        
        // Check if invitee already has a status for this plan
        Optional<UserPlanStatus> existingStatus = userPlanStatusRepository.findByUserAndTravelPlan(invitee, plan);
        if (existingStatus.isPresent()) {
            throw new IllegalStateException("User already has a relationship with this plan");
        }
        
        // Create invitation
        UserPlanStatus invitation = new UserPlanStatus(invitee, plan, UserPlanStatus.Status.INVITED);
        userPlanStatusRepository.save(invitation);
        
        return convertToResponse(plan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Accept invitation to travel plan
     */
    public TravelPlanResponse acceptInvitation(Long planId, User user) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        
        if (userStatusOpt.isEmpty()) {
            throw new IllegalArgumentException("No invitation found for this plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (userStatus.getStatus() != UserPlanStatus.Status.INVITED) {
            throw new IllegalStateException("Can only accept pending invitations");
        }
        
        userStatus.setStatus(UserPlanStatus.Status.INVITED_ACCEPTED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan, UserPlanStatus.Status.INVITED_ACCEPTED);
    }
    
    /**
     * Refuse invitation to travel plan
     */
    public TravelPlanResponse refuseInvitation(Long planId, User user) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        
        if (userStatusOpt.isEmpty()) {
            throw new IllegalArgumentException("No invitation found for this plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (userStatus.getStatus() != UserPlanStatus.Status.INVITED) {
            throw new IllegalStateException("Can only refuse pending invitations");
        }
        
        userStatus.setStatus(UserPlanStatus.Status.INVITED_REFUSED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan, UserPlanStatus.Status.INVITED_REFUSED);
    }
    
    /**
     * Cancel travel plan (owner only)
     */
    public TravelPlanResponse cancelPlan(Long planId, String reason, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can cancel the plan");
        }
        
        plan.setPlanStatus(TravelPlan.PlanStatus.CANCELLED);
        plan.setCancelledAt(LocalDateTime.now());
        plan.setCancellationReason(reason);
        travelPlanRepository.save(plan);
        
        return convertToResponse(plan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Start travel plan (owner only)
     */
    public TravelPlanResponse startPlan(Long planId, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can start the plan");
        }
        
        if (plan.getPlanStatus() != TravelPlan.PlanStatus.NEW) {
            throw new IllegalStateException("Plan is not in NEW status");
        }
        
        plan.setPlanStatus(TravelPlan.PlanStatus.IN_PROGRESS);
        plan.setStartedAt(LocalDateTime.now());
        travelPlanRepository.save(plan);
        
        return convertToResponse(plan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Complete travel plan (owner only)
     */
    public TravelPlanResponse completePlan(Long planId, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can complete the plan");
        }
        
        if (plan.getPlanStatus() != TravelPlan.PlanStatus.IN_PROGRESS) {
            throw new IllegalStateException("Plan is not in progress");
        }
        
        plan.setPlanStatus(TravelPlan.PlanStatus.COMPLETED);
        plan.setCompletedAt(LocalDateTime.now());
        travelPlanRepository.save(plan);
        
        return convertToResponse(plan, UserPlanStatus.Status.OWNED);
    }
    
    /**
     * Check if user has current plan
     */
    public boolean userHasCurrentPlan(Long userId) {
        return travelPlanRepository.userHasCurrentPlan(userId);
    }
    
    /**
     * Convert TravelPlan to TravelPlanResponse
     */
    private TravelPlanResponse convertToResponse(TravelPlan plan, UserPlanStatus.Status userStatus) {
        TravelPlanResponse response = new TravelPlanResponse();
        response.setId(plan.getId());
        response.setTitle(plan.getTitle());
        response.setDescription(plan.getDescription());
        response.setPlanType(plan.getPlanType());
        response.setOriginLocation(plan.getOriginLocation());
        response.setDestination(plan.getDestination());
        response.setDestinationTimezone(plan.getDestinationTimezone());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setTransportType(plan.getTransportType());
        response.setAccommodationType(plan.getAccommodationType());
        response.setAccommodationDetails(plan.getAccommodationDetails());
        response.setEstimatedBudget(plan.getEstimatedBudget());
        response.setOptionalExpenses(plan.getOptionalExpenses());
        response.setMaxMembers(plan.getMaxMembers());
        response.setMinMembers(plan.getMinMembers());
        response.setGenderPreference(plan.getGenderPreference());
        response.setMinAge(plan.getMinAge());
        response.setMaxAge(plan.getMaxAge());
        response.setRequiredLanguages(plan.getRequiredLanguages());
        response.setCommunicationLanguages(plan.getCommunicationLanguages());
        response.setPlanStatus(plan.getPlanStatus());
        response.setVisibility(plan.getVisibility());
        response.setOwnerId(plan.getOwner().getId());
        response.setOwnerName(plan.getOwner().getFirstName() + " " + plan.getOwner().getLastName());
        response.setOwnerAvatar(plan.getOwner().getAvatar());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        response.setStartedAt(plan.getStartedAt());
        response.setCompletedAt(plan.getCompletedAt());
        response.setCancelledAt(plan.getCancelledAt());
        response.setCancellationReason(plan.getCancellationReason());
        response.setCurrentMemberCount(plan.getCurrentMemberCount());
        response.setUserStatus(userStatus);
        
        return response;
    }
} 