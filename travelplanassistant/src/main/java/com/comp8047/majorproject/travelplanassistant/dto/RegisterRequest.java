package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.User;

public class RegisterRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private User.Gender gender;
    private Integer age;
    private String phoneNumber;
    private String mainLanguage;
    private String additionalLanguages;
    private String city;
    private String country;
    private String bio;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public User.Gender getGender() {
        return gender;
    }
    public void setGender(User.Gender gender) {
        this.gender = gender;
    }
    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getMainLanguage() {
        return mainLanguage;
    }
    public void setMainLanguage(String mainLanguage) {
        this.mainLanguage = mainLanguage;
    }
    public String getAdditionalLanguages() {
        return additionalLanguages;
    }
    public void setAdditionalLanguages(String additionalLanguages) {
        this.additionalLanguages = additionalLanguages;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getBio() {
        return bio;
    }
    public void setBio(String bio) {
        this.bio = bio;
    }
} 