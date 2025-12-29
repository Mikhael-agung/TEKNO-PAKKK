package com.example.project_uts.Teknisi.Model;

public class Komplain {
    private String id;
    private String title;
    private String description;
    private String status;
    private String createdAt;
    private String userId;

    // TAMBAH INI untuk dapat nama customer
    private User user;

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    // User object
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Inner class User
    public static class User {
        private String id;
        private String full_name;
        private String email;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}