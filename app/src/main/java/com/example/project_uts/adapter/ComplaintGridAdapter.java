package com.example.project_uts.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_uts.R;
import com.example.project_uts.models.Complaint;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

        Log.d("ComplaintAdapter", "Binding item " + position + ": " +
                complaint.getId() + " - " + complaint.getStatus());

        String id = complaint.getId() != null ? complaint.getId() : "N/A";
        holder.tvTitle.setText(id.length() > 10 ? id.substring(0, 10) + "..." : id);

        String desc = complaint.getDeskripsi() != null ? complaint.getDeskripsi() : "No description";
        holder.tvDescription.setText(desc.length() > 25 ? desc.substring(0, 25) + "..." : desc);

        if (complaint.getId() != null && !complaint.getId().isEmpty()) {
            String shortId = formatShortId(complaint.getId());
            holder.tvTitle.setText(shortId);
        } else if (complaint.getJudul() != null && !complaint.getJudul().isEmpty()) {
            // Pakai judul jika ID tidak ada
            String shortTitle = complaint.getJudul();
            if (shortTitle.length() > 15) {
                shortTitle = shortTitle.substring(0, 15) + "...";
            }
            holder.tvTitle.setText(shortTitle);
        } else {
            holder.tvTitle.setText("COMP-" + (position + 1));
        }

        // Set deskripsi singkat
        String deskripsi = complaint.getDeskripsi();
        if (deskripsi != null && !deskripsi.isEmpty()) {
            if (deskripsi.length() > 25) {
                deskripsi = deskripsi.substring(0, 25) + "...";
            }
            holder.tvDescription.setText(deskripsi);
        } else {
            holder.tvDescription.setText("Tidak ada deskripsi");
        }

        String tanggal = complaint.getTanggal();
        if (tanggal != null && !tanggal.isEmpty()) {
            holder.tvDate.setText(tanggal);
        } else {
            // Fallback: format created_at langsung
            String createdAt = complaint.getCreated_at();
            if (createdAt != null && !createdAt.isEmpty()) {
                String shortDate = formatDateShort(createdAt);
                holder.tvDate.setText(shortDate);
            } else {
                holder.tvDate.setText("-");
            }
        }

        // Get status (handle null)
        String status = complaint.getStatus() != null ? complaint.getStatus().toLowerCase() : "";

        // Set outline card berdasarkan status
        setCardOutlineByStatus(holder.linearLayout, status);

        // Set status badge text
        String statusText = getStatusText(status);
        holder.tvStatusBadge.setText(statusText);

        // Set warna badge berdasarkan status
        setBadgeColorByStatus(holder.tvStatusBadge, status);

        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(complaint);
            }
        });
    }

    private String formatShortId(String id) {
        // Jika ID panjang seperti "65a1b2c3d4e5f6"
        if (id.length() > 8) {
            return "ID:" + id.substring(0, 6) + "...";
        }
        return id;
    }

    private String formatDateShort(String dateString) {
        try {
            // Coba parse format ISO: "2024-12-10T10:30:00.000Z"
            if (dateString.contains("T")) {
                String datePart = dateString.split("T")[0];
                String[] parts = datePart.split("-");
                if (parts.length >= 3) {
                    // Format: "10/12"
                    return parts[2] + "/" + parts[1];
                }
            }
            // Coba format lain
            SimpleDateFormat[] inputFormats = {
                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()),
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
                    new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            };

            Date date = null;
            for (SimpleDateFormat format : inputFormats) {
                try {
                    date = format.parse(dateString);
                    if (date != null) break;
                } catch (Exception e) {
                    // Continue to next format
                }
            }

            if (date != null) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            // Ignore
        }

        // Jika semua gagal, potong string
        return dateString.length() > 10 ? dateString.substring(0, 10) : dateString;
    }

    private String getStatusText(String status) {
        if (status == null || status.isEmpty()) return "UNKNOWN";

        switch (status) {
            case "pending":
                return "PENDING";
            case "in_progress":
            case "dalam proses":
                return "PROSES";
            case "completed":
            case "selesai":
                return "SELESAI";
            case "ditolak":
            case "rejected":
            case "canceled":
            case "cancelled":
                return "DITOLAK";
            default:
                return status.substring(0, 1).toUpperCase() + status.substring(1).toUpperCase();
        }
    }

    private void setCardOutlineByStatus(LinearLayout layout, String status) {
        if (layout == null) return;

        int backgroundResId = R.drawable.bg_card_outline_default; // default

        if (status != null) {
            switch (status) {
                case "pending":
                    backgroundResId = R.drawable.bg_card_outline_pending;
                    break;
                case "in_progress":
                case "dalam proses":
                    backgroundResId = R.drawable.bg_card_outline_proses;
                    break;
                case "completed":
                case "selesai":
                    backgroundResId = R.drawable.bg_card_outline_selesai;
                    break;
                case "ditolak":
                case "rejected":
                case "canceled":
                case "cancelled":
                    backgroundResId = R.drawable.bg_card_outline_ditolak;
                    break;
            }
        }

        try {
            layout.setBackgroundResource(backgroundResId);
        } catch (Exception e) {
            layout.setBackgroundResource(R.drawable.bg_card_outline_default);
        }
    }

    private void setBadgeColorByStatus(TextView badge, String status) {
        if (badge == null) return;

        int backgroundResId = R.drawable.badge_default;

        if (status != null) {
            switch (status) {
                case "pending":
                    backgroundResId = R.drawable.badge_danger;
                    break;
                case "in_progress":
                case "dalam proses":
                    backgroundResId = R.drawable.badge_warning;
                    break;
                case "completed":
                case "selesai":
                    backgroundResId = R.drawable.badge_success;
                    break;
                case "ditolak":
                case "rejected":
                case "canceled":
                case "cancelled":
                    backgroundResId = R.drawable.badge_ditolak;
                    break;
            }
        }

        try {
            badge.setBackgroundResource(backgroundResId);
        } catch (Exception e) {
            // Fallback jika drawable tidak ada
            badge.setBackgroundResource(R.drawable.badge_default);
        }
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    // Helper untuk filter di Fragment
    public void filterList(List<Complaint> filteredList) {
        complaintList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        LinearLayout linearLayout;
        TextView tvDate;
        TextView tvTitle;
        TextView tvDescription;
        TextView tvStatusBadge;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inisialisasi semua view
            cardView = itemView.findViewById(R.id.card_complaint);
            linearLayout = itemView.findViewById(R.id.linear_layout_main);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvStatusBadge = itemView.findViewById(R.id.tv_status_badge);

            //       Validasi
            if (linearLayout == null) {
                throw new RuntimeException("LinearLayout dengan id 'linear_layout_main' tidak ditemukan di item_complaint_grid.xml");
            }
        }
    }
}