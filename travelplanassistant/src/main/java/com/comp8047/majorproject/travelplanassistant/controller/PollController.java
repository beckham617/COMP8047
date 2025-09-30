package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.PollRequest;
import com.comp8047.majorproject.travelplanassistant.dto.PollResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.PollService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polls")
@CrossOrigin(origins = "*")
public class PollController {

    @Autowired
    private PollService pollService;

    /**
     * Create a new poll for a travel plan
     */
    @PostMapping("/travel-plans/{planId}")
    public ResponseEntity<?> createPoll(@PathVariable Long planId,
                                       @Valid @RequestBody PollRequest request,
                                       @AuthenticationPrincipal User user) {
        try {
            PollResponse poll = pollService.createPoll(planId, request, user);
            return ResponseEntity.ok(poll);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Vote on a poll option
     */
    @PostMapping("/{pollId}/vote")
    public ResponseEntity<?> voteOnPoll(@PathVariable Long pollId,
                                       @RequestParam Long optionId,
                                       @AuthenticationPrincipal User user) {
        try {
            PollResponse poll = pollService.voteOnPoll(pollId, optionId, user);
            return ResponseEntity.ok(poll);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get all polls for a travel plan
     */
    @GetMapping("/travel-plans/{planId}")
    public ResponseEntity<?> getPollsForTravelPlan(@PathVariable Long planId,
                                                  @AuthenticationPrincipal User user) {
        try {
            List<PollResponse> polls = pollService.getPolls(planId, user);
            return ResponseEntity.ok(polls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get active polls for a travel plan
     */
    @GetMapping("/travel-plans/{planId}/active")
    public ResponseEntity<?> getActivePollsForTravelPlan(@PathVariable Long planId,
                                                        @AuthenticationPrincipal User user) {
        try {
            List<PollResponse> polls = pollService.getActivePolls(planId, user);
            return ResponseEntity.ok(polls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Close a poll (only creator can close)
     */
    @PutMapping("/{pollId}/close")
    public ResponseEntity<?> closePoll(@PathVariable Long pollId,
                                      @AuthenticationPrincipal User user) {
        try {
            PollResponse poll = pollService.deactivatePoll(pollId, user);
            return ResponseEntity.ok(poll);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Delete a poll (only creator can delete)
     */
    @DeleteMapping("/{pollId}")
    public ResponseEntity<?> deletePoll(@PathVariable Long pollId,
                                       @AuthenticationPrincipal User user) {
        try {
            pollService.deletePoll(pollId, user);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
