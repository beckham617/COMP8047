package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TravelPlanRepository extends JpaRepository<TravelPlan, Long> {
    
    /**
     * Find travel plans by owner
     */
    List<TravelPlan> findByOwner(User owner);
    
    /**
     * Find travel plans by plan status
     */
    List<TravelPlan> findByPlanStatus(TravelPlan.PlanStatus planStatus);
    
    /**
     * Find travel plans by visibility
     */
    List<TravelPlan> findByVisibility(TravelPlan.Visibility visibility);
    
    /**
     * Find public travel plans
     */
    List<TravelPlan> findByVisibilityAndPlanStatus(TravelPlan.Visibility visibility, TravelPlan.PlanStatus planStatus);
    
    /**
     * Find travel plans by plan type
     */
    List<TravelPlan> findByPlanType(TravelPlan.PlanType planType);
    
    /**
     * Find travel plans by destination
     */
    List<TravelPlan> findByDestinationContainingIgnoreCase(String destination);
    
    /**
     * Find travel plans by origin location
     */
    List<TravelPlan> findByOriginLocationContainingIgnoreCase(String originLocation);
    
    /**
     * Find travel plans by transport type
     */
    List<TravelPlan> findByTransportType(TravelPlan.TransportType transportType);
    
    /**
     * Find travel plans by gender preference
     */
    List<TravelPlan> findByGenderPreference(TravelPlan.GenderPreference genderPreference);
    
    /**
     * Find travel plans by start date range
     */
    List<TravelPlan> findByStartDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find travel plans by end date range
     */
    List<TravelPlan> findByEndDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find travel plans starting after a specific date
     */
    List<TravelPlan> findByStartDateAfter(LocalDateTime date);
    
    /**
     * Find travel plans ending before a specific date
     */
    List<TravelPlan> findByEndDateBefore(LocalDateTime date);
    
    /**
     * Find travel plans by maximum members
     */
    List<TravelPlan> findByMaxMembersLessThanEqual(Integer maxMembers);
    
    /**
     * Find travel plans by minimum members
     */
    List<TravelPlan> findByMinMembersGreaterThanEqual(Integer minMembers);
    
    /**
     * Find travel plans by budget range
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.estimatedBudget BETWEEN :minBudget AND :maxBudget")
    List<TravelPlan> findByBudgetRange(@Param("minBudget") java.math.BigDecimal minBudget, 
                                      @Param("maxBudget") java.math.BigDecimal maxBudget);
    
    /**
     * Find travel plans by budget less than or equal to
     */
    List<TravelPlan> findByEstimatedBudgetLessThanEqual(java.math.BigDecimal budget);
    
    /**
     * Find travel plans by required languages
     */
    List<TravelPlan> findByRequiredLanguagesContainingIgnoreCase(String language);
    
    /**
     * Find travel plans by communication languages
     */
    List<TravelPlan> findByCommunicationLanguagesContainingIgnoreCase(String language);
    
    /**
     * Find travel plans by title or description containing search term
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE " +
           "LOWER(tp.title) LIKE LOWER(%:searchTerm%) OR " +
           "LOWER(tp.description) LIKE LOWER(%:searchTerm%) OR " +
           "LOWER(tp.destination) LIKE LOWER(%:searchTerm%) OR " +
           "LOWER(tp.originLocation) LIKE LOWER(%:searchTerm%)")
    List<TravelPlan> findBySearchTerm(@Param("searchTerm") String searchTerm);
    
    /**
     * Find travel plans by multiple criteria for filtering
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE " +
           "(:planType IS NULL OR tp.planType = :planType) AND " +
           "(:destination IS NULL OR LOWER(tp.destination) LIKE LOWER(%:destination%)) AND " +
           "(:transportType IS NULL OR tp.transportType = :transportType) AND " +
           "(:genderPreference IS NULL OR tp.genderPreference = :genderPreference) AND " +
           "(:minAge IS NULL OR tp.minAge <= :minAge) AND " +
           "(:maxAge IS NULL OR tp.maxAge >= :maxAge) AND " +
           "(:language IS NULL OR tp.requiredLanguages LIKE %:language% OR tp.communicationLanguages LIKE %:language%) AND " +
           "(:maxBudget IS NULL OR tp.estimatedBudget <= :maxBudget) AND " +
           "tp.visibility = 'PUBLIC' AND tp.planStatus = 'CREATED'")
    Page<TravelPlan> findPublicTravelPlansByFilters(
            @Param("planType") TravelPlan.PlanType planType,
            @Param("destination") String destination,
            @Param("transportType") TravelPlan.TransportType transportType,
            @Param("genderPreference") TravelPlan.GenderPreference genderPreference,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("language") String language,
            @Param("maxBudget") java.math.BigDecimal maxBudget,
            Pageable pageable
    );
    
    /**
     * Find travel plans that need to be started (scheduled start time reached)
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.planStatus = 'NEW' AND tp.startDate <= :currentTime")
    List<TravelPlan> findPlansToStart(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find travel plans that need to be completed (scheduled end time reached)
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.planStatus = 'IN_PROGRESS' AND tp.endDate <= :currentTime")
    List<TravelPlan> findPlansToComplete(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Find travel plans that need to be cancelled (not enough members before start)
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.planStatus = 'CREATED' AND tp.startDate <= :cancellationTime")
    List<TravelPlan> findPlansToCancel(@Param("cancellationTime") LocalDateTime cancellationTime);
    
    /**
     * Find travel plans by owner and status
     */
    List<TravelPlan> findByOwnerAndPlanStatus(User owner, TravelPlan.PlanStatus planStatus);
    
    /**
     * Find travel plans by owner and visibility
     */
    List<TravelPlan> findByOwnerAndVisibility(User owner, TravelPlan.Visibility visibility);
    
    /**
     * Count travel plans by plan status
     */
    long countByPlanStatus(TravelPlan.PlanStatus planStatus);
    
    /**
     * Count travel plans by plan type
     */
    long countByPlanType(TravelPlan.PlanType planType);
    
    /**
     * Count travel plans by destination
     */
    long countByDestination(String destination);
    
    /**
     * Find travel plans created after a specific date
     */
    List<TravelPlan> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find travel plans created by owner in date range
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.owner = :owner AND tp.createdAt BETWEEN :startDate AND :endDate")
    List<TravelPlan> findByOwnerAndCreatedAtBetween(
            @Param("owner") User owner,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    
    /**
     * Find travel plans with specific member count
     */
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.maxMembers = :memberCount")
    List<TravelPlan> findByMemberCount(@Param("memberCount") Integer memberCount);
    
    /**
     * Find travel plans by accommodation type
     */
    List<TravelPlan> findByAccommodationTypeContainingIgnoreCase(String accommodationType);
    
    /**
     * Find travel plans by optional expenses containing search term
     */
    List<TravelPlan> findByOptionalExpensesContainingIgnoreCase(String expense);
    
    // Find all public plans that are new and not associated with the current user
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.visibility = 'PUBLIC' AND tp.planStatus = 'NEW' " +
           "AND tp.id NOT IN (SELECT ups.travelPlan.id FROM UserPlanStatus ups WHERE ups.user.id = :userId)")
    List<TravelPlan> findPublicNewPlansNotAssociatedWithUser(@Param("userId") Long userId);
    
    // Find plans by owner
    List<TravelPlan> findByOwnerOrderByCreatedAtDesc(User owner);
    
    // Find current plans for a user (new or in_progress status)
    @Query("SELECT tp FROM TravelPlan tp JOIN UserPlanStatus ups ON tp.id = ups.travelPlan.id " +
           "WHERE ups.user.id = :userId AND tp.planStatus IN ('NEW', 'IN_PROGRESS') " +
           "AND ups.status IN ('OWNED', 'APPLIED', 'APPLIED_ACCEPTED', 'INVITED', 'INVITED_ACCEPTED')")
    List<TravelPlan> findCurrentPlansForUser(@Param("userId") Long userId);
    
    // Find history plans for a user
    @Query("SELECT tp FROM TravelPlan tp JOIN UserPlanStatus ups ON tp.id = ups.travelPlan.id " +
           "WHERE ups.user.id = :userId AND (tp.planStatus IN ('COMPLETED', 'CANCELLED') " +
           "OR ups.status IN ('APPLIED_CANCELLED', 'APPLIED_REFUSED', 'INVITED_REFUSED'))")
    List<TravelPlan> findHistoryPlansForUser(@Param("userId") Long userId);
    

    
    // Search plans by title or description
    @Query("SELECT tp FROM TravelPlan tp WHERE tp.visibility = 'PUBLIC' AND tp.planStatus = 'NEW' " +
           "AND (tp.title LIKE %:keyword% OR tp.description LIKE %:keyword%) " +
           "AND tp.id NOT IN (SELECT ups.travelPlan.id FROM UserPlanStatus ups WHERE ups.user.id = :userId)")
    List<TravelPlan> searchPublicPlans(@Param("keyword") String keyword, @Param("userId") Long userId);
    
    // Check if user has any current plan
    @Query("SELECT COUNT(tp) > 0 FROM TravelPlan tp JOIN UserPlanStatus ups ON tp.id = ups.travelPlan.id " +
           "WHERE ups.user.id = :userId AND tp.planStatus IN ('NEW', 'IN_PROGRESS') " +
           "AND ups.status IN ('OWNED', 'APPLIED', 'APPLIED_ACCEPTED', 'INVITED', 'INVITED_ACCEPTED')")
    boolean userHasCurrentPlan(@Param("userId") Long userId);
    
    // Find plans by status
    List<TravelPlan> findByPlanStatusOrderByCreatedAtDesc(TravelPlan.PlanStatus planStatus);
    
    // Find plans by owner and status
    List<TravelPlan> findByOwnerAndPlanStatusOrderByCreatedAtDesc(User owner, TravelPlan.PlanStatus planStatus);
} 