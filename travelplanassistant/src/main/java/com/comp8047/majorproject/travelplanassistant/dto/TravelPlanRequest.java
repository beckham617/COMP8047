package com.comp8047.majorproject.travelplanassistant.dto;

import com.comp8047.majorproject.travelplanassistant.entity.TravelPlan;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public class TravelPlanRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    @NotNull(message = "Plan type is required")
    private TravelPlan.PlanType planType;

    @NotNull(message = "Category is required")
    private TravelPlan.Category category;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String originCountry;
    private String originCity;
    private String destinationCountry;
    private String destinationCity;
    private TravelPlan.TransportType transportation;
    private TravelPlan.AccommodationType accommodation;

    @NotNull(message = "Maximum members is required")
    @Min(value = 1, message = "Minimum 1 member")
    @Max(value = 20, message = "Maximum 20 members")
    private Integer maxMembers;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must be less than 2000 characters")
    private String description;

    private List<String> images;
    private TravelPlan.GenderPreference gender;
    private Integer ageMin;
    private Integer ageMax;
    private String language;

    private TravelPlan.Status status;

    public TravelPlanRequest() {}

    public TravelPlanRequest(String title, TravelPlan.PlanType planType, TravelPlan.Category category, LocalDate startDate, LocalDate endDate, String originCountry, String originCity, String destinationCountry, String destinationCity, TravelPlan.TransportType transportation, TravelPlan.AccommodationType accommodation, Integer maxMembers, String description, List<String> images, TravelPlan.GenderPreference gender, Integer ageMin, Integer ageMax, String language, TravelPlan.Status status) {
        this.title = title;
        this.planType = planType;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.originCountry = originCountry;
        this.originCity = originCity;
        this.destinationCountry = destinationCountry;
        this.destinationCity = destinationCity;
        this.transportation = transportation;
        this.accommodation = accommodation;
        this.maxMembers = maxMembers;
        this.description = description;
        this.images = images;
        this.gender = gender;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.language = language;
        this.status = status;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TravelPlan.PlanType getPlanType() { return planType; }
    public void setPlanType(TravelPlan.PlanType planType) { this.planType = planType; }

    public TravelPlan.Category getCategory() { return category; }
    public void setCategory(TravelPlan.Category category) { this.category = category; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getOriginCity() { return originCity; }
    public void setOriginCity(String originCity) { this.originCity = originCity; }

    public String getDestinationCountry() { return destinationCountry; }
    public void setDestinationCountry(String destinationCountry) { this.destinationCountry = destinationCountry; }

    public String getDestinationCity() { return destinationCity; }
    public void setDestinationCity(String destinationCity) { this.destinationCity = destinationCity; }

    public TravelPlan.TransportType getTransportation() { return transportation; }
    public void setTransportation(TravelPlan.TransportType transportation) { this.transportation = transportation; }

    public TravelPlan.AccommodationType getAccommodation() { return accommodation; }
    public void setAccommodation(TravelPlan.AccommodationType accommodation) { this.accommodation = accommodation; }

    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }

    public TravelPlan.GenderPreference getGender() { return gender; }
    public void setGender(TravelPlan.GenderPreference gender) { this.gender = gender; }

    public Integer getAgeMin() { return ageMin; }
    public void setAgeMin(Integer ageMin) { this.ageMin = ageMin; }

    public Integer getAgeMax() { return ageMax; }
    public void setAgeMax(Integer ageMax) { this.ageMax = ageMax; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public TravelPlan.Status getStatus() { return status; }
    public void setStatus(TravelPlan.Status status) { this.status = status; }
} 