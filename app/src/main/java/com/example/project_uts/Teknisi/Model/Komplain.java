package com.example.project_uts.Teknisi.Model;

import com.google.gson.annotations.SerializedName;

public class Komplain {

    @SerializedName("id")
    private String id;

    @SerializedName("judul")
    private String judul;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("status")
    private String status;

    @SerializedName("tanggal")
    private String tanggal;

    @SerializedName("kota")
    private String kota;

    @SerializedName("alamat")
    private String alamat;

    // kalau backend nanti kirim object user
    @SerializedName("user")
    private User user;

    @SerializedName("deskripsi")
    private String deskripsi;

    // Getter
    public String getId() { return id; }
    public String getJudul() { return judul; }
    public String getKategori() { return kategori; }
    public String getStatus() { return status; }
    public String getTanggal() { return tanggal; }
    public String getKota() { return kota; }
    public String getAlamat() { return alamat; }
    public User getUser() { return user; }
    public String getDeskripsi() { return deskripsi; }
}
