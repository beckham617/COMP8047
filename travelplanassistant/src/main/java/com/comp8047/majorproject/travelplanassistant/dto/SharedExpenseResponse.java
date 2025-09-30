package com.comp8047.majorproject.travelplanassistant.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SharedExpenseResponse {
    
    private Long id;
    private String description;
    private String purpose;
    private Long travelPlanId;
    private String travelPlanTitle;
    private UserInfo paidBy;
    private BigDecimal totalAmount;
    private String currency;
    private LocalDateTime expenseDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ExpenseParticipantResponse> participants;
    private String receipt;
    private Boolean isSettled;
    private BigDecimal totalPaid;
    private BigDecimal totalOwed;
    
    // Constructors
    public SharedExpenseResponse() {}
    
    public SharedExpenseResponse(Long id, String description, String purpose, Long travelPlanId,
                                String travelPlanTitle, UserInfo paidBy, BigDecimal totalAmount,
                                String currency, LocalDateTime expenseDate, LocalDateTime createdAt,
                                List<ExpenseParticipantResponse> participants) {
        this.id = id;
        this.description = description;
        this.purpose = purpose;
        this.travelPlanId = travelPlanId;
        this.travelPlanTitle = travelPlanTitle;
        this.paidBy = paidBy;
        this.totalAmount = totalAmount;
        this.currency = currency;
        this.expenseDate = expenseDate;
        this.createdAt = createdAt;
        this.participants = participants;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
    
    public Long getTravelPlanId() {
        return travelPlanId;
    }
    
    public void setTravelPlanId(Long travelPlanId) {
        this.travelPlanId = travelPlanId;
    }
    
    public String getTravelPlanTitle() {
        return travelPlanTitle;
    }
    
    public void setTravelPlanTitle(String travelPlanTitle) {
        this.travelPlanTitle = travelPlanTitle;
    }
    
    public UserInfo getPaidBy() {
        return paidBy;
    }
    
    public void setPaidBy(UserInfo paidBy) {
        this.paidBy = paidBy;
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
    
    public List<ExpenseParticipantResponse> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<ExpenseParticipantResponse> participants) {
        this.participants = participants;
    }
    
    public String getReceipt() {
        return receipt;
    }
    
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
    
    public Boolean getIsSettled() {
        return isSettled;
    }
    
    public void setIsSettled(Boolean isSettled) {
        this.isSettled = isSettled;
    }
    
    public BigDecimal getTotalPaid() {
        return totalPaid;
    }
    
    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }
    
    public BigDecimal getTotalOwed() {
        return totalOwed;
    }
    
    public void setTotalOwed(BigDecimal totalOwed) {
        this.totalOwed = totalOwed;
    }
    
    // Inner classes
    public static class UserInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String profilePicture;
        
        public UserInfo() {}
        
        public UserInfo(Long id, String firstName, String lastName, String email, String profilePicture) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.profilePicture = profilePicture;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getProfilePicture() { return profilePicture; }
        public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }
        
        public String getFullName() {
            return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
        }
    }
    
    public static class ExpenseParticipantResponse {
        private Long id;
        private UserInfo user;
        private BigDecimal amountOwed;
        private Boolean isPaid;
        private LocalDateTime paidAt;
        
        public ExpenseParticipantResponse() {}
        
        public ExpenseParticipantResponse(Long id, UserInfo user, BigDecimal amountOwed, 
                                         Boolean isPaid, LocalDateTime paidAt) {
            this.id = id;
            this.user = user;
            this.amountOwed = amountOwed;
            this.isPaid = isPaid;
            this.paidAt = paidAt;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public UserInfo getUser() { return user; }
        public void setUser(UserInfo user) { this.user = user; }
        
        public BigDecimal getAmountOwed() { return amountOwed; }
        public void setAmountOwed(BigDecimal amountOwed) { this.amountOwed = amountOwed; }
        
        public Boolean getIsPaid() { return isPaid; }
        public void setIsPaid(Boolean isPaid) { this.isPaid = isPaid; }
        
        public LocalDateTime getPaidAt() { return paidAt; }
        public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
    }
}
