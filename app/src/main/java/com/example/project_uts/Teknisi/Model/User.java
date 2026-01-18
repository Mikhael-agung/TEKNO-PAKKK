package com.example.project_uts.Teknisi.Model;


 public class User {
        private String id;
        private String full_name;
        private String phone;

        // Getter
        public String getId() { return id; }
        public String getFull_name() { return full_name; }
        public String getPhone() { return phone; }

        // Setter (opsional)
        public void setId(String id) { this.id = id; }
        public void setFull_name(String full_name) { this.full_name = full_name; }
        public void setPhone(String phone) { this.phone = phone; }
    }
