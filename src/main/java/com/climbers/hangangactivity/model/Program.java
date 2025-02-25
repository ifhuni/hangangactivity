package com.climbers.hangangactivity.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Program {
    private int id;
    private int companyId;
    private String title;
    private String description;
    private BigDecimal price;
    private int capacity;
    private Timestamp startTime;
    private Timestamp endTime;
    private Timestamp createdAt;

    // 기본 생성자
    public Program() {
    }

    // 모든 필드를 포함한 생성자
    public Program(int id, int companyId, String title, String description, BigDecimal price, int capacity, Timestamp startTime, Timestamp endTime, Timestamp createdAt) {
        this.id = id;
        this.companyId = companyId;
        this.title = title;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = createdAt;
    }

    // Getter & Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
