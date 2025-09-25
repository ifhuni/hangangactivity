package com.example.hangangactivity.model;

import java.time.LocalDateTime;

public class Reservation {

    private Long id;
    private Long activityId;
    private Long touristId;
    private String status;
    private String specialRequest;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }

    public Long getTouristId() { return touristId; }
    public void setTouristId(Long touristId) { this.touristId = touristId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSpecialRequest() { return specialRequest; }
    public void setSpecialRequest(String specialRequest) { this.specialRequest = specialRequest; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
