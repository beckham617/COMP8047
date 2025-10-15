package com.comp8047.majorproject.travelplanassistant.service;

import com.comp8047.majorproject.travelplanassistant.dto.RegisterRequest;
import com.comp8047.majorproject.travelplanassistant.dto.UserRequest;
import com.comp8047.majorproject.travelplanassistant.entity.User;
import com.comp8047.majorproject.travelplanassistant.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService
 * Tests user registration, authentication, profile management, and user retrieval functionality
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setGender(User.Gender.MALE);
        testUser.setBirthYear(1990);
        testUser.setBirthMonth(5);
        testUser.setPhoneNumber("1234567890");
        testUser.setLanguage("English");
        testUser.setCountry("USA");
        testUser.setCity("New York");
        testUser.setBio("Test bio");
        testUser.setProfilePicture("profile.jpg");

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setGender(User.Gender.MALE);
        registerRequest.setBirthYear(1990);
        registerRequest.setBirthMonth(5);
        registerRequest.setPhoneNumber("1234567890");
        registerRequest.setLanguage("English");
        registerRequest.setCountry("USA");
        registerRequest.setCity("New York");
        registerRequest.setBio("Test bio");
        registerRequest.setProfilePicture("profile.jpg");

        userRequest = new UserRequest();
        userRequest.setFirstName("Jane");
        userRequest.setLastName("Smith");
        userRequest.setGender(User.Gender.FEMALE);
        userRequest.setBirthYear(1995);
        userRequest.setBirthMonth(8);
        userRequest.setPhoneNumber("9876543210");
        userRequest.setLanguage("Spanish");
        userRequest.setCountry("Spain");
        userRequest.setCity("Madrid");
        userRequest.setBio("Updated bio");
        userRequest.setProfilePicture("new-profile.jpg");
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.registerUser(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals("encodedPassword", result.getPassword());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(registerRequest);
        });

        assertEquals("User with email test@example.com already exists", exception.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
    }

    @Test
    void testGetUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

        // When
        User result = userService.getUserByEmail("test@example.com");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.getUserByEmail("nonexistent@example.com");
        });

        assertEquals("User not found with email: nonexistent@example.com", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    void testGetAllUsers_Success() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        assertEquals(user2.getEmail(), result.get(1).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void testUpdateUserProfile_Success() {
        // Given
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setGender(User.Gender.FEMALE);
        updatedUser.setBirthYear(1995);
        updatedUser.setBirthMonth(8);
        updatedUser.setPhoneNumber("9876543210");
        updatedUser.setLanguage("Spanish");
        updatedUser.setCountry("Spain");
        updatedUser.setCity("Madrid");
        updatedUser.setBio("Updated bio");
        updatedUser.setProfilePicture("new-profile.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // When
        User result = userService.updateUserProfile(1L, userRequest);

        // Then
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals(User.Gender.FEMALE, result.getGender());
        assertEquals(1995, result.getBirthYear());
        assertEquals(8, result.getBirthMonth());
        assertEquals("9876543210", result.getPhoneNumber());
        assertEquals("Spanish", result.getLanguage());
        assertEquals("Spain", result.getCountry());
        assertEquals("Madrid", result.getCity());
        assertEquals("Updated bio", result.getBio());
        assertEquals("new-profile.jpg", result.getProfilePicture());

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testUpdateUserProfile_UserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUserProfile(999L, userRequest);
        });

        assertEquals("User not found with id: 999", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateLastLogin_Success() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateLastLogin(testUser);

        // Then
        verify(userRepository).save(testUser);
        // Last login timestamp is set internally by the service
    }
}