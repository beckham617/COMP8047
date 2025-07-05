package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.PollCreateRequest;
import com.comp8047.majorproject.travelplanassistant.dto.PollResponse;
import com.comp8047.majorproject.travelplanassistant.entity.Poll;
import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;
import com.comp8047.majorproject.travelplanassistant.repository.PollRepository;
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
public class PollService {
    
    @Autowired
    private PollRepository pollRepository;
    
    @Autowired
    private TravelPlanRepository travelPlanRepository;
    
    @Autowired
    private UserPlanStatusRepository userPlanStatusRepository;
    
    /**
     * Create a new poll
     */
    public PollResponse createPoll(Long planId, PollCreateRequest request, User creator) {
        Optional<TravelPlan> planOpt = travelPlanRepository.findById(planId);
        if (planOpt.isEmpty()) {
            throw new IllegalArgumentException("Travel plan not found");
        }
        
        TravelPlan plan = planOpt.get();
        
        // Check if user is a member of the plan
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(creator, plan);
        if (userStatusOpt.isEmpty()) {
            throw new IllegalStateException("User is not a member of this travel plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (!isActiveMember(userStatus.getStatus())) {
            throw new IllegalStateException("User is not an active member of this travel plan");
        }
        
        Poll poll = new Poll();
        poll.setQuestion(request.getQuestion());
        poll.setTravelPlan(plan);
        poll.setCreatedBy(creator);
        poll.setStartTime(LocalDateTime.now());
        poll.setEndTime(LocalDateTime.now().plusDays(7)); // Default 7 days duration
        poll.setStatus(Poll.PollStatus.ACTIVE);
        
        // Add options
        for (String optionText : request.getOptions()) {
            Poll.PollOption option = new Poll.PollOption(optionText);
            poll.addOption(option);
        }
        
        Poll savedPoll = pollRepository.save(poll);
        
        return convertToResponse(savedPoll);
    }
    
    /**
     * Get all polls for a travel plan
     */
    public List<PollResponse> getPolls(Long planId, User user) {
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
        
        List<Poll> polls = pollRepository.findByTravelPlanOrderByCreatedAtDesc(plan);
        return polls.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active polls for a travel plan
     */
    public List<PollResponse> getActivePolls(Long planId, User user) {
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
        
        List<Poll> polls = pollRepository.findActivePollsByTravelPlan(plan);
        return polls.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Vote on a poll option
     */
    public PollResponse voteOnPoll(Long pollId, Long optionId, User user) {
        Optional<Poll> pollOpt = pollRepository.findById(pollId);
        if (pollOpt.isEmpty()) {
            throw new IllegalArgumentException("Poll not found");
        }
        
        Poll poll = pollOpt.get();
        
        // Check if user is a member of the plan
        Optional<UserPlanStatus> userStatusOpt = userPlanStatusRepository.findByUserAndTravelPlan(user, poll.getTravelPlan());
        if (userStatusOpt.isEmpty()) {
            throw new IllegalStateException("User is not a member of this travel plan");
        }
        
        UserPlanStatus userStatus = userStatusOpt.get();
        if (!isActiveMember(userStatus.getStatus())) {
            throw new IllegalStateException("User is not an active member of this travel plan");
        }
        
        // Check if poll is active
        if (!poll.getIsActive()) {
            throw new IllegalStateException("Poll is not active");
        }
        
        // Check if user has already voted
        if (poll.hasUserVoted(user)) {
            throw new IllegalStateException("User has already voted on this poll");
        }
        
        // Add vote
        poll.addVote(user, optionId);
        Poll savedPoll = pollRepository.save(poll);
        
        return convertToResponse(savedPoll);
    }
    
    /**
     * Deactivate a poll (creator only)
     */
    public PollResponse deactivatePoll(Long pollId, User user) {
        Optional<Poll> pollOpt = pollRepository.findById(pollId);
        if (pollOpt.isEmpty()) {
            throw new IllegalArgumentException("Poll not found");
        }
        
        Poll poll = pollOpt.get();
        
        // Check if user is the creator
        if (!poll.getCreator().getId().equals(user.getId())) {
            throw new IllegalStateException("Only poll creator can deactivate the poll");
        }
        
        poll.setIsActive(false);
        Poll savedPoll = pollRepository.save(poll);
        
        return convertToResponse(savedPoll);
    }
    
    /**
     * Delete a poll (creator only)
     */
    public void deletePoll(Long pollId, User user) {
        Optional<Poll> pollOpt = pollRepository.findById(pollId);
        if (pollOpt.isEmpty()) {
            throw new IllegalArgumentException("Poll not found");
        }
        
        Poll poll = pollOpt.get();
        
        // Check if user is the creator
        if (!poll.getCreator().getId().equals(user.getId())) {
            throw new IllegalStateException("Only poll creator can delete the poll");
        }
        
        pollRepository.delete(poll);
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
     * Convert Poll to PollResponse
     */
    private PollResponse convertToResponse(Poll poll) {
        PollResponse response = new PollResponse();
        response.setId(poll.getId());
        response.setQuestion(poll.getQuestion());
        response.setOptions(poll.getOptions());
        response.setCreatorId(poll.getCreator().getId());
        response.setCreatorName(poll.getCreator().getFirstName() + " " + poll.getCreator().getLastName());
        response.setCreatorAvatar(poll.getCreator().getProfilePicture());
        response.setTravelPlanId(poll.getTravelPlan().getId());
        response.setIsActive(poll.getIsActive());
        response.setVoteCounts(poll.getVoteCounts());
        response.setCreatedAt(poll.getCreatedAt());
        response.setUpdatedAt(poll.getUpdatedAt());
        
        return response;
    }
} 