package com.example.project_uts.Teknisi.Model;
 // sesuaikan dengan paket kamu

public class ComplaintStatusRequest {
    private String status;
    private String technician_id;
    private String alasan;

    public ComplaintStatusRequest(String status, String technician_id, String alasan) {
        this.status = status;
        this.technician_id = technician_id;
        this.alasan = alasan;
    }

    // Getter kalau dibutuhkan oleh serializer
    public String getStatus() { return status; }
    public String getTechnician_id() { return technician_id; }
    public String getAlasan() { return alasan; }
}
