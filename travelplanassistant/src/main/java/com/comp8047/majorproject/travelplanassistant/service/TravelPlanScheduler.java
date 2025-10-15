package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TravelPlanScheduler {

    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private UserPlanStatusRepository userPlanStatusRepository;
    
    @Autowired
    private NotificationService notificationService;

    // Run twice a day at 01:00 and 13:00 server time
    @Scheduled(cron = "0 0 1,13 * * *")
    public void startPlansForToday() {
        List<TravelPlan> plans = travelPlanRepository.findPlansToStartToday();
        for (TravelPlan plan : plans) {
            // Automatically refuse all pending applications and invitations before starting the plan
            refuseAllPendingApplicationsAndInvitations(plan);
            
            // Start the plan
            plan.setStatus(TravelPlan.Status.IN_PROGRESS);
            travelPlanRepository.save(plan);
            
            // Send start reminder emails
            try {
                notificationService.sendTravelPlanStartReminder(plan);
            } catch (Exception e) {
                System.err.println("Failed to send start reminder for plan " + plan.getId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Automatically refuse all pending applications and invitations for a travel plan
     */
    private void refuseAllPendingApplicationsAndInvitations(TravelPlan plan) {
        // Get all pending applications and invitations
        List<UserPlanStatus> pendingApplications = userPlanStatusRepository.findPendingApplications(plan);
        List<UserPlanStatus> pendingInvitations = userPlanStatusRepository.findPendingInvitations(plan);
        
        // Refuse all pending applications
        for (UserPlanStatus application : pendingApplications) {
            application.setStatus(UserPlanStatus.Status.APPLIED_REFUSED);
            userPlanStatusRepository.save(application);
        }
        
        // Refuse all pending invitations
        for (UserPlanStatus invitation : pendingInvitations) {
            invitation.setStatus(UserPlanStatus.Status.INVITED_REFUSED);
            userPlanStatusRepository.save(invitation);
        }
    }

    // Run twice a day at 01:10 and 13:10 server time
    @Scheduled(cron = "0 10 1,13 * * *")
    public void completePlansForToday() {
        List<TravelPlan> plans = travelPlanRepository.findPlansToCompleteToday();
        for (TravelPlan plan : plans) {
            plan.setStatus(TravelPlan.Status.COMPLETED);
            travelPlanRepository.save(plan);
            
            // Send completion reminder emails
            try {
                notificationService.sendTravelPlanCompletionReminder(plan);
            } catch (Exception e) {
                System.err.println("Failed to send completion reminder for plan " + plan.getId() + ": " + e.getMessage());
            }
        }
    }
}


