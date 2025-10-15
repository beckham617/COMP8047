package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TravelPlanScheduler
 * Tests CRON job functionality for automatic plan start and completion
 */
@ExtendWith(MockitoExtension.class)
class TravelPlanSchedulerTest {

    @Mock
    private TravelPlanRepository travelPlanRepository;

    @Mock
    private UserPlanStatusRepository userPlanStatusRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TravelPlanScheduler travelPlanScheduler;

    private TravelPlan testPlan;
    private User testUser;
    private UserPlanStatus userPlanStatus;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testPlan = new TravelPlan();
        testPlan.setId(1L);
        testPlan.setTitle("Test Travel Plan");
        testPlan.setDescription("A test travel plan");
        testPlan.setStartDate(LocalDateTime.now());
        testPlan.setEndDate(LocalDateTime.now().plusDays(7));
        testPlan.setStatus(TravelPlan.Status.NEW);
        testPlan.setOwner(testUser);

        userPlanStatus = new UserPlanStatus();
        userPlanStatus.setId(1L);
        userPlanStatus.setUser(testUser);
        userPlanStatus.setTravelPlan(testPlan);
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED);
        userPlanStatus.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testStartPlans_Success() {
        // Given
        List<TravelPlan> plansToStart = Arrays.asList(testPlan);
        List<UserPlanStatus> pendingStatuses = Arrays.asList(userPlanStatus);

        when(travelPlanRepository.findPlansToStartToday()).thenReturn(plansToStart);
        when(userPlanStatusRepository.findPendingApplications(any(TravelPlan.class))).thenReturn(Arrays.asList());
        when(userPlanStatusRepository.findPendingInvitations(any(TravelPlan.class))).thenReturn(Arrays.asList());
        when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(testPlan);

        // When
        travelPlanScheduler.startPlansForToday();

        // Then
        verify(travelPlanRepository).findPlansToStartToday();
        verify(travelPlanRepository).save(any(TravelPlan.class));
    }

    @Test
    void testStartPlans_NoPlansToStart() {
        // Given
        List<TravelPlan> emptyPlans = Arrays.asList();
        when(travelPlanRepository.findPlansToStartToday()).thenReturn(emptyPlans);

        // When
        travelPlanScheduler.startPlansForToday();

        // Then
        verify(travelPlanRepository).findPlansToStartToday();
        verify(travelPlanRepository, never()).save(any(TravelPlan.class));
    }

    @Test
    void testCompletePlans_Success() {
        // Given
        testPlan.setStatus(TravelPlan.Status.IN_PROGRESS);
        testPlan.setEndDate(LocalDateTime.now().minusDays(1)); // Plan ended yesterday
        List<TravelPlan> plansToComplete = Arrays.asList(testPlan);

        when(travelPlanRepository.findPlansToCompleteToday()).thenReturn(plansToComplete);
        when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(testPlan);

        // When
        travelPlanScheduler.completePlansForToday();

        // Then
        verify(travelPlanRepository).findPlansToCompleteToday();
        verify(travelPlanRepository).save(any(TravelPlan.class));
    }

    @Test
    void testCompletePlans_NoPlansToComplete() {
        // Given
        List<TravelPlan> emptyPlans = Arrays.asList();

        when(travelPlanRepository.findPlansToCompleteToday()).thenReturn(emptyPlans);

        // When
        travelPlanScheduler.completePlansForToday();

        // Then
        verify(travelPlanRepository).findPlansToCompleteToday();
        verify(travelPlanRepository, never()).save(any(TravelPlan.class));
    }
}