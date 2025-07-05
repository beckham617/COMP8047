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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private ChatMessageRepository chatMessageRepository;
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private UserPlanStatusRepository userPlanStatusRepository;
    
    /**
     * Send a chat message
     */
    public ChatMessageResponse sendMessage(Long planId, ChatMessageRequest request, User sender) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is a member of the plan
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(sender, plan);
        if (userStatusOpt.isEmpty()) {
            throw new IllegalStateException("User is not a member of this travel plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (!isActiveMember(userStatus.getStatus())) {
            throw new IllegalStateException("User is not an active member of this travel plan");
        }
        
        ChatMessage message = new ChatMessage();
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setTravelPlan(plan);
        message.setSender(sender);
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        return convertToResponse(savedMessage);
    }
    
    /**
     * Get all messages for a travel plan
     */
    public List<ChatMessageResponse> getMessages(Long planId, User user) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is a member of the plan
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        if (userStatusOpt.isEmpty()) {
            throw new IllegalStateException("User is not a member of this travel plan");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findByTravelPlanOrderByCreatedAtAsc(plan);
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent messages for a travel plan (last N messages)
     */
    public List<ChatMessageResponse> getRecentMessages(Long planId, int limit, User user) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is a member of the plan
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        if (userStatusOpt.isEmpty()) {
            throw new IllegalStateException("User is not a member of this travel plan");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findRecentMessagesByTravelPlan(plan);
        return messages.stream()
                .limit(limit)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get messages after a specific time
     */
    public List<ChatMessageResponse> getMessagesAfter(Long planId, LocalDateTime after, User user) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is a member of the plan
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, plan);
        if (userStatusOpt.isEmpty()) {
            throw new IllegalStateException("User is not a member of this travel plan");
        }
        
        List<ChatMessage> messages = chatMessageRepository.findByTravelPlanAndCreatedAtAfterOrderByCreatedAtAsc(plan, after);
        return messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete a chat message (only sender can delete)
     */
    public void deleteMessage(Long messageId, User user) {
        Optional<ChatMessage> messageOpt = chatMessageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("Message not found");
        }
        
        ChatMessage message = messageOpt.get();
        
        // Check if user is the sender
        if (!message.getSender().getId().equals(user.getId())) {
            throw new IllegalStateException("Only message sender can delete the message");
        }
        
        chatMessageRepository.delete(message);
    }
    
    /**
     * Check if user is an active member of the plan
     */
    private boolean isActiveMember(UserPlanStatus.Status status) {
        return status == UserPlanStatus.Status.OWNED ||
               status == UserPlanStatus.Status.APPLIED_ACCEPTED ||
               status == UserPlanStatus.Status.INVITED_ACCEPTED;
    }
    
    /**
     * Convert ChatMessage to ChatMessageResponse
     */
    private ChatMessageResponse convertToResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setUserId(message.getSender().getId());
        response.setUserName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        response.setUserAvatar(message.getSender().getProfilePicture());
        response.setTravelPlanId(message.getTravelPlan().getId());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());
        
        return response;
    }
} 