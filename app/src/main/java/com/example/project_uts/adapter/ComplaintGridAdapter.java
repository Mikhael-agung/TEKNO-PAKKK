package com.example.project_uts.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_uts.R;
import com.example.project_uts.models.Complaint;
import java.util.List;

public class ComplaintGridAdapter extends RecyclerView.Adapter<ComplaintGridAdapter.ViewHolder> {

    private Context context;
    private List<Complaint> complaintList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Complaint complaint);
    }

    public ComplaintGridAdapter(Context context, List<Complaint> complaintList) {
        this.context = context;
        this.complaintList = complaintList != null ? complaintList : new java.util.ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<Complaint> newList) {
        this.complaintList = newList != null ? newList : new java.util.ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_complaint_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Complaint complaint = complaintList.get(position);

        // 1. SET JUDUL
        String judul = complaint.getJudul();
        if (judul != null && !judul.trim().isEmpty()) {
            judul = judul.trim();
            if (judul.length() > 25) {
                judul = judul.substring(0, 25) + "...";
            }
            holder.tvTitle.setText(judul);
        } else {
            holder.tvTitle.setText("Keluhan");
        }

        // 2. SET DESKRIPSI
        String deskripsi = complaint.getDeskripsi();
        if (deskripsi != null && !deskripsi.trim().isEmpty()) {
            deskripsi = deskripsi.trim();
            if (deskripsi.length() > 40) {
                deskripsi = deskripsi.substring(0, 40) + "...";
            }
            holder.tvDescription.setText(deskripsi);
        } else {
            holder.tvDescription.setText("Tidak ada deskripsi");
        }

        // 3. SET TANGGAL
        String tanggal = formatDateForDisplay(complaint);
        holder.tvDate.setText(tanggal);

        // 4. SET STATUS & BORDER
        String status = complaint.getStatus();
        if (status != null) {
            status = status.toLowerCase().trim();

            String statusText;
            int badgeDrawable;

            if (status.equals("pending")) {
                statusText = "MENUNGGU";
                badgeDrawable = R.drawable.badge_danger;
                setCardOutlineByStatus(holder.cardView, "pending");
            }
            else if (status.equals("on_progress") || status.equals("in_progress")) {
                statusText = "DIPROSES";
                badgeDrawable = R.drawable.badge_warning;
                setCardOutlineByStatus(holder.cardView, "on_progress");
            }
            else if (status.equals("complete") || status.equals("completed") || status.equals("selesai")) {
                statusText = "SELESAI";
                badgeDrawable = R.drawable.badge_success;
                setCardOutlineByStatus(holder.cardView, "completed");
            }
            else {
                statusText = status.toUpperCase();
                badgeDrawable = R.drawable.badge_default;
                setCardOutlineByStatus(holder.cardView, "default");
            }

            holder.tvStatusBadge.setText(statusText);
            holder.tvStatusBadge.setBackgroundResource(badgeDrawable);

        } else {
            holder.tvStatusBadge.setText("UNKNOWN");
            holder.tvStatusBadge.setBackgroundResource(R.drawable.badge_default);
            setCardOutlineByStatus(holder.cardView, "default");
        }

        // 5. SET COMPLAINT ID
        String complaintId = complaint.getId();
        if (complaintId != null && !complaintId.isEmpty()) {
            if (complaintId.length() > 8) {
                holder.tvComplaintId.setText("ID:" + complaintId.substring(0, 6) + "...");
            } else {
                holder.tvComplaintId.setText("ID:" + complaintId);
            }
        } else {
            holder.tvComplaintId.setText("ID:---");
        }

        // 6. CLICK LISTENER
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(complaint);
            }
        });
    }

    private String formatDateForDisplay(Complaint complaint) {
        try {
            if (complaint.getFormattedDate() != null &&
                    !complaint.getFormattedDate().isEmpty()) {
                return complaint.getFormattedDate();
            }

            if (complaint.getTanggal() != null &&
                    !complaint.getTanggal().isEmpty()) {
                return formatDateShort(complaint.getTanggal());
            }

            if (complaint.getCreated_at() != null) {
                return formatDateShort(complaint.getCreated_at());
            }

        } catch (Exception e) {
            Log.e("ComplaintAdapter", "Error formatting date: " + e.getMessage());
        }

        return "-";
    }

    private String formatDateShort(String dateString) {
        try {
            if (dateString.contains("T")) {
                String datePart = dateString.split("T")[0];
                String[] parts = datePart.split("-");
                if (parts.length >= 3) {
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int day = Integer.parseInt(parts[2]);

                    String[] bulan = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};

                    if (month >= 1 && month <= 12) {
                        return day + " " + bulan[month-1] + " " + year;
                    }
                }
            }

            if (dateString.contains("/")) {
                String[] parts = dateString.split("/");
                if (parts.length >= 3) {
                    int day = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]);
                    int year = Integer.parseInt(parts[2]);

                    String[] bulan = {"Jan", "Feb", "Mar", "Apr", "Mei", "Jun",
                            "Jul", "Agu", "Sep", "Okt", "Nov", "Des"};

                    if (month >= 1 && month <= 12) {
                        return day + " " + bulan[month-1] + " " + year;
                    }
                }
            }

        } catch (Exception e) {
            Log.e("ComplaintAdapter", "Error parsing date: " + dateString);
        }

        return dateString.length() > 10 ? dateString.substring(0, 10) : dateString;
    }

    private void setCardOutlineByStatus(View cardView, String status) {
        if (cardView == null) return;

        int backgroundResId = R.drawable.bg_card_outline_default;

        if (status != null) {
            switch (status.toLowerCase()) {
                case "pending":
                    backgroundResId = R.drawable.bg_card_outline_pending;
                    break;
                case "on_progress":
                case "in_progress":
                case "dalam proses":
                    backgroundResId = R.drawable.bg_card_outline_proses;
                    break;
                case "completed":
                case "selesai":
                case "complete":
                    backgroundResId = R.drawable.bg_card_outline_selesai;
                    break;
                default:
                    backgroundResId = R.drawable.bg_card_outline_default;
            }
        }

        try {
            cardView.setBackgroundResource(backgroundResId);
        } catch (Exception e) {
            Log.e("ComplaintAdapter", "Error setting border: " + e.getMessage());
            cardView.setBackgroundResource(R.drawable.bg_card_outline_default);
        }
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public void filterList(List<Complaint> filteredList) {
        complaintList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView tvDate;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvStatusBadge;
        TextView tvComplaintId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.card_complaint);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);
            tvComplaintId = itemView.findViewById(R.id.tv_complaint_id);
        }
    }
}