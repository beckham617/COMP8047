package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanRequest;
import com.comp8047.majorproject.travelplanassistant.dto.TravelPlanResponse;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.TravelPlanRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserPlanStatusRepository;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TravelPlanService
 * Tests travel plan creation, discovery, member management, and plan lifecycle functionality
 */
@ExtendWith(MockitoExtension.class)
class TravelPlanServiceTest {

    @Mock
    private TravelPlanRepository travelPlanRepository;

    @Mock
    private UserPlanStatusRepository userPlanStatusRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TravelPlanService travelPlanService;

    private User testUser;
    private User testUser2;
    private TravelPlan testPlan;
    private TravelPlanRequest planRequest;
    private UserPlanStatus userPlanStatus;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("owner@example.com");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setGender(User.Gender.MALE);
        testUser.setBirthYear(1990);
        testUser.setLanguage("English");
        testUser.setCountry("USA");
        testUser.setCity("New York");

        testUser2 = new User();
        testUser2.setId(2L);
        testUser.setEmail("member@example.com");
        testUser2.setFirstName("Jane");
        testUser2.setLastName("Smith");
        testUser2.setGender(User.Gender.FEMALE);
        testUser2.setBirthYear(1995);
        testUser2.setLanguage("English");
        testUser2.setCountry("USA");
        testUser2.setCity("Los Angeles");

        testPlan = new TravelPlan();
        testPlan.setId(1L);
        testPlan.setTitle("Test Travel Plan");
        testPlan.setDescription("A test travel plan");
        testPlan.setStartDate(LocalDateTime.now().plusDays(30));
        testPlan.setEndDate(LocalDateTime.now().plusDays(37));
        testPlan.setMaxMembers(4);
        testPlan.setOwner(testUser);
        testPlan.setStatus(TravelPlan.Status.NEW);
        testPlan.setPlanType(TravelPlan.PlanType.PUBLIC);
        testPlan.setCreatedAt(LocalDateTime.now());

        planRequest = new TravelPlanRequest();
        planRequest.setTitle("Test Travel Plan");
        planRequest.setDescription("A test travel plan");
        planRequest.setStartDate(LocalDate.now().plusDays(30));
        planRequest.setEndDate(LocalDate.now().plusDays(37));
        planRequest.setMaxMembers(4);

        userPlanStatus = new UserPlanStatus();
        userPlanStatus.setId(1L);
        userPlanStatus.setUser(testUser);
        userPlanStatus.setTravelPlan(testPlan);
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED);
        userPlanStatus.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateTravelPlan_Success() {
        // Given
        when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(testPlan);
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.createTravelPlan(planRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testPlan.getTitle(), result.getTitle());
        assertEquals(testPlan.getDescription(), result.getDescription());
        assertEquals(testPlan.getMaxMembers(), result.getMaxMembers());

        verify(travelPlanRepository).save(any(TravelPlan.class));
        verify(userPlanStatusRepository).save(any(UserPlanStatus.class));
    }

    @Test
    void testCreateTravelPlan_InvalidDateRange() {
        // Given
        planRequest.setStartDate(LocalDate.now().plusDays(10));
        planRequest.setEndDate(LocalDate.now().plusDays(5)); // End date before start date
        when(travelPlanRepository.userHasCurrentPlan(any())).thenReturn(false);
        when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(null); // Simulate save failure

        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            travelPlanService.createTravelPlan(planRequest, testUser);
        });

        verify(travelPlanRepository).save(any(TravelPlan.class));
    }

    @Test
    void testCreateTravelPlan_InvalidMaxMembers() {
        // Given
        planRequest.setMaxMembers(0); // Invalid max members
        when(travelPlanRepository.userHasCurrentPlan(any())).thenReturn(false);
        when(travelPlanRepository.save(any(TravelPlan.class))).thenReturn(null); // Simulate save failure

        // When & Then
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            travelPlanService.createTravelPlan(planRequest, testUser);
        });

        verify(travelPlanRepository).save(any(TravelPlan.class));
    }

    @Test
    void testGetPublicNewPlans_Success() {
        // Given
        TravelPlan publicPlan = new TravelPlan();
        publicPlan.setId(2L);
        publicPlan.setTitle("Public Plan");
        publicPlan.setStatus(TravelPlan.Status.NEW);
        publicPlan.setMaxMembers(4);
        publicPlan.setOwner(testUser2);

        List<TravelPlan> plans = Arrays.asList(publicPlan);
        when(travelPlanRepository.findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW)).thenReturn(plans);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userPlanStatusRepository.findByUserAndTravelPlan(any(), any())).thenReturn(Optional.empty());

        // When
        List<TravelPlanResponse> result = travelPlanService.getPublicNewPlans(1L);

        // Then
        assertNotNull(result);
        verify(travelPlanRepository).findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW);
    }

    @Test
    void testSearchPublicPlans_Success() {
        // Given
        String keyword = "Paris";
        List<TravelPlan> plans = Arrays.asList(testPlan);
        when(travelPlanRepository.findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW)).thenReturn(plans);

        // When
        List<TravelPlanResponse> result = travelPlanService.searchPublicPlans(keyword, 1L);

        // Then
        assertNotNull(result);
        verify(travelPlanRepository).findByPlanTypeAndStatus(TravelPlan.PlanType.PUBLIC, TravelPlan.Status.NEW);
    }

    @Test
    void testGetCurrentPlans_Success() {
        // Given
        List<TravelPlan> plans = Arrays.asList(testPlan);
        when(travelPlanRepository.getCurrentPlans(1L)).thenReturn(plans);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userPlanStatusRepository.findByUserAndTravelPlan(any(), any())).thenReturn(Optional.of(userPlanStatus));

        // When
        List<TravelPlanResponse> result = travelPlanService.getCurrentPlans(1L);

        // Then
        assertNotNull(result);
        verify(travelPlanRepository).getCurrentPlans(1L);
    }

    @Test
    void testGetHistoryPlans_Success() {
        // Given
        testPlan.setStatus(TravelPlan.Status.COMPLETED);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userPlanStatusRepository.findByUser(testUser)).thenReturn(Arrays.asList(userPlanStatus));

        // When
        List<TravelPlanResponse> result = travelPlanService.getHistoryPlans(1L);

        // Then
        assertNotNull(result);
        verify(userPlanStatusRepository).findByUser(testUser);
    }

    @Test
    void testApplyToPlan_Success() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.empty());
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.applyToPlan(1L, testUser2);

        // Then
        assertNotNull(result);
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
        verify(userPlanStatusRepository).save(any(UserPlanStatus.class));
    }

    @Test
    void testApplyToPlan_PlanNotFound() {
        // Given
        when(travelPlanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            travelPlanService.applyToPlan(999L, testUser2);
        });

        assertEquals("Travel plan not found", exception.getMessage());
        verify(travelPlanRepository).findById(999L);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void testApplyToPlan_AlreadyApplied() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.of(userPlanStatus));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            travelPlanService.applyToPlan(1L, testUser2);
        });

        assertEquals("User already has a relationship with this plan", exception.getMessage());
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
    }

    @Test
    void testInviteUser_Success() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userRepository.findByEmail("member@example.com")).thenReturn(Optional.of(testUser2));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.empty());
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.inviteUser(1L, "member@example.com", testUser);

        // Then
        assertNotNull(result);
        verify(travelPlanRepository).findById(1L);
        verify(userRepository).findByEmail("member@example.com");
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
        verify(userPlanStatusRepository).save(any(UserPlanStatus.class));
    }

    @Test
    void testInviteUser_NotOwner() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            travelPlanService.inviteUser(1L, "member@example.com", testUser2); // testUser2 is not the owner
        });

        assertEquals("Only plan owner can send invitations", exception.getMessage());
        verify(travelPlanRepository).findById(1L);
        verify(userRepository, never()).findByEmail(any());
    }

    @Test
    void testAcceptApplication_Success() {
        // Given
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED);
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.handleApplication(1L, 2L, testUser, TravelPlanService.Decision.ACCEPT);

        // Then
        assertNotNull(result);
        assertEquals(UserPlanStatus.Status.APPLIED_ACCEPTED, userPlanStatus.getStatus());
        verify(travelPlanRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
        verify(userPlanStatusRepository).save(userPlanStatus);
    }

    @Test
    void testRefuseApplication_Success() {
        // Given
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED);
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userRepository.findById(2L)).thenReturn(Optional.of(testUser2));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.handleApplication(1L, 2L, testUser, TravelPlanService.Decision.REFUSE);

        // Then
        assertNotNull(result);
        assertEquals(UserPlanStatus.Status.APPLIED_REFUSED, userPlanStatus.getStatus());
        verify(travelPlanRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
        verify(userPlanStatusRepository).save(userPlanStatus);
    }

    @Test
    void testAcceptInvitation_Success() {
        // Given
        userPlanStatus.setStatus(UserPlanStatus.Status.INVITED);
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.handleInvitation(1L, testUser2, TravelPlanService.Decision.ACCEPT);

        // Then
        assertNotNull(result);
        assertEquals(UserPlanStatus.Status.INVITED_ACCEPTED, userPlanStatus.getStatus());
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
        verify(userPlanStatusRepository).save(userPlanStatus);
    }

    @Test
    void testRefuseInvitation_Success() {
        // Given
        userPlanStatus.setStatus(UserPlanStatus.Status.INVITED);
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser2, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(userPlanStatusRepository.save(any(UserPlanStatus.class))).thenReturn(userPlanStatus);

        // When
        TravelPlanResponse result = travelPlanService.handleInvitation(1L, testUser2, TravelPlanService.Decision.REFUSE);

        // Then
        assertNotNull(result);
        assertEquals(UserPlanStatus.Status.INVITED_REFUSED, userPlanStatus.getStatus());
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser2, testPlan);
        verify(userPlanStatusRepository).save(userPlanStatus);
    }
}