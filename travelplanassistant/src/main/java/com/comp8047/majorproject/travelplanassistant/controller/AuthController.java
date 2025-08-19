package com.comp8047.majorproject.travelplanassistant.controller;

import com.comp8047.majorproject.travelplanassistant.dto.AuthRequest;
import com.comp8047.majorproject.travelplanassistant.dto.RegisterRequest;
import com.comp8047.majorproject.travelplanassistant.dto.UserResponse;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.security.JwtTokenUtil;
import com.comp8047.majorproject.travelplanassistant.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtTokenUtil jwtTokenUtil, 
                         UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(request);
        String token = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new UserResponse(user, token));
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@Valid @RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        System.out.println("user:" + user.getProfilePicture());
        String token = jwtTokenUtil.generateToken(user);
        // Update last login time
        userService.updateLastLogin(user);
        return ResponseEntity.ok(new UserResponse(user, token));
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
} 