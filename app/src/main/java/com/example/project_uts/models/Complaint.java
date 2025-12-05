package com.example.project_uts.models;

public class Complaint {
    private String id;
    private String judul;
    private String kategori;
    private String tanggal;
    private String status;
    private String deskripsi;

    public Complaint() {
        // Default constructor required for Firebase
    }

    // Constructor dengan ID
    public Complaint(String id, String judul, String kategori, String tanggal, String status, String deskripsi) {
        this.id = id;
        this.judul = judul;
        this.kategori = kategori;
        this.tanggal = tanggal;
        this.status = status;
        this.deskripsi = deskripsi;
    }

    // Constructor tanpa ID (backward compatibility)
    public Complaint(String judul, String kategori, String tanggal, String status) {
        this.judul = judul;
        this.kategori = kategori;
        this.tanggal = tanggal;
        this.status = status;
        this.deskripsi = "Deskripsi tidak tersedia";
    }

    // Getter and Setter methods
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getTanggal() { return tanggal; }
    public void setTanggal(String tanggal) { this.tanggal = tanggal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeskripsi() { return deskripsi; }

    public void SetDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}