package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanRequest;
import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.TravelPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-plans")
@CrossOrigin(origins = "*")
public class TravelPlanController {
    
    @Autowired
    private TravelPlanService travelPlanService;
    
    public TravelPlanController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    /**
     * Create a new travel plan
     */
    @PostMapping
    public ResponseEntity<TravelPlanResponse> createTravelPlan(
            @Valid @RequestBody TravelPlanRequest request,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.createTravelPlan(request, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get all public new plans not associated with the current user
     */
    @GetMapping("/discovery")
    public ResponseEntity<List<TravelPlanResponse>> getPublicNewPlans(
            @AuthenticationPrincipal User user) {
        List<TravelPlanResponse> plans = travelPlanService.getPublicNewPlans(user.getId());
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Search public plans by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<List<TravelPlanResponse>> searchPublicPlans(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user) {
        List<TravelPlanResponse> plans = travelPlanService.searchPublicPlans(keyword, user.getId());
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Get current plans for the authenticated user
     */
    @GetMapping("/current")
    public ResponseEntity<List<TravelPlanResponse>> getCurrentPlans(
            @AuthenticationPrincipal User user) {
        List<TravelPlanResponse> plans = travelPlanService.getCurrentPlans(user.getId());
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Get history plans for the authenticated user
     */
    @GetMapping("/history")
    public ResponseEntity<List<TravelPlanResponse>> getHistoryPlans(
            @AuthenticationPrincipal User user) {
        List<TravelPlanResponse> plans = travelPlanService.getHistoryPlans(user.getId());
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Get travel plan by ID
     */
    @GetMapping("/{planId}")
    public ResponseEntity<TravelPlanResponse> getTravelPlanById(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse plan = travelPlanService.getTravelPlanById(planId, user.getId());
        return ResponseEntity.ok(plan);
    }
    
    /**
     * Apply to a travel plan
     */
    @PostMapping("/{planId}/apply")
    public ResponseEntity<TravelPlanResponse> applyToPlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.applyToPlan(planId, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel application to a travel plan
     */
    @DeleteMapping("/{planId}/apply")
    public ResponseEntity<TravelPlanResponse> cancelApplication(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.cancelApplication(planId, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Accept application to travel plan (owner only)
     */
    @PostMapping("/{planId}/applications/{applicantId}/accept")
    public ResponseEntity<TravelPlanResponse> acceptApplication(
            @PathVariable Long planId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.acceptApplication(planId, applicantId, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Reject application to travel plan (owner only)
     */
    @PostMapping("/{planId}/applications/{applicantId}/reject")
    public ResponseEntity<TravelPlanResponse> rejectApplication(
            @PathVariable Long planId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.rejectApplication(planId, applicantId, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Invite user to travel plan (owner only)
     */
    @PostMapping("/{planId}/invite")
    public ResponseEntity<TravelPlanResponse> inviteUser(
            @RequestParam String email,
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.inviteUser(planId, email, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Accept invitation to travel plan
     */
    @PostMapping("/{planId}/invite/accept")
    public ResponseEntity<TravelPlanResponse> acceptInvitation(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.acceptInvitation(planId, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refuse invitation to travel plan
     */
    @PostMapping("/{planId}/invite/refuse")
    public ResponseEntity<TravelPlanResponse> refuseInvitation(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.refuseInvitation(planId, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel travel plan (owner only)
     */
    @PostMapping("/{planId}/cancel")
    public ResponseEntity<TravelPlanResponse> cancelPlan(
            @RequestParam String reason,
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.cancelPlan(planId, reason, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Start travel plan (owner only)
     */
    @PostMapping("/{planId}/start")
    public ResponseEntity<TravelPlanResponse> startPlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.startPlan(planId, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Complete travel plan (owner only)
     */
    @PostMapping("/{planId}/complete")
    public ResponseEntity<TravelPlanResponse> completePlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.completePlan(planId, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Check if user has current plan
     */
    @GetMapping("/check-current")
    public ResponseEntity<Boolean> userHasCurrentPlan(
            @AuthenticationPrincipal User user) {
        boolean hasCurrentPlan = travelPlanService.userHasCurrentPlan(user.getId());
        return ResponseEntity.ok(hasCurrentPlan);
    }
} 