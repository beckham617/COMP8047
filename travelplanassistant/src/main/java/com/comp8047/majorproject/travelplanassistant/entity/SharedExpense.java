package com.comp8047.majorproject.travelplanassistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shared_expenses")
public class SharedExpense {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Expense description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    @Column(nullable = false, length = 500)
    private String description;
    
    @NotBlank(message = "Expense purpose is required")
    @Size(max = 200, message = "Purpose must be less than 200 characters")
    @Column(nullable = false, length = 200)
    private String purpose;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id", nullable = false)
    @NotNull(message = "Travel plan is required")
    private TravelPlan travelPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by", nullable = false)
    @NotNull(message = "Payer is required")
    private User paidBy;
    
    @Positive(message = "Amount must be positive")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;
    
    @Column(name = "currency", nullable = false)
    private String currency = "CAD";
    
    @Column(name = "expense_date", nullable = false)
    private LocalDateTime expenseDate;
    
    @Column(name = "payment_method")
    private String paymentMethod;
    
    @Column(name = "receipt_image")
    private String receiptImage;
    
    @Column(name = "notes", length = 1000)
    private String notes;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExpenseStatus status = ExpenseStatus.PENDING;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "settled_at")
    private LocalDateTime settledAt;
    
    // Relationships
    @OneToMany(mappedBy = "sharedExpense", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ExpenseAllocation> allocations = new ArrayList<>();
    
    // Constructors
    public SharedExpense() {
        this.createdAt = LocalDateTime.now();
        this.expenseDate = LocalDateTime.now();
    }
    
    public SharedExpense(String description, String purpose, TravelPlan travelPlan, User paidBy, BigDecimal totalAmount) {
        this();
        this.description = description;
        this.purpose = purpose;
        this.travelPlan = travelPlan;
        this.paidBy = paidBy;
        this.totalAmount = totalAmount;
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
    
    public TravelPlan getTravelPlan() {
        return travelPlan;
    }
    
    public void setTravelPlan(TravelPlan travelPlan) {
        this.travelPlan = travelPlan;
    }
    
    public User getPaidBy() {
        return paidBy;
    }
    
    public void setPaidBy(User paidBy) {
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public String getReceiptImage() {
        return receiptImage;
    }
    
    public void setReceiptImage(String receiptImage) {
        this.receiptImage = receiptImage;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public ExpenseStatus getStatus() {
        return status;
    }
    
    public void setStatus(ExpenseStatus status) {
        this.status = status;
        if (status == ExpenseStatus.SETTLED) {
            this.settledAt = LocalDateTime.now();
        }
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
    
    public LocalDateTime getSettledAt() {
        return settledAt;
    }
    
    public void setSettledAt(LocalDateTime settledAt) {
        this.settledAt = settledAt;
    }
    
    public List<ExpenseAllocation> getAllocations() {
        return allocations;
    }
    
    public void setAllocations(List<ExpenseAllocation> allocations) {
        this.allocations = allocations;
    }
    
    // Helper methods
    public boolean isPending() {
        return status == ExpenseStatus.PENDING;
    }
    
    public boolean isSettled() {
        return status == ExpenseStatus.SETTLED;
    }
    
    public int getParticipantCount() {
        return allocations.size();
    }
    
    public BigDecimal getAmountPerPerson() {
        if (allocations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return totalAmount.divide(BigDecimal.valueOf(allocations.size()), 2, BigDecimal.ROUND_HALF_UP);
    }
    
    public BigDecimal getTotalAllocatedAmount() {
        return allocations.stream()
                .map(ExpenseAllocation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public boolean isFullyAllocated() {
        return getTotalAllocatedAmount().compareTo(totalAmount) == 0;
    }
    
    public void addAllocation(ExpenseAllocation allocation) {
        allocations.add(allocation);
        allocation.setSharedExpense(this);
    }
    
    public void removeAllocation(ExpenseAllocation allocation) {
        allocations.remove(allocation);
        allocation.setSharedExpense(null);
    }
    
    public void allocateEqually(List<User> participants) {
        allocations.clear();
        BigDecimal amountPerPerson = getAmountPerPerson();
        
        for (User participant : participants) {
            ExpenseAllocation allocation = new ExpenseAllocation(participant, amountPerPerson);
            addAllocation(allocation);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Expense status enum
    public enum ExpenseStatus {
        PENDING, SETTLED
    }
    
    // Inner class for expense allocations
    @Entity
    @Table(name = "expense_allocations")
    public static class ExpenseAllocation {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "shared_expense_id", nullable = false)
        private SharedExpense sharedExpense;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        
        @Positive(message = "Allocated amount must be positive")
        @Column(precision = 10, scale = 2, nullable = false)
        private BigDecimal amount;
        
        @Column(name = "is_paid")
        private Boolean isPaid = false;
        
        @Column(name = "paid_at")
        private LocalDateTime paidAt;
        
        @Column(name = "payment_method")
        private String paymentMethod;
        
        @Column(name = "notes", length = 500)
        private String notes;
        
        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;
        
        public ExpenseAllocation() {
            this.createdAt = LocalDateTime.now();
        }
        
        public ExpenseAllocation(User user, BigDecimal amount) {
            this();
            this.user = user;
            this.amount = amount;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public SharedExpense getSharedExpense() {
            return sharedExpense;
        }
        
        public void setSharedExpense(SharedExpense sharedExpense) {
            this.sharedExpense = sharedExpense;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public Boolean getIsPaid() {
            return isPaid;
        }
        
        public void setIsPaid(Boolean isPaid) {
            this.isPaid = isPaid;
            if (isPaid) {
                this.paidAt = LocalDateTime.now();
            }
        }
        
        public LocalDateTime getPaidAt() {
            return paidAt;
        }
        
        public void setPaidAt(LocalDateTime paidAt) {
            this.paidAt = paidAt;
        }
        
        public String getPaymentMethod() {
            return paymentMethod;
        }
        
        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
        
        public String getNotes() {
            return notes;
        }
        
        public void setNotes(String notes) {
            this.notes = notes;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        // Helper methods
        public boolean isPaid() {
            return isPaid;
        }
        
        public void markAsPaid() {
            setIsPaid(true);
        }
        
        public void markAsUnpaid() {
            setIsPaid(false);
            this.paidAt = null;
        }
    }
} 