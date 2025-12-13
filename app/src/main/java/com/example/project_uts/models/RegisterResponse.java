package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;

public class RegisterResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private RegisterData data;

    public static class RegisterData {
        @SerializedName("token")
        private String token;

        @SerializedName("user")
        private User user;

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RegisterData getData() { return data; }
    public void setData(RegisterData data) { this.data = data; }
}