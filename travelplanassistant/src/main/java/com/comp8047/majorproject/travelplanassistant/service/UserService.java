package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import com.comp8047.majorproject.travelplanassistant.dto.RegisterRequest;

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
        user.setAge(request.getAge());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setMainLanguage(request.getMainLanguage());
        user.setAdditionalLanguages(request.getAdditionalLanguages());
        user.setCity(request.getCity());
        user.setCountry(request.getCountry());
        user.setBio(request.getBio());

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

    public User updateUserProfile(Long userId, User updatedUser) {
        User existingUser = getUserById(userId);
        
        // Update fields that can be modified
        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setMainLanguage(updatedUser.getMainLanguage());
        existingUser.setAdditionalLanguages(updatedUser.getAdditionalLanguages());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setCountry(updatedUser.getCountry());
        existingUser.setBio(updatedUser.getBio());
        existingUser.setProfilePicture(updatedUser.getProfilePicture());

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
        return userRepository.findUsersForTravelPlanFilter(gender, minAge, maxAge, language);
    }

    public List<User> findUsersByCity(String city) {
        return userRepository.findByCity(city);
    }

    public List<User> findUsersByCountry(String country) {
        return userRepository.findByCountry(country);
    }

    public List<User> findUsersByLanguage(String language) {
        return userRepository.findByMainLanguageOrAdditionalLanguagesContaining(language);
    }

    public List<User> findUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userRepository.findByAgeRange(minAge, maxAge);
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