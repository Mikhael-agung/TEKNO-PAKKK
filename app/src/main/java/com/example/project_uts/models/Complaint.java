package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;

public class Complaint {
    @SerializedName("id")
    private String id;

    @SerializedName("judul")
    private String judul;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("created_at") // BE masih pakai created_at
    private String created_at;

    @SerializedName("status")
    private String status;

    @SerializedName("deskripsi")
    private String deskripsi;

    @SerializedName("user_id")
    private String user_id;

    @SerializedName("teknisi_id")
    private String teknisi_id;

    @SerializedName("resolution_notes")
    private String resolution_notes;

    @SerializedName("updated_at")
    private String updated_at;

    public Complaint() {}

    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    // Helper method untuk FE (getTanggal)
    public String getTanggal() {
        // Format dari "2024-12-10T10:30:00.000Z" ke "10 Dec 2024"
        if (created_at == null) return "";
        try {
            String datePart = created_at.split("T")[0];
            // Format: YYYY-MM-DD to DD-MM-YYYY
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return created_at;
        }
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getTeknisi_id() { return teknisi_id; }
    public void setTeknisi_id(String teknisi_id) { this.teknisi_id = teknisi_id; }

    public String getResolution_notes() { return resolution_notes; }
    public void setResolution_notes(String resolution_notes) {
        this.resolution_notes = resolution_notes;
    }

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}