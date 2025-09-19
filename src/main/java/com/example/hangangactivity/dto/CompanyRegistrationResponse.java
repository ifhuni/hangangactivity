package com.example.hangangactivity.dto;

public class CompanyRegistrationResponse {

    private boolean success;
    private String message;
    private Long companyId;
    private String membershipStatus;

    public CompanyRegistrationResponse() {
    }

    public CompanyRegistrationResponse(boolean success, String message) {
        this(success, message, null, null);
    }

    public CompanyRegistrationResponse(boolean success, String message, Long companyId, String membershipStatus) {
        this.success = success;
        this.message = message;
        this.companyId = companyId;
        this.membershipStatus = membershipStatus;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }
}