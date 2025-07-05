package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPlanStatusRepository extends JpaRepository<UserPlanStatus, Long> {
    
    /**
     * Find user plan status by user and travel plan
     */
    Optional<UserPlanStatus> findByUserAndTravelPlan(User user, TravelPlan travelPlan);
    
    /**
     * Find all user plan statuses by user
     */
    List<UserPlanStatus> findByUser(User user);
    
    /**
     * Find all user plan statuses by travel plan
     */
    List<UserPlanStatus> findByTravelPlan(TravelPlan travelPlan);
    
    /**
     * Find user plan statuses by status
     */
    List<UserPlanStatus> findByStatus(UserPlanStatus.Status status);
    
    /**
     * Find user plan statuses by user and status
     */
    List<UserPlanStatus> findByUserAndStatus(User user, UserPlanStatus.Status status);
    
    /**
     * Find user plan statuses by travel plan and status
     */
    List<UserPlanStatus> findByTravelPlanAndStatus(TravelPlan travelPlan, UserPlanStatus.Status status);
    
    /**
     * Find active user plan statuses (OWNED or ACCEPTED)
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.status IN ('OWNED', 'ACCEPTED')")
    List<UserPlanStatus> findActiveUserPlanStatuses();
    
    /**
     * Find active user plan statuses for a specific user
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('OWNED', 'ACCEPTED')")
    List<UserPlanStatus> findActiveUserPlanStatusesByUser(@Param("user") User user);
    
    /**
     * Find active user plan statuses for a specific travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'ACCEPTED')")
    List<UserPlanStatus> findActiveUserPlanStatusesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find pending user plan statuses (APPLIED or INVITED)
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.status IN ('APPLIED', 'INVITED')")
    List<UserPlanStatus> findPendingUserPlanStatuses();
    
    /**
     * Find pending user plan statuses for a specific user
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('APPLIED', 'INVITED')")
    List<UserPlanStatus> findPendingUserPlanStatusesByUser(@Param("user") User user);
    
    /**
     * Find pending user plan statuses for a specific travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('APPLIED', 'INVITED')")
    List<UserPlanStatus> findPendingUserPlanStatusesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find user plan statuses by user and travel plan status
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan.planStatus = :planStatus")
    List<UserPlanStatus> findByUserAndTravelPlanStatus(@Param("user") User user, @Param("planStatus") TravelPlan.PlanStatus planStatus);
    
    /**
     * Find user plan statuses by travel plan and user plan status
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = :status")
    List<UserPlanStatus> findByTravelPlanAndUserPlanStatus(@Param("travelPlan") TravelPlan travelPlan, @Param("status") UserPlanStatus.Status status);
    
    /**
     * Count active members for a travel plan
     */
    @Query("SELECT COUNT(ups) FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'ACCEPTED')")
    long countActiveMembersByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Count pending applications for a travel plan
     */
    @Query("SELECT COUNT(ups) FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = 'APPLIED'")
    long countPendingApplicationsByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Count pending invitations for a travel plan
     */
    @Query("SELECT COUNT(ups) FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = 'INVITED'")
    long countPendingInvitationsByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find user plan statuses created after a specific date
     */
    List<UserPlanStatus> findByCreatedAtAfter(LocalDateTime date);
    
    /**
     * Find user plan statuses updated after a specific date
     */
    List<UserPlanStatus> findByUpdatedAtAfter(LocalDateTime date);
    
    /**
     * Find user plan statuses where application was submitted after a specific date
     */
    List<UserPlanStatus> findByAppliedAtAfter(LocalDateTime date);
    
    /**
     * Find user plan statuses where invitation was sent after a specific date
     */
    List<UserPlanStatus> findByInvitedAtAfter(LocalDateTime date);
    
    /**
     * Find user plan statuses where application/invitation was accepted after a specific date
     */
    List<UserPlanStatus> findByAcceptedAtAfter(LocalDateTime date);
    
    /**
     * Find user plan statuses where application/invitation was refused after a specific date
     */
    List<UserPlanStatus> findByRefusedAtAfter(LocalDateTime date);
    
    /**
     * Check if user has any active travel plans
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('OWNED', 'ACCEPTED')")
    boolean hasActiveTravelPlans(@Param("user") User user);
    
    /**
     * Check if user has any pending travel plans
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('APPLIED', 'INVITED')")
    boolean hasPendingTravelPlans(@Param("user") User user);
    
    /**
     * Check if user is owner of a specific travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status = 'OWNED'")
    boolean isOwnerOfTravelPlan(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user is member of a specific travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'ACCEPTED')")
    boolean isMemberOfTravelPlan(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user has applied to a specific travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status = 'APPLIED'")
    boolean hasAppliedToTravelPlan(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user has been invited to a specific travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status = 'INVITED'")
    boolean hasBeenInvitedToTravelPlan(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find all members of a travel plan (owners and accepted members)
     */
    @Query("SELECT ups.user FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'ACCEPTED')")
    List<User> findMembersByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find all applicants for a travel plan
     */
    @Query("SELECT ups.user FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = 'APPLIED'")
    List<User> findApplicantsByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find all invitees for a travel plan
     */
    @Query("SELECT ups.user FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = 'INVITED'")
    List<User> findInviteesByTravelPlan(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Delete all user plan statuses for a specific travel plan
     */
    void deleteByTravelPlan(TravelPlan travelPlan);
    
    /**
     * Delete all user plan statuses for a specific user
     */
    void deleteByUser(User user);
    
    /**
     * Find all statuses for a specific travel plan ordered by creation date
     */
    List<UserPlanStatus> findByTravelPlanOrderByCreatedAtDesc(TravelPlan travelPlan);
    
    /**
     * Find all statuses for a specific user ordered by creation date
     */
    List<UserPlanStatus> findByUserOrderByCreatedAtDesc(User user);
    
    /**
     * Find user plan status by user, travel plan and status
     */
    Optional<UserPlanStatus> findByUserAndTravelPlanAndStatus(User user, TravelPlan travelPlan, UserPlanStatus.Status status);
    
    /**
     * Find pending applications for a travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = 'APPLIED'")
    List<UserPlanStatus> findPendingApplications(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find pending invitations for a travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status = 'INVITED'")
    List<UserPlanStatus> findPendingInvitations(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find accepted members for a travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED')")
    List<UserPlanStatus> findAcceptedMembers(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find all active members for a travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED')")
    List<UserPlanStatus> findActiveMembers(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Count active members for a travel plan
     */
    @Query("SELECT COUNT(ups) FROM UserPlanStatus ups WHERE ups.travelPlan = :travelPlan AND ups.status IN ('OWNED', 'APPLIED_ACCEPTED', 'INVITED_ACCEPTED')")
    long countActiveMembers(@Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user is owner of travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status = 'OWNED'")
    boolean isUserOwner(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user has applied to travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status = 'APPLIED'")
    boolean hasUserApplied(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user has been invited to travel plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.travelPlan = :travelPlan AND ups.status = 'INVITED'")
    boolean hasUserBeenInvited(@Param("user") User user, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Find current plans for user
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('OWNED', 'APPLIED', 'APPLIED_ACCEPTED', 'INVITED', 'INVITED_ACCEPTED')")
    List<UserPlanStatus> findCurrentPlansForUser(@Param("user") User user);
    
    /**
     * Find history plans for user
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('APPLIED_CANCELLED', 'APPLIED_REFUSED', 'INVITED_REFUSED')")
    List<UserPlanStatus> findHistoryPlansForUser(@Param("user") User user);
    
    /**
     * Find user plan status by user email and travel plan
     */
    @Query("SELECT ups FROM UserPlanStatus ups WHERE ups.user.email = :email AND ups.travelPlan = :travelPlan")
    Optional<UserPlanStatus> findByUserEmailAndTravelPlan(@Param("email") String email, @Param("travelPlan") TravelPlan travelPlan);
    
    /**
     * Check if user has any current plan
     */
    @Query("SELECT COUNT(ups) > 0 FROM UserPlanStatus ups WHERE ups.user = :user AND ups.status IN ('OWNED', 'APPLIED', 'APPLIED_ACCEPTED', 'INVITED', 'INVITED_ACCEPTED')")
    boolean userHasCurrentPlan(@Param("user") User user);
} 