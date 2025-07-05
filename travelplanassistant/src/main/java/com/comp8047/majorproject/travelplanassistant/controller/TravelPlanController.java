package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanCreateRequest;
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
@RequestMapping("/api/travel-plans")
@CrossOrigin(origins = "*")
public class TravelPlanController {
    
    @Autowired
    private TravelPlanService travelPlanService;
    
    /**
     * Create a new travel plan
     */
    @PostMapping
    public ResponseEntity<TravelPlanResponse> createTravelPlan(
            @Valid @RequestBody TravelPlanCreateRequest request,
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
     * Get current plans for a user
     */
    @GetMapping("/current")
    public ResponseEntity<List<TravelPlanResponse>> getCurrentPlans(
            @AuthenticationPrincipal User user) {
        List<TravelPlanResponse> plans = travelPlanService.getCurrentPlans(user.getId());
        return ResponseEntity.ok(plans);
    }
    
    /**
     * Get history plans for a user
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
        TravelPlanResponse response = travelPlanService.getTravelPlanById(planId, user.getId());
        return ResponseEntity.ok(response);
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
    @PostMapping("/{planId}/cancel-application")
    public ResponseEntity<TravelPlanResponse> cancelApplication(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.cancelApplication(planId, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Accept application to a travel plan (owner only)
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
     * Refuse application to a travel plan (owner only)
     */
    @PostMapping("/{planId}/applications/{applicantId}/refuse")
    public ResponseEntity<TravelPlanResponse> refuseApplication(
            @PathVariable Long planId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.refuseApplication(planId, applicantId, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Invite user to travel plan (owner only)
     */
    @PostMapping("/{planId}/invite")
    public ResponseEntity<TravelPlanResponse> inviteUser(
            @PathVariable Long planId,
            @RequestParam String inviteeEmail,
            @AuthenticationPrincipal User owner) {
        TravelPlanResponse response = travelPlanService.inviteUser(planId, inviteeEmail, owner);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Accept invitation to travel plan
     */
    @PostMapping("/{planId}/accept-invitation")
    public ResponseEntity<TravelPlanResponse> acceptInvitation(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        TravelPlanResponse response = travelPlanService.acceptInvitation(planId, user);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Refuse invitation to travel plan
     */
    @PostMapping("/{planId}/refuse-invitation")
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
            @PathVariable Long planId,
            @RequestParam String reason,
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
    public ResponseEntity<Boolean> userHasCurrentPlan(@AuthenticationPrincipal User user) {
        boolean hasCurrentPlan = travelPlanService.userHasCurrentPlan(user.getId());
        return ResponseEntity.ok(hasCurrentPlan);
    }
} 