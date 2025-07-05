package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.ChatMessage;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    
    // Find all messages for a specific travel plan
    List<ChatMessage> findByTravelPlanOrderByCreatedAtAsc(TravelPlan travelPlan);
    
    // Find messages for a travel plan after a specific time
    List<ChatMessage> findByTravelPlanAndCreatedAtAfterOrderByCreatedAtAsc(TravelPlan travelPlan, LocalDateTime after);
    
    // Find messages by user
    List<ChatMessage> findByUserOrderByCreatedAtDesc(User user);
    
    // Find messages by user and travel plan
    List<ChatMessage> findByUserAndTravelPlanOrderByCreatedAtAsc(User user, TravelPlan travelPlan);
    
    // Find recent messages for a travel plan (last N messages)
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.travelPlan = :travelPlan ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessagesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Count messages for a travel plan
    long countByTravelPlan(TravelPlan travelPlan);
    
    // Find messages created between two dates for a travel plan
    List<ChatMessage> findByTravelPlanAndCreatedAtBetweenOrderByCreatedAtAsc(
            TravelPlan travelPlan, LocalDateTime startDate, LocalDateTime endDate);
    
    // Delete all messages for a travel plan
    void deleteByTravelPlan(TravelPlan travelPlan);
    
    // Delete all messages by a user
    void deleteByUser(User user);
} 