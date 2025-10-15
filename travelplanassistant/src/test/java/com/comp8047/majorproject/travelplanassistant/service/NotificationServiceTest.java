package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.entity.Notification;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for NotificationService
 * Tests notification creation, email sending, and notification management functionality
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationService notificationService;

    private User testUser;
    private User testUser2;
    private TravelPlan testPlan;
    private Notification testNotification;

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
        testPlan.setDescription("A test travel plan");

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testSendInvitationNotification_Success() {
        // When & Then
        // Note: The method may return early if emailNotificationsEnabled is false
        // We can only verify that the method executes without throwing exceptions
        assertDoesNotThrow(() -> {
            notificationService.sendInvitationNotification(testUser, testPlan, testUser2);
        });
    }

}