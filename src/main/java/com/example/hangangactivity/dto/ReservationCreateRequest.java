package com.example.hangangactivity.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReservationCreateRequest {

    @NotNull
    private Long activityId;

    @NotBlank
    private String touristName;

    @NotBlank
    private String passportNumber;

    @NotBlank
    private String nationality;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthdate;

    @Email
    @NotBlank
    private String email;

    @Min(1)
    private Integer peopleCount;

    private String specialRequest;

    private String gender;

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public String getTouristName() { return touristName; }
    public void setTouristName(String touristName) { this.touristName = touristName; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public LocalDate getBirthdate() { return birthdate; }
    public void setBirthdate(LocalDate birthdate) { this.birthdate = birthdate; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getPeopleCount() { return peopleCount; }
    public void setPeopleCount(Integer peopleCount) { this.peopleCount = peopleCount; }

    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
