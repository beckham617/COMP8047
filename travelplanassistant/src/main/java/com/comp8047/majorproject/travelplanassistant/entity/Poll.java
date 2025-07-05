package com.comp8047.majorproject.travelplanassistant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polls")
public class Poll {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Question is required")
    @Size(max = 500, message = "Question must be less than 500 characters")
    @Column(nullable = false, length = 500)
    private String question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plan_id", nullable = false)
    @NotNull(message = "Travel plan is required")
    private TravelPlan travelPlan;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    @NotNull(message = "Creator is required")
    private User createdBy;
    
    @Column(name = "is_multiple_choice")
    private Boolean isMultipleChoice = false;
    
    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;
    
    @NotNull(message = "Start time is required")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @NotNull(message = "End time is required")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PollStatus status = PollStatus.ACTIVE;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    // Relationships
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PollOption> options = new ArrayList<>();
    
    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PollVote> votes = new ArrayList<>();
    
    // Constructors
    public Poll() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Poll(String question, TravelPlan travelPlan, User createdBy, LocalDateTime startTime, LocalDateTime endTime) {
        this();
        this.question = question;
        this.travelPlan = travelPlan;
        this.createdBy = createdBy;
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public void setQuestion(String question) {
        this.question = question;
    }
    
    public TravelPlan getTravelPlan() {
        return travelPlan;
    }
    
    public void setTravelPlan(TravelPlan travelPlan) {
        this.travelPlan = travelPlan;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public Boolean getIsMultipleChoice() {
        return isMultipleChoice;
    }
    
    public void setIsMultipleChoice(Boolean isMultipleChoice) {
        this.isMultipleChoice = isMultipleChoice;
    }
    
    public Boolean getIsAnonymous() {
        return isAnonymous;
    }
    
    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public PollStatus getStatus() {
        return status;
    }
    
    public void setStatus(PollStatus status) {
        this.status = status;
        if (status == PollStatus.CLOSED) {
            this.closedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    
    public void setClosedAt(LocalDateTime closedAt) {
        this.closedAt = closedAt;
    }
    
    public List<PollOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<PollOption> options) {
        this.options = options;
    }
    
    public List<PollVote> getVotes() {
        return votes;
    }
    
    public void setVotes(List<PollVote> votes) {
        this.votes = votes;
    }
    
    // Helper methods
    public boolean isActive() {
        return status == PollStatus.ACTIVE;
    }
    
    public boolean isClosed() {
        return status == PollStatus.CLOSED;
    }
    
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(endTime);
    }
    
    public boolean isStarted() {
        return LocalDateTime.now().isAfter(startTime);
    }
    
    public boolean canVote() {
        return isActive() && isStarted() && !isExpired();
    }
    
    public int getTotalVotes() {
        return votes.size();
    }
    
    public boolean hasUserVoted(User user) {
        return votes.stream()
                .anyMatch(vote -> vote.getUser().getId().equals(user.getId()));
    }
    
    public PollOption getWinningOption() {
        return options.stream()
                .max((o1, o2) -> Integer.compare(o1.getVoteCount(), o2.getVoteCount()))
                .orElse(null);
    }
    
    public void addOption(PollOption option) {
        options.add(option);
        option.setPoll(this);
    }
    
    public void removeOption(PollOption option) {
        options.remove(option);
        option.setPoll(null);
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Poll status enum
    public enum PollStatus {
        ACTIVE, CLOSED
    }
    
    // Inner class for poll options
    @Entity
    @Table(name = "poll_options")
    public static class PollOption {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @NotBlank(message = "Option text is required")
        @Size(max = 200, message = "Option text must be less than 200 characters")
        @Column(nullable = false, length = 200)
        private String text;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "poll_id", nullable = false)
        private Poll poll;
        
        @Column(name = "created_at", nullable = false)
        private LocalDateTime createdAt;
        
        // Relationships
        @OneToMany(mappedBy = "pollOption", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        private List<PollVote> votes = new ArrayList<>();
        
        public PollOption() {
            this.createdAt = LocalDateTime.now();
        }
        
        public PollOption(String text) {
            this();
            this.text = text;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public Poll getPoll() {
            return poll;
        }
        
        public void setPoll(Poll poll) {
            this.poll = poll;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public List<PollVote> getVotes() {
            return votes;
        }
        
        public void setVotes(List<PollVote> votes) {
            this.votes = votes;
        }
        
        // Helper methods
        public int getVoteCount() {
            return votes.size();
        }
        
        public double getVotePercentage() {
            if (poll.getTotalVotes() == 0) {
                return 0.0;
            }
            return (double) getVoteCount() / poll.getTotalVotes() * 100;
        }
    }
    
    // Inner class for poll votes
    @Entity
    @Table(name = "poll_votes")
    public static class PollVote {
        
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "poll_id", nullable = false)
        private Poll poll;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "poll_option_id", nullable = false)
        private PollOption pollOption;
        
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;
        
        @Column(name = "voted_at", nullable = false)
        private LocalDateTime votedAt;
        
        public PollVote() {
            this.votedAt = LocalDateTime.now();
        }
        
        public PollVote(Poll poll, PollOption pollOption, User user) {
            this();
            this.poll = poll;
            this.pollOption = pollOption;
            this.user = user;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Poll getPoll() {
            return poll;
        }
        
        public void setPoll(Poll poll) {
            this.poll = poll;
        }
        
        public PollOption getPollOption() {
            return pollOption;
        }
        
        public void setPollOption(PollOption pollOption) {
            this.pollOption = pollOption;
        }
        
        public User getUser() {
            return user;
        }
        
        public void setUser(User user) {
            this.user = user;
        }
        
        public LocalDateTime getVotedAt() {
            return votedAt;
        }
        
        public void setVotedAt(LocalDateTime votedAt) {
            this.votedAt = votedAt;
        }
    }
} 