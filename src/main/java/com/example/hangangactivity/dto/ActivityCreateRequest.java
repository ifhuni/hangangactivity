package com.example.hangangactivity.dto;

import java.time.LocalDateTime;

public class ActivityCreateRequest {

    private final Long companyId;
    private final String title;
    private final String category;
    private final String description;
    private final String location;
    private final Integer capacity;
    private final LocalDateTime startAt;
    private final LocalDateTime endAt;
    private final Integer price;
    private final String status;

    public ActivityCreateRequest(Long companyId,
                                 String title,
                                 String category,
                                 String description,
                                 String location,
                                 Integer capacity,
                                 LocalDateTime startAt,
                                 LocalDateTime endAt,
                                 Integer price,
                                 String status) {
        this.companyId = companyId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.location = location;
        this.capacity = capacity;
        this.startAt = startAt;
        this.endAt = endAt;
        this.price = price;
        this.status = status;
    }

    public Long getCompanyId() { return companyId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public Integer getCapacity() { return capacity; }
    public LocalDateTime getStartAt() { return startAt; }
    public LocalDateTime getEndAt() { return endAt; }
    public Integer getPrice() { return price; }
    public String getStatus() { return status; }
}
