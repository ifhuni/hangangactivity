package com.example.hangangactivity.dto;

public class AuthResponse {

    private boolean success;
    private String message;
    private String username;
    private String name;
    private Long companyId;
    private String companyName;
    private String membershipStatus;
    private boolean requiresCompanySetup;
    private String role;
    private String nextAction;

    public AuthResponse() {
    }

    public AuthResponse(boolean success, String message) {
        this(success, message, null);
    }

    public AuthResponse(boolean success, String message, String username) {
        this.success = success;
        this.message = message;
        this.username = username;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getMembershipStatus() {
        return membershipStatus;
    }

    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    public boolean isRequiresCompanySetup() {
        return requiresCompanySetup;
    }

    public void setRequiresCompanySetup(boolean requiresCompanySetup) {
        this.requiresCompanySetup = requiresCompanySetup;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNextAction() {
        return nextAction;
    }

    public void setNextAction(String nextAction) {
        this.nextAction = nextAction;
    }
}
