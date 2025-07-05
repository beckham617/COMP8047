package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.SharedExpense;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SharedExpenseRepository extends JpaRepository<SharedExpense, Long> {
    
    // Find all expenses for a specific travel plan
    List<SharedExpense> findByTravelPlanOrderByCreatedAtDesc(TravelPlan travelPlan);
    
    // Find expenses by creator
    List<SharedExpense> findByCreatorOrderByCreatedAtDesc(User creator);
    
    // Find expenses by travel plan and creator
    List<SharedExpense> findByTravelPlanAndCreatorOrderByCreatedAtDesc(TravelPlan travelPlan, User creator);
    
    // Find expenses by travel plan and status
    List<SharedExpense> findByTravelPlanAndStatusOrderByCreatedAtDesc(TravelPlan travelPlan, SharedExpense.ExpenseStatus status);
    
    // Find pending expenses for a travel plan
    @Query("SELECT se FROM SharedExpense se WHERE se.travelPlan = :travelPlan AND se.status = 'PENDING' ORDER BY se.createdAt DESC")
    List<SharedExpense> findPendingExpensesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Find settled expenses for a travel plan
    @Query("SELECT se FROM SharedExpense se WHERE se.travelPlan = :travelPlan AND se.status = 'SETTLED' ORDER BY se.createdAt DESC")
    List<SharedExpense> findSettledExpensesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Count expenses for a travel plan
    long countByTravelPlan(TravelPlan travelPlan);
    
    // Count pending expenses for a travel plan
    @Query("SELECT COUNT(se) FROM SharedExpense se WHERE se.travelPlan = :travelPlan AND se.status = 'PENDING'")
    long countPendingExpensesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Sum total amount of expenses for a travel plan
    @Query("SELECT SUM(se.totalAmount) FROM SharedExpense se WHERE se.travelPlan = :travelPlan")
    BigDecimal sumTotalAmountByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Sum total amount of settled expenses for a travel plan
    @Query("SELECT SUM(se.totalAmount) FROM SharedExpense se WHERE se.travelPlan = :travelPlan AND se.status = 'SETTLED'")
    BigDecimal sumSettledAmountByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Delete all expenses for a travel plan
    void deleteByTravelPlan(TravelPlan travelPlan);
    
    // Delete all expenses by a user
    void deleteByCreator(User creator);
} 