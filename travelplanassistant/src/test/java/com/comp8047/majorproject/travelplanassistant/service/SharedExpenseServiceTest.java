package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.SharedExpenseRequest;
import com.comp8047.majorproject.travelplanassistant.dto.SharedExpenseResponse;
import com.comp8047.majorproject.travelplanassistant.entity.SharedExpense;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.SharedExpenseRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SharedExpenseService
 * Tests shared expense creation, management, and calculation functionality
 */
@ExtendWith(MockitoExtension.class)
class SharedExpenseServiceTest {

    @Mock
    private SharedExpenseRepository sharedExpenseRepository;

    @Mock
    private TravelPlanRepository travelPlanRepository;

    @Mock
    private UserPlanStatusRepository userPlanStatusRepository;

    @InjectMocks
    private SharedExpenseService sharedExpenseService;

    private User testUser;
    private User testUser2;
    private TravelPlan testPlan;
    private SharedExpense testExpense;
    private SharedExpenseRequest expenseRequest;
    private UserPlanStatus userPlanStatus;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");

        testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setEmail("user2@example.com");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");

        testPlan = new TravelPlan();
        testPlan.setId(1L);
        testPlan.setTitle("Test Travel Plan");
        testPlan.setStatus(TravelPlan.Status.IN_PROGRESS);

        userPlanStatus = new UserPlanStatus();
        userPlanStatus.setId(1L);
        userPlanStatus.setUser(testUser);
        userPlanStatus.setTravelPlan(testPlan);
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED_ACCEPTED);

        testPlan.setUserPlanStatuses(Arrays.asList(userPlanStatus));

        testExpense = new SharedExpense();
        testExpense.setId(1L);
        testExpense.setDescription("Dinner at Restaurant");
        testExpense.setTravelPlan(testPlan);
        testExpense.setPaidBy(testUser);
        testExpense.setCreatedAt(LocalDateTime.now());

        expenseRequest = new SharedExpenseRequest();
        expenseRequest.setDescription("Dinner at Restaurant");
    }

    @Test
    void testCreateSharedExpense_Success() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(sharedExpenseRepository.save(any(SharedExpense.class))).thenReturn(testExpense);

        // When
        SharedExpenseResponse result = sharedExpenseService.createSharedExpense(1L, expenseRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testExpense.getDescription(), result.getDescription());

        verify(travelPlanRepository).findById(1L);
        verify(sharedExpenseRepository).save(any(SharedExpense.class));
    }

    @Test
    void testCreateSharedExpense_PlanNotFound() {
        // Given
        when(travelPlanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sharedExpenseService.createSharedExpense(999L, expenseRequest, testUser);
        });

        assertEquals("Travel plan not found", exception.getMessage());
        verify(travelPlanRepository).findById(999L);
        verify(userPlanStatusRepository, never()).findByUserAndTravelPlan(any(), any());
        verify(sharedExpenseRepository, never()).save(any(SharedExpense.class));
    }

    @Test
    void testCreateSharedExpense_UserNotMember() {
        // Given
        // Create a plan without the user as a member
        TravelPlan planWithoutUser = new TravelPlan();
        planWithoutUser.setId(2L);
        planWithoutUser.setTitle("Plan Without User");
        planWithoutUser.setStatus(TravelPlan.Status.IN_PROGRESS);
        planWithoutUser.setUserPlanStatuses(Arrays.asList()); // Empty list - user is not a member
        
        when(travelPlanRepository.findById(2L)).thenReturn(Optional.of(planWithoutUser));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sharedExpenseService.createSharedExpense(2L, expenseRequest, testUser);
        });

        assertEquals("You are not a member of this travel plan", exception.getMessage());
        verify(travelPlanRepository).findById(2L);
        verify(sharedExpenseRepository, never()).save(any(SharedExpense.class));
    }

}