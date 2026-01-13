package com.example.project_uts.Teknisi.Model;

import com.example.project_uts.Teknisi.Model.Komplain;
import java.util.List;

public class TeknisiComplaintsResponse {
    private List<Komplain> complaints;
    private int total;
    private int page;
    private int limit;

    // getters & setters
    public List<Komplain> getComplaints() { return complaints; }
    public void setComplaints(List<Komplain> complaints) { this.complaints = complaints; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getLimit() { return limit; }
    public void setLimit(int limit) { this.limit = limit; }
}