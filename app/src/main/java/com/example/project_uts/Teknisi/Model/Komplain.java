package com.example.project_uts.Teknisi.Model;

public class Komplain {
    private String id;
    private String judul;
    private String Deskripsi;
    private String status;
    private String createdAt;
    private String userId;


    private User user;

    // Getter & Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getDeskripsi() { return Deskripsi; }
    public void setDeskripsi(String deskripsi) { this.Deskripsi = deskripsi; }

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
        private String phone;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getFull_name() { return full_name; }
        public void setFull_name(String full_name) { this.full_name = full_name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() {return phone;}
        public void setPhone(String phone) {this.phone = phone; }
    }
}