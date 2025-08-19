package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.UserRequest;
import com.comp8047.majorproject.travelplanassistant.dto.UserResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getCurrentUserProfile(@AuthenticationPrincipal User user) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(new UserResponse(currentUser));
    }

    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody UserRequest userRequest) {
        User currentUser = userService.getCurrentUser();
        // Update the user profile
        User savedUser = userService.updateUserProfile(currentUser.getId(), userRequest);
        return ResponseEntity.ok(new UserResponse(savedUser));
    }

    /**
     * Get user profile by ID (for viewing other users)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserProfile(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return ResponseEntity.ok(new UserResponse(user));
    }

    /**
     * Change password
     */
    @PutMapping("/profile/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, String> passwordRequest) {
        
        String oldPassword = passwordRequest.get("oldPassword");
        String newPassword = passwordRequest.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Old password and new password are required"));
        }
        
        try {
            userService.changePassword(user.getId(), oldPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Deactivate user account
     */
    @PutMapping("/profile/deactivate")
    public ResponseEntity<Map<String, String>> deactivateAccount(@AuthenticationPrincipal User user) {
        userService.deactivateUser(user.getId());
        return ResponseEntity.ok(Map.of("message", "Account deactivated successfully"));
    }
} 