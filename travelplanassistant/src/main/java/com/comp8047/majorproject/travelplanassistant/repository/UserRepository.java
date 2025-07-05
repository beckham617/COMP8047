package com.comp8047.majorproject.travelplanassistant.repository;

import com.comp8047.majorproject.travelplanassistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find users by city
     */
    List<User> findByCity(String city);
    
    /**
     * Find users by country
     */
    List<User> findByCountry(String country);
    
    /**
     * Find users by main language
     */
    List<User> findByMainLanguage(String mainLanguage);
    
    /**
     * Find users by gender
     */
    List<User> findByGender(User.Gender gender);
    
    /**
     * Find users by age range
     */
    @Query("SELECT u FROM User u WHERE u.age BETWEEN :minAge AND :maxAge")
    List<User> findByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    
    /**
     * Find users by age greater than or equal to
     */
    List<User> findByAgeGreaterThanEqual(Integer age);
    
    /**
     * Find users by age less than or equal to
     */
    List<User> findByAgeLessThanEqual(Integer age);
    
    /**
     * Find active users
     */
    List<User> findByIsActiveTrue();
    
    /**
     * Find users by role
     */
    List<User> findByRole(User.Role role);
    
    /**
     * Find users by city and country
     */
    List<User> findByCityAndCountry(String city, String country);
    
    /**
     * Find users by main language and country
     */
    List<User> findByMainLanguageAndCountry(String mainLanguage, String country);
    
    /**
     * Find users by additional languages containing the specified language
     */
    @Query("SELECT u FROM User u WHERE u.additionalLanguages LIKE %:language%")
    List<User> findByAdditionalLanguagesContaining(@Param("language") String language);
    
    /**
     * Find users by main language or additional languages
     */
    @Query("SELECT u FROM User u WHERE u.mainLanguage = :language OR u.additionalLanguages LIKE %:language%")
    List<User> findByMainLanguageOrAdditionalLanguagesContaining(@Param("language") String language);
    
    /**
     * Find users by name (first name or last name containing the search term)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(%:name%) OR LOWER(u.lastName) LIKE LOWER(%:name%)")
    List<User> findByNameContainingIgnoreCase(@Param("name") String name);
    
    /**
     * Find users by email containing the search term
     */
    List<User> findByEmailContainingIgnoreCase(String email);
    
    /**
     * Find users created after a specific date
     */
    List<User> findByCreatedAtAfter(java.time.LocalDateTime date);
    
    /**
     * Find users who logged in after a specific date
     */
    List<User> findByLastLoginAfter(java.time.LocalDateTime date);
    
    /**
     * Count users by city
     */
    long countByCity(String city);
    
    /**
     * Count users by country
     */
    long countByCountry(String country);
    
    /**
     * Count active users
     */
    long countByIsActiveTrue();
    
    /**
     * Find users with no last login (new users)
     */
    List<User> findByLastLoginIsNull();
    
    /**
     * Find users by multiple criteria for travel plan filtering
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:gender IS NULL OR u.gender = :gender) AND " +
           "(:minAge IS NULL OR u.age >= :minAge) AND " +
           "(:maxAge IS NULL OR u.age <= :maxAge) AND " +
           "(:language IS NULL OR u.mainLanguage = :language OR u.additionalLanguages LIKE %:language%) AND " +
           "u.isActive = true")
    List<User> findUsersForTravelPlanFilter(
            @Param("gender") User.Gender gender,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            @Param("language") String language
    );
} 