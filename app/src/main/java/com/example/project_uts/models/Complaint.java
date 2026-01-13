package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Complaint {
    @SerializedName("id")
    private String id;

    @SerializedName("judul")
    private String judul;

    @SerializedName("kategori")
    private String kategori;

    @SerializedName("tanggal")
    private String tanggal;

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

    @SerializedName("alamat")
    private String alamat;

    @SerializedName("kota")
    private String kota;

    @SerializedName("kecamatan")
    private String kecamatan;

    @SerializedName("telepon_alamat")
    private String telepon_alamat;

    @SerializedName("catatan_alamat")
    private String catatan_alamat;

    public Complaint() {}

    // Getter dan Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJudul() { return judul; }
    public void setJudul(String judul) { this.judul = judul; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getCreated_at() { return tanggal; }
    public void setCreated_at(String created_at) { this.tanggal = created_at; }

    public String getTanggal() {
        if (tanggal == null) return "";
        try {
            String datePart = tanggal.split("T")[0];
            // Format: YYYY-MM-DD to DD-MM-YYYY
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return tanggal;
        }
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getAlamat() { return alamat; }
    public void setAlamat(String alamat) { this.alamat = alamat; }

    public String getKota() { return kota; }
    public void setKota(String kota) { this.kota = kota; }

    public String getKecamatan() { return kecamatan; }
    public void setKecamatan(String kecamatan) { this.kecamatan = kecamatan; }

    public String getTelepon_alamat() { return telepon_alamat; }
    public void setTelepon_alamat(String telepon_alamat) { this.telepon_alamat = telepon_alamat; }

    public String getCatatan_alamat() { return catatan_alamat; }
    public void setCatatan_alamat(String catatan_alamat) { this.catatan_alamat = catatan_alamat; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getTeknisi_id() { return teknisi_id; }
    public void setTeknisi_id(String teknisi_id) { this.teknisi_id = teknisi_id; }

    public String getResolution_notes() { return resolution_notes; }
    public void setResolution_notes(String resolution_notes) {
        this.resolution_notes = resolution_notes;
    }

    public String getShortDate() {
        if (tanggal == null) return "";
        try {
            // Format: "2024-12-10T10:30:00.000Z" → "15 Des"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", new Locale("id", "ID"));

            Date date = inputFormat.parse(tanggal.split("\\.")[0]);
            return outputFormat.format(date);
        } catch (Exception e) {
            // Fallback ke format pendek
            try {
                String[] parts = getTanggal().split("/");
                if (parts.length >= 2) {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};
                    if (month >= 1 && month <= 12) {
                        return day + " " + monthNames[month-1];
                    }
                }
            } catch (Exception ex) {
                // Ignore
            }
            return getTanggal();
        }
    }

    // Method untuk dapatkan judul yang lebih presentable
    public String getPresentableTitle() {
        if (judul != null && !judul.trim().isEmpty()) {
            String cleanTitle = judul.trim();
            // Jika judul terlalu pendek atau tidak jelas
            if (cleanTitle.length() < 4 ||
                    cleanTitle.equalsIgnoreCase("null") ||
                    cleanTitle.matches(".*[0-9]{5,}.*")) { // jika berisi banyak angka
                // Gunakan kategori sebagai fallback
                if (kategori != null && !kategori.trim().isEmpty()) {
                    return "Keluhan " + kategori;
                }
                return "Keluhan Layanan";
            }
            return cleanTitle;
        }
        // Fallback ke kategori
        if (kategori != null && !kategori.trim().isEmpty()) {
            return "Keluhan " + kategori;
        }
        return "Keluhan Layanan";
    }

    // Method untuk deskripsi yang lebih baik
    public String getCleanDescription() {
        if (deskripsi != null && !deskripsi.trim().isEmpty()) {
            String cleanDesc = deskripsi.trim();
            // Hilangkan "null" string
            if (cleanDesc.equalsIgnoreCase("null")) {
                return "";
            }
            return cleanDesc;
        }
        return "";
    }

    public String getFormattedDate() {
        if (tanggal == null) return "";
        try {
            // Format: "2024-12-10T10:30:00.000Z" → "15 Des 2024"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));

            Date date = inputFormat.parse(tanggal.split("\\.")[0]);
            return outputFormat.format(date);
        } catch (Exception e) {
            return getTanggal(); // fallback ke format lama
        }
    }
    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}