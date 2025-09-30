package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.SharedExpenseRequest;
import com.comp8047.majorproject.travelplanassistant.dto.SharedExpenseResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.SharedExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shared-expenses")
@CrossOrigin(origins = "*")
public class SharedExpenseController {
    
    @Autowired
    private SharedExpenseService sharedExpenseService;
    
    public SharedExpenseController(SharedExpenseService sharedExpenseService) {
        this.sharedExpenseService = sharedExpenseService;
    }
    
    /**
     * Create a new shared expense
     */
    @PostMapping("/travel-plans/{planId}")
    public ResponseEntity<?> createSharedExpense(
            @PathVariable Long planId,
            @Valid @RequestBody SharedExpenseRequest request,
            @AuthenticationPrincipal User user) {
        try {
            SharedExpenseResponse response = sharedExpenseService.createSharedExpense(planId, request, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get shared expenses for a travel plan
     */
    @GetMapping("/travel-plan/{travelPlanId}")
    public ResponseEntity<?> getExpensesForTravelPlan(
            @PathVariable Long travelPlanId,
            @AuthenticationPrincipal User currentUser) {
        try {
            List<SharedExpenseResponse> expenses = sharedExpenseService.getExpensesForTravelPlan(
                    travelPlanId, currentUser);
            
            return ResponseEntity.ok(expenses);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get a specific shared expense
     */
    @GetMapping("/{expenseId}")
    public ResponseEntity<?> getSharedExpense(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal User currentUser) {
        try {
            SharedExpenseResponse response = sharedExpenseService.getSharedExpense(expenseId, currentUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Update a shared expense
     */
    @PutMapping("/{expenseId}")
    public ResponseEntity<?> updateSharedExpense(
            @PathVariable Long expenseId,
            @Valid @RequestBody SharedExpenseRequest request,
            @AuthenticationPrincipal User currentUser) {
        try {
            SharedExpenseResponse response = sharedExpenseService.updateSharedExpense(
                    expenseId, request, currentUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Delete a shared expense
     */
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteSharedExpense(
            @PathVariable Long expenseId,
            @AuthenticationPrincipal User currentUser) {
        try {
            sharedExpenseService.deleteSharedExpense(expenseId, currentUser);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Shared expense deleted successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Mark a participant's payment as paid
     */
    @PatchMapping("/{expenseId}/participants/{participantId}/mark-paid")
    public ResponseEntity<?> markParticipantAsPaid(
            @PathVariable Long expenseId,
            @PathVariable Long participantId,
            @AuthenticationPrincipal User currentUser) {
        try {
            SharedExpenseResponse response = sharedExpenseService.markParticipantAsPaid(
                    expenseId, participantId, currentUser);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get expense summary for current user in a travel plan
     */
    @GetMapping("/travel-plan/{travelPlanId}/summary")
    public ResponseEntity<?> getExpenseSummary(
            @PathVariable Long travelPlanId,
            @AuthenticationPrincipal User currentUser) {
        try {
            SharedExpenseService.ExpenseSummaryResponse summary = 
                    sharedExpenseService.getExpenseSummaryForUser(travelPlanId, currentUser);
            return ResponseEntity.ok(summary);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get expenses where current user owes money
     */
    @GetMapping("/my-debts")
    public ResponseEntity<?> getMyDebts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        try {
            // This would need a custom repository method to find expenses where user owes money
            // For now, returning a placeholder
            Map<String, String> response = new HashMap<>();
            response.put("message", "Feature coming soon - Get expenses where current user owes money");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Get expenses where current user is owed money
     */
    @GetMapping("/my-credits")
    public ResponseEntity<?> getMyCredits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        try {
            // This would need a custom repository method to find expenses where user is owed money
            // For now, returning a placeholder
            Map<String, String> response = new HashMap<>();
            response.put("message", "Feature coming soon - Get expenses where current user is owed money");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "SharedExpenseController");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}
