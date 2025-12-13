package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private LoginData data;

    public static class LoginData {
        @SerializedName("token")
        private String token;

        @SerializedName("user")
        private User user;

        // Getter dan Setter
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
    }

    // Getter dan Setter
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LoginData getData() { return data; }
    public void setData(LoginData data) { this.data = data; }
}