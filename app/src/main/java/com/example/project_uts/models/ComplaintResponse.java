// ComplaintResponse.java
package com.example.project_uts.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ComplaintResponse {
    @SerializedName("complaints")
    private List<Complaint> complaints;

    @SerializedName("pagination")
    private Pagination pagination;

    // Getter dan Setter
    public List<Complaint> getComplaints() {
        return complaints;
    }

    public void setComplaints(List<Complaint> complaints) {
        this.complaints = complaints;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    // Pagination class
    public static class Pagination {
        @SerializedName("total")
        private int total;

        @SerializedName("page")
        private int page;

        @SerializedName("limit")
        private int limit;

        @SerializedName("total_pages")
        private int totalPages;

        // Getter dan Setter
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getLimit() { return limit; }
        public void setLimit(int limit) { this.limit = limit; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}