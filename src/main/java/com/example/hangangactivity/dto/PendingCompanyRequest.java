package com.example.hangangactivity.dto;

import java.time.LocalDateTime;

public class PendingCompanyRequest {

    private Long userId;
    private String username;
    private Long companyId;
    private String companyName;
    private String businessNumber;
    private String ceoName;
    private String ceoContact;
    private String officeContact;
    private String officeAddress;
    private Boolean companyVerified;
    private String membershipStatus;
    private LocalDateTime companyCreatedAt;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public Boolean getCompanyVerified() {
        return companyVerified;
    }

    public void setCompanyVerified(Boolean companyVerified) {
        this.companyVerified = companyVerified;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public LocalDateTime getCompanyCreatedAt() {
        return companyCreatedAt;
    }

    public void setCompanyCreatedAt(LocalDateTime companyCreatedAt) {
        this.companyCreatedAt = companyCreatedAt;
    }
}