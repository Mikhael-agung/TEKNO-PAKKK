package com.example.project_uts.Teknisi.Model;

import com.google.gson.annotations.SerializedName;

public class DiskusiTeknisi {
    private int id;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("technician")
    private Technician technician;

    @SerializedName("complaint")
    private Complaint complaint;

    // 👉 Tambahin field untuk request body
    @SerializedName("complaint_id")
    private String complaintId;

    // Getter & Setter
    public int getId() { return id; }
    public String getCreated_at() { return created_at; }
    public Technician getTechnician() { return technician; }
    public Complaint getComplaint() { return complaint; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    // Nested classes
    public static class Technician {
        private String id;
        private String phone;
        private String full_name;

        public String getId() { return id; }
        public String getPhone() { return phone; }
        public String getFull_name() { return full_name; }
    }

    public static class Complaint {
        private String id;
        private User user;
        private String judul;

        public String getId() { return id; }
        public User getUser() { return user; }
        public String getJudul() { return judul; }

        public static class User {
            private String id;
            private String full_name;

            public String getId() { return id; }
            public String getFull_name() { return full_name; }
        }
    }
}
