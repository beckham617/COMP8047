package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.ChatMessageRequest;
import com.comp8047.majorproject.travelplanassistant.dto.ChatMessageResponse;
import com.comp8047.majorproject.travelplanassistant.entity.ChatMessage;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.ChatMessageRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ChatService
 * Tests chat message sending, retrieval, and member validation functionality
 */
@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;

    @Mock
    private TravelPlanRepository travelPlanRepository;

    @Mock
    private UserPlanStatusRepository userPlanStatusRepository;

    @InjectMocks
    private ChatService chatService;

    private User testUser;
    private TravelPlan testPlan;
    private ChatMessage testMessage;
    private ChatMessageRequest messageRequest;
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
        testPlan.setStatus(TravelPlan.Status.NEW);

        testMessage = new ChatMessage();
        testMessage.setId(1L);
        testMessage.setContent("Hello everyone!");
        testMessage.setMessageType(ChatMessage.MessageType.TEXT);
        testMessage.setTravelPlan(testPlan);
        testMessage.setSender(testUser);
        testMessage.setCreatedAt(LocalDateTime.now());

        messageRequest = new ChatMessageRequest();
        messageRequest.setContent("Hello everyone!");
        messageRequest.setMessageType(ChatMessage.MessageType.TEXT);

        userPlanStatus = new UserPlanStatus();
        userPlanStatus.setId(1L);
        userPlanStatus.setUser(testUser);
        userPlanStatus.setTravelPlan(testPlan);
        userPlanStatus.setStatus(UserPlanStatus.Status.APPLIED_ACCEPTED);
    }

    @Test
    void testSendMessage_Success() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(testMessage);

        // When
        ChatMessageResponse result = chatService.sendMessage(1L, messageRequest, testUser);

        // Then
        assertNotNull(result);
        assertEquals(testMessage.getContent(), result.getContent());
        assertEquals(testMessage.getMessageType(), result.getMessageType());

        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    void testSendMessage_PlanNotFound() {
        // Given
        when(travelPlanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            chatService.sendMessage(999L, messageRequest, testUser);
        });

        assertEquals("Travel plan not found", exception.getMessage());
        verify(travelPlanRepository).findById(999L);
        verify(userPlanStatusRepository, never()).findByUserAndTravelPlan(any(), any());
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    void testSendMessage_UserNotMember() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            chatService.sendMessage(1L, messageRequest, testUser);
        });

        assertEquals("User is not a member of this travel plan", exception.getMessage());
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(chatMessageRepository, never()).save(any(ChatMessage.class));
    }

    @Test
    void testGetMessages_Success() {
        // Given
        ChatMessage message2 = new ChatMessage();
        message2.setId(2L);
        message2.setContent("How is everyone?");
        message2.setMessageType(ChatMessage.MessageType.TEXT);
        message2.setTravelPlan(testPlan);
        message2.setSender(testUser);
        message2.setCreatedAt(LocalDateTime.now().plusMinutes(5));

        List<ChatMessage> messages = Arrays.asList(testMessage, message2);
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.of(userPlanStatus));
        when(chatMessageRepository.findByTravelPlanOrderByCreatedAtAsc(testPlan)).thenReturn(messages);

        // When
        List<ChatMessageResponse> result = chatService.getMessages(1L, testUser);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Hello everyone!", result.get(0).getContent());
        assertEquals("How is everyone?", result.get(1).getContent());

        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(chatMessageRepository).findByTravelPlanOrderByCreatedAtAsc(testPlan);
    }

    @Test
    void testGetMessages_PlanNotFound() {
        // Given
        when(travelPlanRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            chatService.getMessages(999L, testUser);
        });

        assertEquals("Travel plan not found", exception.getMessage());
        verify(travelPlanRepository).findById(999L);
        verify(userPlanStatusRepository, never()).findByUserAndTravelPlan(any(), any());
        verify(chatMessageRepository, never()).findByTravelPlanOrderByCreatedAtAsc(any());
    }

    @Test
    void testGetMessages_UserNotMember() {
        // Given
        when(travelPlanRepository.findById(1L)).thenReturn(Optional.of(testPlan));
        when(userPlanStatusRepository.findByUserAndTravelPlan(testUser, testPlan)).thenReturn(Optional.empty());

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            chatService.getMessages(1L, testUser);
        });

        assertEquals("User is not a member of this travel plan", exception.getMessage());
        verify(travelPlanRepository).findById(1L);
        verify(userPlanStatusRepository).findByUserAndTravelPlan(testUser, testPlan);
        verify(chatMessageRepository, never()).findByTravelPlanOrderByCreatedAtAsc(any());
    }
}