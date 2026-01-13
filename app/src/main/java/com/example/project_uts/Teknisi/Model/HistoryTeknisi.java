package com.example.project_uts.Teknisi.Model;

import com.google.gson.annotations.SerializedName;

public class HistoryTeknisi {
    private int id;

    @SerializedName("complaint_id")
    private String complaintId;

    @SerializedName("teknisi_id")
    private String teknisiId;

    private String status;

    @SerializedName("alasan")
    private String reason;

    @SerializedName("created_at")
    private String createdAt;

    private User teknisi; // nested object

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getComplaintId() { return complaintId; }
    public void setComplaintId(String complaintId) { this.complaintId = complaintId; }

    public String getTeknisiId() { return teknisiId; }
    public void setTeknisiId(String teknisiId) { this.teknisiId = teknisiId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public User getTeknisi() { return teknisi; }
    public void setTeknisi(User teknisi) { this.teknisi = teknisi; }
}
