package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanRequest;
import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanResponse;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public TravelPlanService(TravelPlanRepository travelPlanRepository, 
    UserPlanStatusRepository userPlanStatusRepository,
    UserRepository userRepository,
    ObjectMapper objectMapper) {
        this.travelPlanRepository = travelPlanRepository;
        this.userPlanStatusRepository = userPlanStatusRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Create a new travel plan
     */
    public TravelPlanResponse createTravelPlan(TravelPlanRequest request, User owner) {
        // Check if user already has a current plan
        if (travelPlanRepository.userHasCurrentPlan(owner.getId())) {
            throw new IllegalStateException("User already has a current travel plan");
        }
        
        TravelPlan travelPlan = new TravelPlan();
        travelPlan.setTitle(request.getTitle());
        travelPlan.setPlanType(request.getPlanType());
        travelPlan.setCategory(request.getCategory());
        travelPlan.setStartDate(request.getStartDate().atStartOfDay());
        travelPlan.setEndDate(request.getEndDate().atTime(java.time.LocalTime.MAX));
        travelPlan.setOriginCountry(request.getOriginCountry());
        travelPlan.setOriginCity(request.getOriginCity());
        travelPlan.setDestinationCountry(request.getDestinationCountry());
        travelPlan.setDestinationCity(request.getDestinationCity());
        travelPlan.setTransportation(request.getTransportation());
        travelPlan.setAccommodation(request.getAccommodation());
        travelPlan.setMaxMembers(request.getMaxMembers());
        travelPlan.setDescription(request.getDescription());
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            try {
                String imagesJson = objectMapper.writeValueAsString(request.getImages());
                travelPlan.setImages(imagesJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize images", e);
            }
        } else {
            travelPlan.setImages("[]");
        }
        travelPlan.setGender(request.getGender());
        travelPlan.setAgeMin(request.getAgeMin());
        travelPlan.setAgeMax(request.getAgeMax());
        travelPlan.setLanguage(request.getLanguage());
        travelPlan.setStatus(TravelPlan.Status.NEW);
        travelPlan.setOwner(owner);
        
        TravelPlan savedPlan = travelPlanRepository.save(travelPlan);
        
        // Create user plan status for owner
        UserPlanStatus ownerStatus = new UserPlanStatus(owner, savedPlan, UserPlanStatus.Status.OWNED);
        userPlanStatusRepository.save(ownerStatus);
        
        savedPlan.setUserPlanStatuses(new ArrayList<>(Arrays.asList(ownerStatus)));

        return convertToResponse(savedPlan);
    }
    
    /**
     * Get all new public plans (planType = PUBLIC, status = NEW) not associated with the current user
     */
    public List<TravelPlanResponse> getPublicNewPlans(Long userId) {
        // Find all plans with planType PUBLIC and status NEW, and filter out those where user is already a member
        List<TravelPlan> plans = travelPlanRepository.findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW);
        return plans.stream()
                .filter(plan -> userPlanStatusRepository.findByUserAndTravelPlan(userRepository.findById(userId).orElse(null), plan).isEmpty())
                .map(plan -> convertToResponse(plan))
                .collect(Collectors.toList());
    }

    /**
     * Search public plans by keyword (planType = PUBLIC, status = NEW)
     */
    public List<TravelPlanResponse> searchPublicPlans(String keyword, Long userId) {
        List<TravelPlan> plans = travelPlanRepository.findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW)
                .stream()
                .filter(plan -> (plan.getTitle() != null && plan.getTitle().toLowerCase().contains(keyword.toLowerCase()))
                        || (plan.getDescription() != null && plan.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .filter(plan -> userPlanStatusRepository.findByUserAndTravelPlan(userRepository.findById(userId).orElse(null), plan).isEmpty())
                .collect(Collectors.toList());
        return plans.stream()
                .map(plan -> convertToResponse(plan))
                .collect(Collectors.toList());
    }

    /**
     * Get current plans for a user (status NEW or IN_PROGRESS)
     */
    public List<TravelPlanResponse> getCurrentPlans(Long userId) {
        List<TravelPlan> travelPlans = travelPlanRepository.getCurrentPlans(userId);
        return travelPlans.stream().map(tp -> convertToResponse(tp)).collect(Collectors.toList());
    }

    /**
     * Get history plans for a user (status COMPLETED or CANCELLED)
     */
    public List<TravelPlanResponse> getHistoryPlans(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<UserPlanStatus> userStatuses = userPlanStatusRepository.findByUser(user);
        return userStatuses.stream()
                .filter(ups -> ups.getTravelPlan().getStatus() == TravelPlan.Status.COMPLETED || ups.getTravelPlan().getStatus() == TravelPlan.Status.CANCELLED)
                .map(ups -> convertToResponse(ups.getTravelPlan()))
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
        
        return convertToResponse(plan);
    }
    
    /**
     * Apply to a travel plan
     */
    public TravelPlanResponse applyToPlan(Long planId, User user) {
        // Check if user already has a current plan
        if (userHasCurrentPlan(user.getId())) {
            throw new IllegalStateException("User already has a current travel plan");
        }
        
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if plan is public and new
        if (plan.getPlanType() != TravelPlan.PlanType.PUBLIC || plan.getStatus() != TravelPlan.Status.NEW) {
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
        
        return convertToResponse(plan);
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
        
        userPlanStatusRepository.delete(userStatus);
        
        return convertToResponse(plan);
    }
    
    /**
     * Accept application to travel plan (owner only)
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
        if (userStatusOpt.isEmpty()) {
            throw new IllegalArgumentException("No application found for this plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (userStatus.getStatus() != UserPlanStatus.Status.APPLIED) {
            throw new IllegalStateException("Can only accept pending applications");
        }
        
        userStatus.setStatus(UserPlanStatus.Status.APPLIED_ACCEPTED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan);
    }
    
    /**
     * Reject application to travel plan (owner only)
     */
    public TravelPlanResponse rejectApplication(Long planId, Long applicantId, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can reject applications");
        }
        
        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Applicant not found"));
        
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(applicant, plan);
        if (userStatusOpt.isEmpty()) {
            throw new IllegalArgumentException("No application found for this plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (userStatus.getStatus() != UserPlanStatus.Status.APPLIED) {
            throw new IllegalStateException("Can only reject pending applications");
        }
        
        userStatus.setStatus(UserPlanStatus.Status.APPLIED_REFUSED);
        userPlanStatusRepository.save(userStatus);
        
        return convertToResponse(plan);
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
        
        return convertToResponse(plan);
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
        
        return convertToResponse(plan);
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
        
        return convertToResponse(plan);
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
        
        plan.setStatus(TravelPlan.Status.CANCELLED);
        plan.setCancelledAt(LocalDateTime.now());
        plan.setCancellationReason(reason);
        travelPlanRepository.save(plan);
        
        return convertToResponse(plan);
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
        
        if (plan.getStatus() != TravelPlan.Status.NEW) {
            throw new IllegalStateException("Plan is not in NEW status");
        }
        
        plan.setStatus(TravelPlan.Status.IN_PROGRESS);
        // plan.setStartedAt(LocalDateTime.now()); // Remove, no such field
        travelPlanRepository.save(plan);
        
        return convertToResponse(plan);
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
        
        if (plan.getStatus() != TravelPlan.Status.IN_PROGRESS) {
            throw new IllegalStateException("Plan is not in progress");
        }
        
        plan.setStatus(TravelPlan.Status.COMPLETED);
        // plan.setCompletedAt(LocalDateTime.now()); // Remove, no such field
        travelPlanRepository.save(plan);
        
        return convertToResponse(plan);
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
    private TravelPlanResponse convertToResponse(TravelPlan plan) {
        TravelPlanResponse response = new TravelPlanResponse();
        response.setId(plan.getId());
        response.setTitle(plan.getTitle());
        response.setPlanType(plan.getPlanType());
        response.setCategory(plan.getCategory());
        response.setStartDate(plan.getStartDate());
        response.setEndDate(plan.getEndDate());
        response.setOriginCountry(plan.getOriginCountry());
        response.setOriginCity(plan.getOriginCity());
        response.setDestinationCountry(plan.getDestinationCountry());
        response.setDestinationCity(plan.getDestinationCity());
        response.setTransportation(plan.getTransportation());
        response.setAccommodation(plan.getAccommodation());
        response.setMaxMembers(plan.getMaxMembers());
        response.setDescription(plan.getDescription());
        if (plan.getImages() != null && !plan.getImages().isEmpty()) {
            try {
                List<String> images = objectMapper.readValue(plan.getImages(), new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {});
                response.setImages(images);
            } catch (JsonProcessingException e) {
                response.setImages(List.of());
            }
        } else {
            response.setImages(List.of());
        }
        response.setGender(plan.getGender());
        response.setAgeMin(plan.getAgeMin());
        response.setAgeMax(plan.getAgeMax());
        response.setLanguage(plan.getLanguage());
        response.setStatus(plan.getStatus());
        response.setOwnerId(plan.getOwner().getId());
        response.setOwnerName(plan.getOwner().getFirstName() + " " + plan.getOwner().getLastName());
        response.setOwnerAvatar(plan.getOwner().getProfilePicture());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());
        response.setCancelledAt(plan.getCancelledAt());
        response.setCancellationReason(plan.getCancellationReason());
        response.setCurrentMemberCount(plan.getCurrentMemberCount());
        response.setUserPlanStatus(plan.getUserPlanStatuses().get(0).getStatus());
        return response;
    }
} 