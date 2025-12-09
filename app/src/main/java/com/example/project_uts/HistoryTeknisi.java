package com.example.project_uts;

public class HistoryTeknisi {
    private String status;
    private String reason;
    private String time;

    public HistoryTeknisi(String status, String reason, String time) {
        this.status = status;
        this.reason = reason;
        this.time = time;
    }

    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public String getTime() { return time; }
}
