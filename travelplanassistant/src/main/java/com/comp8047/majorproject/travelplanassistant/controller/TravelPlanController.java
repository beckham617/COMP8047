package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanRequest;
import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.TravelPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> createTravelPlan(
            @Valid @RequestBody TravelPlanRequest request,
            @AuthenticationPrincipal User user) {
        try {
            TravelPlanResponse response = travelPlanService.createTravelPlan(request, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Get all public new plans not associated with the current user
     */
    @GetMapping("/discovery")
    public ResponseEntity<?> getPublicNewPlans(
            @AuthenticationPrincipal User user) {
        try {
            List<TravelPlanResponse> plans = travelPlanService.getPublicNewPlans(user.getId());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Search public plans by keyword
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchPublicPlans(
            @RequestParam String keyword,
            @AuthenticationPrincipal User user) {
        try {
            List<TravelPlanResponse> plans = travelPlanService.searchPublicPlans(keyword, user.getId());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Get current plans for the authenticated user
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentPlans(
            @AuthenticationPrincipal User user) {
        try {
            List<TravelPlanResponse> plans = travelPlanService.getCurrentPlans(user.getId());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Get history plans for the authenticated user
     */
    @GetMapping("/history")
    public ResponseEntity<?> getHistoryPlans(
            @AuthenticationPrincipal User user) {
        try {
            List<TravelPlanResponse> plans = travelPlanService.getHistoryPlans(user.getId());
            return ResponseEntity.ok(plans);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Get travel plan by ID
     */
    @GetMapping("/{planId}")
    public ResponseEntity<?> getTravelPlanById(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        try {
            TravelPlanResponse plan = travelPlanService.getTravelPlanById(planId, user.getId());
            return ResponseEntity.ok(plan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Apply to a travel plan
     */
    @PostMapping("/{planId}/apply")
    public ResponseEntity<?> applyToPlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        try {
            TravelPlanResponse response = travelPlanService.applyToPlan(planId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Cancel application to a travel plan
     */
    @PutMapping("/{planId}/cancel")
    public ResponseEntity<?> cancelApplication(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        try {
            TravelPlanResponse response = travelPlanService.cancelApplication(planId, user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Accept application to travel plan (owner only)
     */
    @PostMapping("/{planId}/applications/{applicantId}/accept")
    public ResponseEntity<?> acceptApplication(
            @PathVariable Long planId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal User owner) {
        try {
            TravelPlanResponse response = travelPlanService.handleApplication(planId, applicantId, owner, TravelPlanService.Decision.ACCEPT);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Reject application to travel plan (owner only)
     */
    @PostMapping("/{planId}/applications/{applicantId}/reject")
    public ResponseEntity<?> rejectApplication(
            @PathVariable Long planId,
            @PathVariable Long applicantId,
            @AuthenticationPrincipal User owner) {
        try {
            TravelPlanResponse response = travelPlanService.handleApplication(planId, applicantId, owner, TravelPlanService.Decision.REFUSE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Invite user to travel plan (owner only)
     */
    @PostMapping("/{planId}/invite")
    public ResponseEntity<?> inviteUser(
            @RequestParam String email,
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        try {
            TravelPlanResponse response = travelPlanService.inviteUser(planId, email, owner);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Accept invitation to travel plan
     */
    @PostMapping("/{planId}/invite/accept")
    public ResponseEntity<?> acceptInvitation(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        try {
            TravelPlanResponse response = travelPlanService.handleInvitation(planId, user, TravelPlanService.Decision.ACCEPT);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Refuse invitation to travel plan
     */
    @PostMapping("/{planId}/invite/refuse")
    public ResponseEntity<?> refuseInvitation(
            @PathVariable Long planId,
            @AuthenticationPrincipal User user) {
        try {
            TravelPlanResponse response = travelPlanService.handleInvitation(planId, user, TravelPlanService.Decision.REFUSE);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Close travel plan (owner only) when status is NEW
     */
    @PostMapping("/{planId}/close")
    public ResponseEntity<?> closePlan(
            @RequestParam String reason,
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        try {
            TravelPlanResponse response = travelPlanService.closePlan(planId, reason, owner);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Start travel plan (owner only)
     */
    @PostMapping("/{planId}/start")
    public ResponseEntity<?> startPlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        try {
            TravelPlanResponse response = travelPlanService.startPlan(planId, owner);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Complete travel plan (owner only)
     */
    @PostMapping("/{planId}/complete")
    public ResponseEntity<?> completePlan(
            @PathVariable Long planId,
            @AuthenticationPrincipal User owner) {
        try {
            TravelPlanResponse response = travelPlanService.completePlan(planId, owner);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
    
    /**
     * Check if user has current plan
     */
    @GetMapping("/check-current")
    public ResponseEntity<?> userHasCurrentPlan(
            @AuthenticationPrincipal User user) {
        try {
            boolean hasCurrentPlan = travelPlanService.userHasCurrentPlan(user.getId());
            return ResponseEntity.ok(hasCurrentPlan);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
} 