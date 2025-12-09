package com.example.project_uts;



public class ComplaintResponse {
    private Komplain complaint;
    private ComplaintStatus latest_status;

    public Komplain getComplaint() { return complaint; }
    public void setComplaint(Komplain complaint) { this.complaint = complaint; }

    public ComplaintStatus getLatest_status() { return latest_status; }
    public void setLatest_status(ComplaintStatus latest_status) { this.latest_status = latest_status; }
}