package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.Poll;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PollRepository extends JpaRepository<Poll, Long> {
    
    // Find all polls for a specific travel plan
    List<Poll> findByTravelPlanOrderByCreatedAtDesc(TravelPlan travelPlan);
    
    // Find polls by creator
    List<Poll> findByCreatorOrderByCreatedAtDesc(User creator);
    
    // Find polls by travel plan and creator
    List<Poll> findByTravelPlanAndCreatorOrderByCreatedAtDesc(TravelPlan travelPlan, User creator);
    
    // Find active polls for a travel plan
    @Query("SELECT p FROM Poll p WHERE p.travelPlan = :travelPlan AND p.isActive = true ORDER BY p.createdAt DESC")
    List<Poll> findActivePollsByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Count polls for a travel plan
    long countByTravelPlan(TravelPlan travelPlan);
    
    // Count active polls for a travel plan
    @Query("SELECT COUNT(p) FROM Poll p WHERE p.travelPlan = :travelPlan AND p.isActive = true")
    long countActivePollsByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    // Delete all polls for a travel plan
    void deleteByTravelPlan(TravelPlan travelPlan);
    
    // Delete all polls by a user
    void deleteByCreator(User creator);
} 