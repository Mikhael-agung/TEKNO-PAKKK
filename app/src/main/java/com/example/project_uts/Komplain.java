package com.example.project_uts;

public class Komplain {
    private String id;
    private String judul;
    private String pelapor;
    private String status;
    private String waktu;
    private String fotoUrl;
    private String deskripsi;
    private String phoneNumber;
    private String teknisiNote;
    private String fotoPerbaikan;
    private String alasanPending;

    public Komplain(String id, String judul, String pelapor, String status, String waktu,
                    String fotoUrl, String deskripsi, String phoneNumber, String teknisiNote,
                    String fotoPerbaikan, String alasanPending) {
        this.id = id;
        this.judul = judul;
        this.pelapor = pelapor;
        this.status = status;
        this.waktu = waktu;
        this.fotoUrl = fotoUrl;
        this.deskripsi = deskripsi;
        this.phoneNumber = phoneNumber;
        this.teknisiNote = teknisiNote;
        this.fotoPerbaikan = fotoPerbaikan;
        this.alasanPending = alasanPending;
    }

    // Getter methods
    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getPelapor() { return pelapor; }
    public String getStatus() { return status; }
    public String getWaktu() { return waktu; }
    public String getFotoUrl() { return fotoUrl; }
    public String getDeskripsi() { return deskripsi; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getTeknisiNote() { return teknisiNote; }
    public String getFotoPerbaikan() { return fotoPerbaikan; }
    public String getAlasanPending() { return alasanPending; }
}