package com.climbers.hangangactivity.model;

public class UserResponse {
    private boolean success;
    private String message;

    public UserResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
