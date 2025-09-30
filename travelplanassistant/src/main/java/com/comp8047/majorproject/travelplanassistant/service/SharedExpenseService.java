package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.SharedExpenseRequest;
import com.comp8047.majorproject.travelplanassistant.dto.SharedExpenseResponse;
import com.comp8047.majorproject.travelplanassistant.entity.*;
import com.comp8047.majorproject.travelplanassistant.repository.SharedExpenseRepository;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SharedExpenseService {
    
    @Autowired
    private SharedExpenseRepository sharedExpenseRepository;
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new shared expense
     */
    public SharedExpenseResponse createSharedExpense(Long planId, SharedExpenseRequest request, User user) {
        // Validate travel plan exists and user has access
        TravelPlan travelPlan = travelPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Travel plan not found"));
        
        // Check if user is member of the travel plan
        if (!isMemberOfTravelPlan(user, travelPlan)) {
            throw new RuntimeException("You are not a member of this travel plan");
        }
        
        // Create shared expense
        SharedExpense expense = new SharedExpense();
        expense.setDescription(request.getDescription());
        expense.setPurpose(request.getPurpose());
        expense.setTravelPlan(travelPlan);
        expense.setPaidBy(user);
        expense.setTotalAmount(request.getTotalAmount());
        expense.setCurrency(request.getCurrency());
        expense.setExpenseDate(request.getExpenseDate() != null ? request.getExpenseDate() : LocalDateTime.now());
        expense.setReceiptImage(request.getReceipt());
        
        SharedExpense savedExpense = sharedExpenseRepository.save(expense);
        
        // Create expense allocations
        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            for (SharedExpenseRequest.ExpenseParticipantRequest participantRequest : request.getParticipants()) {
                User participant = userRepository.findById(participantRequest.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found: " + participantRequest.getUserId()));
                
                SharedExpense.ExpenseAllocation allocation = new SharedExpense.ExpenseAllocation();
                allocation.setSharedExpense(savedExpense);
                allocation.setUser(participant);
                allocation.setAmount(participantRequest.getAmountOwed());
                allocation.setIsPaid(participantRequest.getIsPaid());
                
                savedExpense.getAllocations().add(allocation);
            }
            savedExpense = sharedExpenseRepository.save(savedExpense);
        }
        
        return convertToResponse(savedExpense);
    }
    
    /**
     * Get shared expenses for a travel plan
     */
    public List<SharedExpenseResponse> getExpensesForTravelPlan(Long travelPlanId, User currentUser) {
        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new RuntimeException("Travel plan not found"));
        
        if (!isMemberOfTravelPlan(currentUser, travelPlan)) {
            throw new RuntimeException("You are not a member of this travel plan");
        }
        
        List<SharedExpense> expenses = sharedExpenseRepository.findByTravelPlanOrderByCreatedAtDesc(travelPlan);
        return expenses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get a specific shared expense
     */
    public SharedExpenseResponse getSharedExpense(Long expenseId, User currentUser) {
        SharedExpense expense = sharedExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Shared expense not found"));
        
        if (!isMemberOfTravelPlan(currentUser, expense.getTravelPlan())) {
            throw new RuntimeException("You are not a member of this travel plan");
        }
        
        return convertToResponse(expense);
    }
    
    /**
     * Update a shared expense
     */
    public SharedExpenseResponse updateSharedExpense(Long expenseId, SharedExpenseRequest request, User currentUser) {
        SharedExpense expense = sharedExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Shared expense not found"));
        
        // Only the person who paid can update the expense
        if (!expense.getPaidBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the person who paid can update this expense");
        }
        
        // Update expense details
        expense.setDescription(request.getDescription());
        expense.setPurpose(request.getPurpose());
        expense.setTotalAmount(request.getTotalAmount());
        expense.setCurrency(request.getCurrency());
        expense.setExpenseDate(request.getExpenseDate());
        expense.setReceiptImage(request.getReceipt());
        
        // Clear existing allocations and add new ones
        expense.getAllocations().clear();
        
        if (request.getParticipants() != null && !request.getParticipants().isEmpty()) {
            for (SharedExpenseRequest.ExpenseParticipantRequest participantRequest : request.getParticipants()) {
                User participant = userRepository.findById(participantRequest.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found: " + participantRequest.getUserId()));
                
                SharedExpense.ExpenseAllocation allocation = new SharedExpense.ExpenseAllocation();
                allocation.setSharedExpense(expense);
                allocation.setUser(participant);
                allocation.setAmount(participantRequest.getAmountOwed());
                allocation.setIsPaid(participantRequest.getIsPaid());
                
                expense.getAllocations().add(allocation);
            }
        }
        
        SharedExpense savedExpense = sharedExpenseRepository.save(expense);
        return convertToResponse(savedExpense);
    }
    
    /**
     * Delete a shared expense
     */
    public void deleteSharedExpense(Long expenseId, User currentUser) {
        SharedExpense expense = sharedExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Shared expense not found"));
        
        // Only the person who paid can delete the expense
        if (!expense.getPaidBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the person who paid can delete this expense");
        }
        
        sharedExpenseRepository.delete(expense);
    }
    
    /**
     * Mark a participant's payment as paid
     */
    public SharedExpenseResponse markParticipantAsPaid(Long expenseId, Long participantId, User currentUser) {
        SharedExpense expense = sharedExpenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Shared expense not found"));
        
        // Only the person who paid can mark payments as received
        if (!expense.getPaidBy().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Only the person who paid can mark payments as received");
        }
        
        SharedExpense.ExpenseAllocation allocation = expense.getAllocations().stream()
                .filter(a -> a.getId().equals(participantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Allocation not found"));
        
        allocation.setIsPaid(true);
        allocation.setPaidAt(LocalDateTime.now());
        
        SharedExpense savedExpense = sharedExpenseRepository.save(expense);
        return convertToResponse(savedExpense);
    }
    
    /**
     * Get expense summary for a user in a travel plan
     */
    public ExpenseSummaryResponse getExpenseSummaryForUser(Long travelPlanId, User currentUser) {
        TravelPlan travelPlan = travelPlanRepository.findById(travelPlanId)
                .orElseThrow(() -> new RuntimeException("Travel plan not found"));
        
        if (!isMemberOfTravelPlan(currentUser, travelPlan)) {
            throw new RuntimeException("You are not a member of this travel plan");
        }
        
        List<SharedExpense> expenses = sharedExpenseRepository.findByTravelPlan(travelPlan);
        
        BigDecimal totalPaid = expenses.stream()
                .filter(expense -> expense.getPaidBy().getId().equals(currentUser.getId()))
                .map(SharedExpense::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOwed = BigDecimal.ZERO;
        BigDecimal totalOwedToPay = BigDecimal.ZERO;
        
        for (SharedExpense expense : expenses) {
            for (SharedExpense.ExpenseAllocation allocation : expense.getAllocations()) {
                if (allocation.getUser().getId().equals(currentUser.getId())) {
                    totalOwed = totalOwed.add(allocation.getAmount());
                    if (!allocation.getIsPaid()) {
                        totalOwedToPay = totalOwedToPay.add(allocation.getAmount());
                    }
                }
            }
        }
        
        BigDecimal balance = totalPaid.subtract(totalOwed);
        
        return new ExpenseSummaryResponse(totalPaid, totalOwed, totalOwedToPay, balance);
    }
    
    /**
     * Check if user is member of travel plan
     */
    private boolean isMemberOfTravelPlan(User user, TravelPlan travelPlan) {
        return travelPlan.getUserPlanStatuses().stream()
                .anyMatch(ups -> ups.getUser().getId().equals(user.getId()) && 
                         (ups.getStatus() == UserPlanStatus.Status.OWNED ||
                          ups.getStatus() == UserPlanStatus.Status.APPLIED_ACCEPTED ||
                          ups.getStatus() == UserPlanStatus.Status.INVITED_ACCEPTED));
    }
    
    /**
     * Convert SharedExpense entity to response DTO
     */
    private SharedExpenseResponse convertToResponse(SharedExpense expense) {
        SharedExpenseResponse response = new SharedExpenseResponse();
        response.setId(expense.getId());
        response.setDescription(expense.getDescription());
        response.setPurpose(expense.getPurpose());
        response.setTravelPlanId(expense.getTravelPlan().getId());
        response.setTravelPlanTitle(expense.getTravelPlan().getTitle());
        response.setTotalAmount(expense.getTotalAmount());
        response.setCurrency(expense.getCurrency());
        response.setExpenseDate(expense.getExpenseDate());
        response.setCreatedAt(expense.getCreatedAt());
        response.setUpdatedAt(expense.getUpdatedAt());
        response.setReceipt(expense.getReceiptImage());
        
        // Convert paidBy user
        User paidBy = expense.getPaidBy();
        response.setPaidBy(new SharedExpenseResponse.UserInfo(
                paidBy.getId(),
                paidBy.getFirstName(),
                paidBy.getLastName(),
                paidBy.getEmail(),
                paidBy.getProfilePicture()
        ));
        
        // Convert allocations to participants
        List<SharedExpenseResponse.ExpenseParticipantResponse> participants = 
                expense.getAllocations().stream()
                        .map(allocation -> {
                            User user = allocation.getUser();
                            SharedExpenseResponse.UserInfo userInfo = new SharedExpenseResponse.UserInfo(
                                    user.getId(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    user.getEmail(),
                                    user.getProfilePicture()
                            );
                            return new SharedExpenseResponse.ExpenseParticipantResponse(
                                    allocation.getId(),
                                    userInfo,
                                    allocation.getAmount(),
                                    allocation.getIsPaid(),
                                    allocation.getPaidAt()
                            );
                        })
                        .collect(Collectors.toList());
        
        response.setParticipants(participants);
        
        // Calculate totals
        BigDecimal totalPaid = participants.stream()
                .filter(p -> p.getIsPaid())
                .map(SharedExpenseResponse.ExpenseParticipantResponse::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOwed = participants.stream()
                .map(SharedExpenseResponse.ExpenseParticipantResponse::getAmountOwed)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        response.setTotalPaid(totalPaid);
        response.setTotalOwed(totalOwed);
        response.setIsSettled(totalPaid.compareTo(totalOwed) == 0);
        
        return response;
    }
    
    // Inner class for expense summary
    public static class ExpenseSummaryResponse {
        private BigDecimal totalPaid;
        private BigDecimal totalOwed;
        private BigDecimal totalOwedToPay;
        private BigDecimal balance;
        
        public ExpenseSummaryResponse(BigDecimal totalPaid, BigDecimal totalOwed, 
                                     BigDecimal totalOwedToPay, BigDecimal balance) {
            this.totalPaid = totalPaid;
            this.totalOwed = totalOwed;
            this.totalOwedToPay = totalOwedToPay;
            this.balance = balance;
        }
        
        // Getters and Setters
        public BigDecimal getTotalPaid() { return totalPaid; }
        public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
        
        public BigDecimal getTotalOwed() { return totalOwed; }
        public void setTotalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; }
        
        public BigDecimal getTotalOwedToPay() { return totalOwedToPay; }
        public void setTotalOwedToPay(BigDecimal totalOwedToPay) { this.totalOwedToPay = totalOwedToPay; }
        
        public BigDecimal getBalance() { return balance; }
        public void setBalance(BigDecimal balance) { this.balance = balance; }
    }
}
