package com.example.project_uts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiskusiTeknisi {
    private String id;
    private String komplainId;
    private String judulKomplain;
    private String pelapor;
    private String deskripsiKomplain;
    private String teknisiPeminta;
    private String noTelpTeknisi; // TAMBAH INI
    private String waktu;
    private String status;

    public DiskusiTeknisi() {}

    public DiskusiTeknisi(String komplainId, String judulKomplain, String pelapor,
                          String deskripsiKomplain, String teknisiPeminta, String noTelpTeknisi) {
        this.komplainId = komplainId;
        this.judulKomplain = judulKomplain;
        this.pelapor = pelapor;
        this.deskripsiKomplain = deskripsiKomplain;
        this.teknisiPeminta = teknisiPeminta;
        this.noTelpTeknisi = noTelpTeknisi; // SIMPAN NO TELEPON
        this.waktu = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        this.status = "pending";
    }

    // GETTER AND SETTER - PERBAIKI NAMA METHOD
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getKomplainId() { return komplainId; }
    public void setKomplainId(String komplainId) { this.komplainId = komplainId; }

    public String getJudulKomplain() { return judulKomplain; }
    public void setJudulKomplain(String judulKomplain) { this.judulKomplain = judulKomplain; }

    public String getPelapor() { return pelapor; }
    public void setPelapor(String pelapor) { this.pelapor = pelapor; }

    public String getDeskripsiKomplain() { return deskripsiKomplain; }
    public void setDeskripsiKomplain(String deskripsiKomplain) { this.deskripsiKomplain = deskripsiKomplain; }

    public String getTeknisiPeminta() { return teknisiPeminta; }
    public void setTeknisiPeminta(String teknisiPeminta) { this.teknisiPeminta = teknisiPeminta; }

    // INI YANG PERLU DIPERBAIKI - PASTIKAN SAMA:
    public String getNoTelpTeknisi() { return noTelpTeknisi; } // getNoTelpTeknisi() BUKAN getNoTelpTeknisi()
    public void setNoTelpTeknisi(String noTelpTeknisi) { this.noTelpTeknisi = noTelpTeknisi; } // setNoTelpTeknisi() BUKAN setNoTelpTeknisi()

    public String getWaktu() { return waktu; }
    public void setWaktu(String waktu) { this.waktu = waktu; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}