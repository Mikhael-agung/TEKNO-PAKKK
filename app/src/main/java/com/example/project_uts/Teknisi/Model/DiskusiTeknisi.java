package com.example.project_uts.Teknisi.Model;

public class DiskusiTeknisi {
    private String id;
    private String komplainId;
    private String judulKomplain;
    private String pelapor;
    private String deskripsiKomplain;
    private String teknisiPeminta;
    private String noTelpTeknisi;
    private String waktu;
    private String status;

    // Getter & Setter
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

    public String getNoTelpTeknisi() { return noTelpTeknisi; }
    public void setNoTelpTeknisi(String noTelpTeknisi) { this.noTelpTeknisi = noTelpTeknisi; }

    public String getWaktu() { return waktu; }
    public void setWaktu(String waktu) { this.waktu = waktu; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
