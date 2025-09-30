package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanRequest;
import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanResponse;
import com.comp8047.majorproject.travelplanassistant.dto.MemberResponseDTO;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TravelPlanService {
    public enum Decision { ACCEPT, REFUSE }
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private UserPlanStatusRepository userPlanStatusRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private NotificationService notificationService;
    
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
        // Get current user profile for filtering
        User currentUser = userRepository.findById(userId).orElse(null);

        // Find all PUBLIC plans with status NEW
        List<TravelPlan> plans = travelPlanRepository.findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW);

        return plans.stream()
                // Exclude plans where the user already has a relationship/status
                .filter(plan -> userPlanStatusRepository.findByUserAndTravelPlan(currentUser, plan).isEmpty())
                // Exclude plans that have reached maxMembers
                .filter(plan -> !isPlanFull(plan))
                // Gender restriction: allow ANY/null or match user's gender
                .filter(plan -> {
                    if (plan.getGender() == null || plan.getGender() == TravelPlan.GenderPreference.ANY) return true;
                    if (currentUser == null || currentUser.getGender() == null) return true;
                    try {
                        return plan.getGender().name().equalsIgnoreCase(currentUser.getGender().name());
                    } catch (Exception e) {
                        return false;
                    }
                })
                // Age restriction: within [ageMin, ageMax] if specified
                .filter(plan -> {
                    Integer userAge = currentUser == null ? null : currentUser.getAge();
                    if (userAge == null) return true;
                    Integer min = plan.getAgeMin();
                    Integer max = plan.getAgeMax();
                    if (min != null && userAge < min) return false;
                    if (max != null && userAge > max) return false;
                    return true;
                })
                // Language restriction: allow null/blank or exact match (case-insensitive)
                .filter(plan -> {
                    String planLang = plan.getLanguage();
                    if (planLang == null || planLang.isBlank()) return true;
                    String userLang = currentUser == null ? null : currentUser.getLanguage();
                    return userLang != null && planLang.equalsIgnoreCase(userLang);
                })
                .map(plan -> {
                    TravelPlanResponse response = convertToResponse(plan);
                    // Populate members (include pending applications and invitations)
                    populateMembers(response, plan, true);
                    return response;
                })
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
                .filter(plan -> !isPlanFull(plan))
                .collect(Collectors.toList());
        return plans.stream()
                .map(plan -> {
                    TravelPlanResponse response = convertToResponse(plan);
                    // Populate members (include pending applications and invitations)
                    populateMembers(response, plan, true);
                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Get current plans for a user (status NEW or IN_PROGRESS)
     */
    public List<TravelPlanResponse> getCurrentPlans(Long userId) {
        List<TravelPlan> travelPlans = travelPlanRepository.getCurrentPlans(userId);
        User user = userRepository.findById(userId).orElse(null);
        return travelPlans.stream().map(plan -> {
            TravelPlanResponse response = convertToResponse(plan);
            UserPlanStatus ups = userPlanStatusRepository.findByUserAndTravelPlan(user, plan).orElse(null);
            
            // Populate members (include pending applications and invitations)
            populateMembers(response, plan, true);
            response.setUserPlanStatus(ups == null ? null : ups.getStatus());
            return response;
        }).collect(Collectors.toList());
    }

    /**
     * Get history plans for a user (status COMPLETED or CANCELLED)
     */
    public List<TravelPlanResponse> getHistoryPlans(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        List<UserPlanStatus> userStatuses = userPlanStatusRepository.findByUser(user);
        return userStatuses.stream()
                .filter(ups -> {
                    TravelPlan.Status planStatus = ups.getTravelPlan().getStatus();
                    UserPlanStatus.Status userStatus = ups.getStatus();
                    
                    // Only show COMPLETED or CANCELLED plans with specific user statuses
                    if (planStatus == TravelPlan.Status.COMPLETED || planStatus == TravelPlan.Status.CANCELLED) {
                        return userStatus == UserPlanStatus.Status.OWNED
                                || userStatus == UserPlanStatus.Status.APPLIED_ACCEPTED
                                || userStatus == UserPlanStatus.Status.INVITED_ACCEPTED;
                    }
                    return false;
                })
                .map(ups -> {
                    TravelPlanResponse response = convertToResponse(ups.getTravelPlan());
                    response.setUserPlanStatus(ups == null ? null : ups.getStatus());
                    // Populate members for history plans (only confirmed members)
                    populateMembers(response, ups.getTravelPlan(), false);
                    return response;
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
        
        boolean hasCurrentPlan = travelPlanRepository.userHasCurrentPlan(userId);

        TravelPlanResponse response = convertToResponse(plan);
        
        // Populate members (include pending applications and invitations)
        populateMembers(response, plan, true);
        response.setUserPlanStatus(userStatus == null ? null : userStatus.getStatus());
        response.setHasCurrentPlan(hasCurrentPlan);
        return response;
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
        
        // Check if plan has reached maxMembers
        if (isPlanFull(plan)) {
            throw new IllegalStateException("Travel plan has reached maximum number of members");
        }
        
        // Check if user already has a status for this plan
        Optional<UserPlanStatus> existingStatus = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        if (existingStatus.isPresent()) {
            throw new IllegalStateException("User already has a relationship with this plan");
        }
        
        // Create application
        UserPlanStatus application = new UserPlanStatus(user, plan, UserPlanStatus.Status.APPLIED);
        userPlanStatusRepository.save(application);
        
        TravelPlanResponse response = convertToResponse(plan);
        
        // Populate members (include pending applications and invitations)
        populateMembers(response, plan, true);
        return response;
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
        
        return convertToResponse(plan);
    }
    
    // Combined accept/reject application handler
    public TravelPlanResponse handleApplication(Long planId, Long applicantId, User owner, Decision decision) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        TravelPlan plan = planOpt.get();

        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can modify applications");
        }

        User applicant = userRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Applicant not found"));

        Optional<UserPlanStatus> userPlanStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(applicant, plan);
        if (userPlanStatusOpt.isEmpty()) {
            throw new IllegalArgumentException("No application found for this plan");
        }

        UserPlanStatus userPlanStatus = userPlanStatusOpt.get();
        if (userPlanStatus.getStatus() != UserPlanStatus.Status.APPLIED) {
            throw new IllegalStateException("Can only act on pending applications");
        }

        switch (decision) {
            case ACCEPT:
                userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED_ACCEPTED);
                userPlanStatusRepository.save(userPlanStatus);
                
                // Check if maxMembers is reached after accepting this application
                checkAndHandleMaxMembersReached(plan);
                break;
            case REFUSE:
                userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED_REFUSED);
                userPlanStatusRepository.save(userPlanStatus);
                break;
        }

        TravelPlanResponse response = convertToResponse(plan);
        populateMembers(response, plan, true);
        return response;
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
        
        // Check if plan has reached maxMembers
        if (isPlanFull(plan)) {
            throw new IllegalStateException("Travel plan has reached maximum number of members");
        }
        
        User invitee = userRepository.findByEmail(inviteeEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        // Check if invitee already has a current plan
        boolean hasCurrentPlan = travelPlanRepository.userHasCurrentPlan(invitee.getId());
        if (hasCurrentPlan) {
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
        
        // Send invitation notification email
        try {
            notificationService.sendInvitationNotification(invitee, plan, owner);
        } catch (Exception e) {
            System.err.println("Failed to send invitation notification to " + invitee.getEmail() + ": " + e.getMessage());
        }
        
        TravelPlanResponse travelPlanResponse = convertToResponse(plan);
        travelPlanResponse.setHasCurrentPlan(hasCurrentPlan);
        
        // Populate members (include pending applications and invitations)
        populateMembers(travelPlanResponse, plan, true);
        return travelPlanResponse;
    }
    
    // Combined accept/refuse invitation handler
    public TravelPlanResponse handleInvitation(Long planId, User user, Decision decision) {
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
            throw new IllegalStateException("Can only act on pending invitations");
        }

        switch (decision) {
            case ACCEPT:
                userStatus.setStatus(UserPlanStatus.Status.INVITED_ACCEPTED);
                userPlanStatusRepository.save(userStatus);
                
                // Check if maxMembers is reached after accepting this invitation
                checkAndHandleMaxMembersReached(plan);
                break;
            case REFUSE:
                userStatus.setStatus(UserPlanStatus.Status.INVITED_REFUSED);
                userPlanStatusRepository.save(userStatus);
                break;
        }

        TravelPlanResponse response = convertToResponse(plan);
        populateMembers(response, plan, true);
        return response;
    }
    
    /**
     * Cancel travel plan (owner only)
     */
    public TravelPlanResponse closePlan(Long planId, String reason, User owner) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is owner
        if (!plan.getOwner().getId().equals(owner.getId())) {
            throw new IllegalStateException("Only plan owner can cancel the plan");
        }
        
        // Only allow cancel when plan is NEW
        if (plan.getStatus() != TravelPlan.Status.NEW) {
            throw new IllegalStateException("Plan can only be closed when status is NEW");
        }
        
        plan.setStatus(TravelPlan.Status.CANCELLED);
        plan.setCancelledAt(LocalDateTime.now());
        plan.setCancellationReason(reason);
        travelPlanRepository.save(plan);
        
        // Automatically refuse all pending applications and invitations when plan is closed
        refuseAllPendingApplicationsAndInvitations(plan);
        
        TravelPlanResponse response = convertToResponse(plan);
        populateMembers(response, plan, true);
        return response;
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
     * Check if travel plan has reached maxMembers and automatically refuse pending applications/invitations
     */
    private void checkAndHandleMaxMembersReached(TravelPlan plan) {
        // Count current active members (OWNED, APPLIED_ACCEPTED, INVITED_ACCEPTED)
        long activeMemberCount = userPlanStatusRepository.countActiveMembers(plan);
        
        // If we've reached maxMembers, automatically refuse all pending applications and invitations
        if (activeMemberCount >= plan.getMaxMembers()) {
            refuseAllPendingApplicationsAndInvitations(plan);
        }
    }
    
    /**
     * Check if travel plan has reached maxMembers
     */
    public boolean isPlanFull(TravelPlan plan) {
        long activeMemberCount = userPlanStatusRepository.countActiveMembers(plan);
        return activeMemberCount >= plan.getMaxMembers();
    }
    
    /**
     * Populate members in TravelPlanResponse
     */
    private void populateMembers(TravelPlanResponse response, TravelPlan plan, boolean includePending) {
        List<UserPlanStatus.Status> allowedStatuses;
        if (includePending) {
            // Include pending applications and invitations
            allowedStatuses = Arrays.asList(
                    UserPlanStatus.Status.OWNED,
                    UserPlanStatus.Status.APPLIED,
                    UserPlanStatus.Status.APPLIED_ACCEPTED,
                    UserPlanStatus.Status.INVITED,
                    UserPlanStatus.Status.INVITED_ACCEPTED
            );
        } else {
            // Only include confirmed members
            allowedStatuses = Arrays.asList(
                    UserPlanStatus.Status.OWNED,
                    UserPlanStatus.Status.APPLIED_ACCEPTED,
                    UserPlanStatus.Status.INVITED_ACCEPTED
            );
        }

        List<MemberResponseDTO> members = plan.getUserPlanStatuses().stream()
                .filter(userPlanStatus -> allowedStatuses.contains(userPlanStatus.getStatus()))
                .map(userPlanStatus -> new MemberResponseDTO(
                        userPlanStatus.getUser().getId(),
                        userPlanStatus.getUser().getFullName(),
                        userPlanStatus.getUser().getProfilePicture(),
                        userPlanStatus.getStatus()
                ))
                .collect(Collectors.toList());

        response.setMembers(members);
    }
    
    /**
     * Automatically refuse all pending applications and invitations for a travel plan
     */
    private void refuseAllPendingApplicationsAndInvitations(TravelPlan plan) {
        // Get all pending applications and invitations
        List<UserPlanStatus> pendingApplications = userPlanStatusRepository.findPendingApplications(plan);
        List<UserPlanStatus> pendingInvitations = userPlanStatusRepository.findPendingInvitations(plan);
        
        // Refuse all pending applications
        for (UserPlanStatus application : pendingApplications) {
            application.setStatus(UserPlanStatus.Status.APPLIED_REFUSED);
            userPlanStatusRepository.save(application);
        }
        
        // Refuse all pending invitations
        for (UserPlanStatus invitation : pendingInvitations) {
            invitation.setStatus(UserPlanStatus.Status.INVITED_REFUSED);
            userPlanStatusRepository.save(invitation);
        }
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
        return response;
    }

    
} 