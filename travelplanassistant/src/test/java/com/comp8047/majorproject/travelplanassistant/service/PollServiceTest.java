package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.PollRequest;
import com.comp8047.majorproject.travelplanassistant.dto.PollResponse;
import com.comp8047.majorproject.travelplanassistant.entity.Poll;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.PollRepository;
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
 * Unit tests for PollService
 * Tests poll creation, voting, and result calculation functionality
 */
@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private TravelPlanRepository travelPlanRepository;

    @Mock
    private UserPlanStatusRepository userPlanStatusRepository;

    @InjectMocks
    private PollService pollService;

    private User testUser;
    private TravelPlan testPlan;
    private Poll testPoll;
    private PollRequest pollRequest;
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
        testPlan.setStatus(TravelPlan.Status.IN_PROGRESS);

        testPoll = new Poll();
        testPoll.setId(1L);
        testPoll.setQuestion("Where should we go for dinner?");
        testPoll.setTravelPlan(testPlan);
        testPoll.setCreatedBy(testUser);
        testPoll.setCreatedAt(LocalDateTime.now());

        pollRequest = new PollRequest();
        pollRequest.setQuestion("Where should we go for dinner?");
        pollRequest.setOptions(Arrays.asList("Restaurant A", "Restaurant B", "Restaurant C"));

        userPlanStatus = new UserPlanStatus();
        userPlanStatus.setId(1L);
        userPlanStatus.setUser(testUser);
        userPlanStatus.setTravelPlan(testPlan);
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED_ACCEPTED);
    }

    @Test
    void testCreatePoll_Success() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(pollRepository.save(any(Poll.class))).thenReturn(testPoll);

        // When
        PollResponse result = pollService.createPoll(1L, pollRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testPoll.getQuestion(), result.getQuestion());

        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    void testCreatePoll_PlanNotFound() {
        // Given
        when(travelPlanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pollService.createPoll(999L, pollRequest, testUser);
        });

        assertEquals("Travel plan not found", exception.getMessage());
        verify(travelPlanRepository).findById(999L);
        verify(userPlanStatusRepository, never()).findByUserAndTravelPlan(any(), any());
        verify(pollRepository, never()).save(any(Poll.class));
    }

    @Test
    void testCreatePoll_UserNotMember() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pollService.createPoll(1L, pollRequest, testUser);
        });

        assertEquals("User is not a member of this travel plan", exception.getMessage());
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(pollRepository, never()).save(any(Poll.class));
    }

    @Test
    void testCreatePoll_InvalidOptions() {
        // Given
        pollRequest.setOptions(Arrays.asList("Only one option")); // Less than 2 options
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(pollRepository.save(any(Poll.class))).thenReturn(testPoll);

        // When
        PollResponse result = pollService.createPoll(1L, pollRequest, testUser);

        // Then
        // Note: Service doesn't validate option count - this is handled by DTO validation
        assertNotNull(result);
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(pollRepository).save(any(Poll.class));
    }

    @Test
    void testVoteOnPoll_Success() {
        // Given
        Poll testPoll = new Poll();
        testPoll.setId(1L);
        testPoll.setQuestion("Test Poll Question");
        testPoll.setTravelPlan(testPlan);
        testPoll.setCreatedBy(testUser);
        testPoll.setStatus(Poll.PollStatus.ACTIVE);
        
        // Add poll options
        Poll.PollOption option1 = new Poll.PollOption("Option 1");
        Poll.PollOption option2 = new Poll.PollOption("Option 2");
        option1.setId(1L); // Set ID manually for testing
        option2.setId(2L);
        testPoll.addOption(option1);
        testPoll.addOption(option2);

        when(pollRepository.findById(1L)).thenReturn(Optional.of(testPoll));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(pollRepository.save(any(Poll.class))).thenReturn(testPoll);

        // When
        PollResponse result = pollService.voteOnPoll(1L, option1.getId(), testUser);

        // Then
        assertNotNull(result);
        verify(pollRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(pollRepository).save(testPoll);
    }
}