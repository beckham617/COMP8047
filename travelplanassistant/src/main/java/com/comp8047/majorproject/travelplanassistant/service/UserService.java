package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import com.comp8047.majorproject.travelplanassistant.dto.RegisterRequest;
import com.comp8047.majorproject.travelplanassistant.dto.UserRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setGender(request.getGender());
        user.setBirthYear(request.getBirthYear());
        user.setBirthMonth(request.getBirthMonth());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setLanguage(request.getLanguage());
        user.setCountry(request.getCountry());
        user.setCity(request.getCity());
        user.setBio(request.getBio());
        user.setProfilePicture(request.getProfilePicture());

        return userRepository.save(user);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUserProfile(Long userId, UserRequest userRequest) {
        User existingUser = getUserById(userId);
        
        // Update fields that can be modified
        existingUser.setFirstName(userRequest.getFirstName());
        existingUser.setLastName(userRequest.getLastName());
        existingUser.setGender(userRequest.getGender());
        existingUser.setBirthYear(userRequest.getBirthYear());
        existingUser.setBirthMonth(userRequest.getBirthMonth());
        existingUser.setPhoneNumber(userRequest.getPhoneNumber());
        existingUser.setLanguage(userRequest.getLanguage());
        existingUser.setCountry(userRequest.getCountry());
        existingUser.setCity(userRequest.getCity());
        existingUser.setBio(userRequest.getBio());
        existingUser.setProfilePicture(userRequest.getProfilePicture());

        return userRepository.save(existingUser);
    }

    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        
        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void deactivateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public void activateUser(Long userId) {
        User user = getUserById(userId);
        user.setIsActive(true);
        userRepository.save(user);
    }

    public List<User> findUsersForTravelPlanFilter(User.Gender gender, Integer minAge, Integer maxAge, String language) {
        // Calculate birth year range based on age range
        int currentYear = java.time.LocalDate.now().getYear();
        Integer minBirthYear = maxAge != null ? currentYear - maxAge : null;
        Integer maxBirthYear = minAge != null ? currentYear - minAge : null;
        
        return userRepository.findUsersForTravelPlanFilter(gender, minAge, maxAge, language, minBirthYear, maxBirthYear);
    }

    public List<User> findUsersByCity(String city) {
        return userRepository.findByCity(city);
    }

    public List<User> findUsersByCountry(String country) {
        return userRepository.findByCountry(country);
    }

    public List<User> findUsersByLanguage(String language) {
        return userRepository.findByLanguage(language);
    }

    public List<User> findUsersByAgeRange(Integer minAge, Integer maxAge) {
        // Calculate birth year range based on age range
        int currentYear = java.time.LocalDate.now().getYear();
        Integer minBirthYear = maxAge != null ? currentYear - maxAge : null;
        Integer maxBirthYear = minAge != null ? currentYear - minAge : null;
        
        return userRepository.findByBirthYearRange(minBirthYear, maxBirthYear);
    }

    public List<User> findUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    public long countUsersByCity(String city) {
        return userRepository.countByCity(city);
    }

    public long countUsersByCountry(String country) {
        return userRepository.countByCountry(country);
    }
} 