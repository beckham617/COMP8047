package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    // Find by owner
    List<TravelPlan> findByOwner(User owner);

    // Find by status
    List<TravelPlan> findByStatus(TravelPlan.Status status);

    // Find by plan type
    List<TravelPlan> findByPlanType(TravelPlan.PlanType planType);

    // Find by category
    List<TravelPlan> findByCategory(TravelPlan.Category category);

    // Find by transportation
    List<TravelPlan> findByTransportation(TravelPlan.TransportType transportation);

    // Find by accommodation
    List<TravelPlan> findByAccommodation(TravelPlan.AccommodationType accommodation);

    // Find by gender preference
    List<TravelPlan> findByGender(TravelPlan.GenderPreference gender);

    // Find by destination country and city
    List<TravelPlan> findByDestinationCountryAndDestinationCity(String destinationCountry, String destinationCity);

    // Find by origin country and city
    List<TravelPlan> findByOriginCountryAndOriginCity(String originCountry, String originCity);

    // Find by start date range
    List<TravelPlan> findByStartDateBetween(LocalDateTime start, LocalDateTime end);

    // Find by end date range
    List<TravelPlan> findByEndDateBetween(LocalDateTime start, LocalDateTime end);

    // Find by start date after
    List<TravelPlan> findByStartDateAfter(LocalDateTime date);

    // Find by end date before
    List<TravelPlan> findByEndDateBefore(LocalDateTime date);

    // Find by max members
    List<TravelPlan> findByMaxMembersLessThanEqual(Integer maxMembers);

    // Find by age min and max
    List<TravelPlan> findByAgeMinGreaterThanEqual(Integer ageMin);
    List<TravelPlan> findByAgeMaxLessThanEqual(Integer ageMax);

    // Find by language
    List<TravelPlan> findByLanguageContainingIgnoreCase(String language);

    // Find by title or description containing search term
    List<TravelPlan> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    // Find by created at after
    List<TravelPlan> findByCreatedAtAfter(LocalDateTime date);

    // Find by owner and status
    List<TravelPlan> findByOwnerAndStatus(User owner, TravelPlan.Status status);

    // Find by status ordered by created at desc
    List<TravelPlan> findByStatusOrderByCreatedAtDesc(TravelPlan.Status status);

    // Find by owner ordered by created at desc
    List<TravelPlan> findByOwnerOrderByCreatedAtDesc(User owner);

    // Search plans by keyword in title or description
    @Query("SELECT tp FROM TravelPlan tp WHERE (LOWER(tp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(tp.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND tp.status = 'NEW'")
    List<TravelPlan> searchPlansByKeyword(@Param("keyword") String keyword);

    // Find by plan type and status
    List<TravelPlan> findByPlanTypeAndStatus(TravelPlan.PlanType planType, TravelPlan.Status status);

    // Check if user has any current plan (status NEW or IN_PROGRESS)
    @Query("SELECT COUNT(tp) > 0 FROM TravelPlan tp JOIN tp.userPlanStatuses ups WHERE ups.user.id = :userId AND tp.status IN ('NEW', 'IN_PROGRESS') AND ups.status IN ('OWNED', 'APPLIED', 'APPLIED_ACCEPTED', 'INVITED', 'INVITED_ACCEPTED')")
    boolean userHasCurrentPlan(@Param("userId") Long userId);
    
    @Query("SELECT tp FROM TravelPlan tp JOIN tp.userPlanStatuses ups WHERE tp.status IN ('NEW', 'IN_PROGRESS') AND ups.user.id = :userId AND ups.status IN ('OWNED', 'APPLIED', 'APPLIED_ACCEPTED', 'INVITED', 'INVITED_ACCEPTED')")
    List<TravelPlan> getCurrentPlans(@Param("userId") Long userId);
} 