package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.UserPlanStatus;

public class MemberResponseDTO {
    private Long userId;
    private String fullName;
    private String profilePicture;
    private UserPlanStatus.Status userPlanStatus;

    public MemberResponseDTO() {}

    public MemberResponseDTO(Long userId, String fullName, String profilePicture, UserPlanStatus.Status userPlanStatus) {
        this.userId = userId;
        this.fullName = fullName;
        this.profilePicture = profilePicture;
        this.userPlanStatus = userPlanStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public UserPlanStatus.Status getUserPlanStatus() {
        return userPlanStatus;
    }

    public void setUserPlanStatus(UserPlanStatus.Status userPlanStatus) {
        this.userPlanStatus = userPlanStatus;
    }
}


