package com.comp8047.majorproject.travelplanassistant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SharedExpenseRequest {
    
    @NotBlank(message = "Expense description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    private String description;
    
    @NotBlank(message = "Expense purpose is required")
    @Size(max = 200, message = "Purpose must be less than 200 characters")
    private String purpose;
    
    @Positive(message = "Amount must be positive")
    private BigDecimal totalAmount;
    
    private String currency = "CAD";
    
    private LocalDateTime expenseDate;
    
    private List<ExpenseParticipantRequest> participants;
    
    private String receipt; // File path or URL to receipt image
    
    // Constructors
    public SharedExpenseRequest() {}
    
    public SharedExpenseRequest(String description, String purpose, Long travelPlanId, 
                               BigDecimal totalAmount, String currency, LocalDateTime expenseDate,
                               List<ExpenseParticipantRequest> participants) {
        this.description = description;
        this.purpose = purpose;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.expenseDate = expenseDate;
        this.participants = participants;
    }
    
    // Getters and Setters
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public LocalDateTime getExpenseDate() {
        return expenseDate;
    }
    
    public void setExpenseDate(LocalDateTime expenseDate) {
        this.expenseDate = expenseDate;
    }
    
    public List<ExpenseParticipantRequest> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<ExpenseParticipantRequest> participants) {
        this.participants = participants;
    }
    
    public String getReceipt() {
        return receipt;
    }
    
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
    
    // Inner class for expense participants
    public static class ExpenseParticipantRequest {
        @NotNull(message = "User ID is required")
        private Long userId;
        
        @Positive(message = "Amount must be positive")
        private BigDecimal amountOwed;
        
        private Boolean isPaid = false;
        
        public ExpenseParticipantRequest() {}
        
        public ExpenseParticipantRequest(Long userId, BigDecimal amountOwed, Boolean isPaid) {
            this.userId = userId;
            this.amountOwed = amountOwed;
            this.isPaid = isPaid;
        }
        
        // Getters and Setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public BigDecimal getAmountOwed() {
            return amountOwed;
        }
        
        public void setAmountOwed(BigDecimal amountOwed) {
            this.amountOwed = amountOwed;
        }
        
        public Boolean getIsPaid() {
            return isPaid;
        }
        
        public void setIsPaid(Boolean isPaid) {
            this.isPaid = isPaid;
        }
    }
}
