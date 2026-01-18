
public class ComplaintStatus {
    private String status;
    private String reason; // bisa alasan atau catatan
    private String time;

    public ComplaintStatus(String status, String reason, String time) {
        this.status = status;
        this.reason = reason;
        this.time = time;
    }

    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public String getTime() { return time; }
}
