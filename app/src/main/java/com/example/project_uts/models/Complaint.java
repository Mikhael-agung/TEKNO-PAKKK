package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

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

    // Field dari relasi user
    @SerializedName("user_name")
    private String user_name;

    @SerializedName("user_email")
    private String user_email;

    @SerializedName("user_phone")
    private String user_phone;

    // Field dari relasi teknisi
    @SerializedName("teknisi_name")
    private String teknisi_name;

    @SerializedName("teknisi_phone")
    private String teknisi_phone;

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

    public String getUpdated_at() { return updated_at; }
    public void setUpdated_at(String updated_at) { this.updated_at = updated_at; }

    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }

    public String getUser_email() { return user_email; }
    public void setUser_email(String user_email) { this.user_email = user_email; }

    public String getUser_phone() { return user_phone; }
    public void setUser_phone(String user_phone) { this.user_phone = user_phone; }

    public String getTeknisi_name() { return teknisi_name; }
    public void setTeknisi_name(String teknisi_name) { this.teknisi_name = teknisi_name; }

    public String getTeknisi_phone() { return teknisi_phone; }
    public void setTeknisi_phone(String teknisi_phone) { this.teknisi_phone = teknisi_phone; }

    // Helper methods untuk UI
    public String getNama_pelapor() {
        return user_name != null ? user_name : "Pelanggan";
    }

    public String getTeknisi_nama() {
        return teknisi_name != null ? teknisi_name :
                (teknisi_id != null ? "Teknisi #" + teknisi_id : "Belum ditugaskan");
    }

    public String getTanggal_update() {
        return formatDate(updated_at);
    }

    public String getCatatan() {
        return resolution_notes;
    }

    public String getFullAlamat() {
        StringBuilder sb = new StringBuilder();
        if (alamat != null) {
            sb.append(alamat);
        }
        if (kecamatan != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(kecamatan);
        }
        if (kota != null) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(kota);
        }
        return sb.length() > 0 ? sb.toString() : "Alamat tidak tersedia";
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

    public String getAlamatLengkap() {
        StringBuilder sb = new StringBuilder();
        if (alamat != null) {
            sb.append(alamat);
        }
        if (kecamatan != null) {
            sb.append("\nKec. ").append(kecamatan);
        }
        if (kota != null) {
            sb.append("\n").append(kota);
        }
        if (telepon_alamat != null && !telepon_alamat.isEmpty()) {
            sb.append("\n📱 ").append(telepon_alamat);
        }
        if (catatan_alamat != null && !catatan_alamat.isEmpty()) {
            sb.append("\n📝 ").append(catatan_alamat);
        }
        return sb.toString();
    }

    public String getFormattedTimelineDate() {
        if (tanggal == null) return "";
        try {
            // Format: "2024-12-10T10:30:00.000Z" → "25/12/2024 12:54"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(tanggal.split("\\.")[0]);
            return outputFormat.format(date);
        } catch (Exception e) {
            return getTanggal();
        }
    }

    public String getFormattedUpdatedDate() {
        if (updated_at == null || updated_at.isEmpty()) return getFormattedTimelineDate();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(updated_at.split("\\.")[0]);
            return outputFormat.format(date);
        } catch (Exception e) {
            return updated_at;
        }
    }

    private String formatDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("id", "ID"));
            Date date = inputFormat.parse(dateString.split("\\.")[0]);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateString;
        }
    }
}