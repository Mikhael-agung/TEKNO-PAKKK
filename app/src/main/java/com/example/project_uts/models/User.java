package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private String id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("full_name")
    private String full_name;

    @SerializedName("role")
    private String role;

    @SerializedName("phone")
    private String phone;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;

    public User() {}

    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFull_name() { return full_name; }
    public void setFull_name(String full_name) { this.full_name = full_name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @SerializedName("telepon")
    private String telepon;

    @SerializedName("no_hp")
    private String no_hp;

    public String getTelepon() {
        // Prioritas: telepon > phone > no_hp
        if (telepon != null && !telepon.isEmpty()) {
            return telepon;
        } else if (phone != null && !phone.isEmpty()) {
            return phone;
        } else {
            return no_hp != null ? no_hp : "";
        }
    }

    public void setTelepon(String telepon) { this.telepon = telepon; }

    public String getNo_hp() {
        // Prioritas: no_hp > telepon > phone
        if (no_hp != null && !no_hp.isEmpty()) {
            return no_hp;
        } else if (telepon != null && !telepon.isEmpty()) {
            return telepon;
        } else {
            return phone != null ? phone : "";
        }
    }

    public void setNo_hp(String no_hp) { this.no_hp = no_hp; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

    // Helper methods (tidak perlu @SerializedName)
    public String getDisplayName() {
        return full_name != null ? full_name : username;
    }

    public boolean isCustomer() {
        return "customer".equals(role);
    }

    public boolean isTeknisi() {
        return "teknisi".equals(role);
    }

    public boolean isAdmin() {
        return "admin".equals(role);
    }
}