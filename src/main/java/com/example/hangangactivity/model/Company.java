package com.example.hangangactivity.model;

import java.time.LocalDateTime;

public class Company {

    private Long id;
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private String ceoContact;
    private String officeContact;
    private String officeAddress;
    private Boolean verified;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getCeoName() {
        return ceoName;
    }

    public void setCeoName(String ceoName) {
        this.ceoName = ceoName;
    }

    public String getCeoContact() {
        return ceoContact;
    }

    public void setCeoContact(String ceoContact) {
        this.ceoContact = ceoContact;
    }

    public String getOfficeContact() {
        return officeContact;
    }

    public void setOfficeContact(String officeContact) {
        this.officeContact = officeContact;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}