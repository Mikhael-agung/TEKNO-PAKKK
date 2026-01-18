package com.example.project_uts.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.fragment.ComplaintDetailActivity;
import com.example.project_uts.R;
import com.example.project_uts.models.Complaint;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Complaint> complaints;
    private OnItemClickListener listener;

    public HistoryAdapter(List<Complaint> complaints, OnItemClickListener listener) {
        this.complaints = complaints != null ? complaints : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_complaint, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            final Complaint complaint = complaints.get(position);

            // Set basic data
            holder.tvTitle.setText(complaint.getJudul() != null ? complaint.getJudul() : "");
            holder.tvCategory.setText(complaint.getKategori() != null ? complaint.getKategori() : "");
            holder.tvDate.setText(complaint.getTanggal() != null ? complaint.getTanggal() : "");

            String status = complaint.getStatus() != null ? complaint.getStatus() : "";

            // 1. SET CARD STROKE COLOR BERDASARKAN STATUS
            if (holder.cardComplaint != null) {
                setCardStrokeColor(holder.cardComplaint, status);
            }

            // 2. SET STATUS TEXT DAN BADGE
            setStatusStyle(holder, status);

            // Set category icon
            setCategoryIcon(holder, complaint.getKategori());

            // Handle visibility based on status
            handleVisibility(holder, status, complaint);

            // ============ SET TEKNISI NAMA DARI DATA REAL ============
            if (holder.layoutTechnician.getVisibility() == View.VISIBLE) {
                // Ambil nama teknisi dari Complaint object
                String teknisiName = complaint.getTeknisi_name();
                if (teknisiName != null && !teknisiName.isEmpty() &&
                        !teknisiName.equalsIgnoreCase("Belum ditugaskan")) {
                    holder.tvTechnician.setText(teknisiName);
                } else {
                    // Jika belum ada teknisi, sembunyikan layout
                    holder.layoutTechnician.setVisibility(View.GONE);
                }
            }

            // Button click listeners
            holder.btnDetail.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ComplaintDetailActivity.class);
                intent.putExtra("complaint", complaint);
                v.getContext().startActivity(intent);
            });

            // ✅ WHATSAPP FUNCTION REMOVED - User harus ke detail dulu
            holder.btnChat.setOnClickListener(v -> {
                // Navigate ke detail untuk hubungi teknisi
                Intent intent = new Intent(v.getContext(), ComplaintDetailActivity.class);
                intent.putExtra("complaint", complaint);
                v.getContext().startActivity(intent);
            });

        } catch (Exception e) {
            e.printStackTrace();
            setErrorState(holder);
        }
    }

    private void setCardStrokeColor(MaterialCardView cardView, String status) {
        if (cardView == null) return;

        String statusLower = status.toLowerCase();
        Context context = cardView.getContext();

        // Map status ke warna stroke
        int strokeColor;

        if (statusLower.contains("pending") || statusLower.contains("menunggu")) {
            strokeColor = ContextCompat.getColor(context, R.color.status_pending); // #FF9800
        } else if (statusLower.contains("proses") || statusLower.contains("progress") ||
                statusLower.contains("dalam proses") || statusLower.contains("on progress")) {
            strokeColor = ContextCompat.getColor(context, R.color.status_progress); // #2196F3
        } else if (statusLower.contains("selesai") || statusLower.contains("completed") ||
                statusLower.contains("done")) {
            strokeColor = ContextCompat.getColor(context, R.color.status_completed); // #4CAF50
        }  else {
            strokeColor = ContextCompat.getColor(context, R.color.status_default); // #666666
        }

        // Set stroke color dan width ke MaterialCardView
        cardView.setStrokeColor(strokeColor);
        cardView.setStrokeWidth(dpToPx(context, 2)); // 2dp
    }

    private int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    private void setStatusStyle(ViewHolder holder, String status) {
        if (status == null) {
            holder.tvStatus.setText("UNKNOWN");
            holder.tvStatus.setBackgroundResource(R.drawable.badge_default);
            return;
        }

        String statusLower = status.toLowerCase();

        // Set status text dan badge
        if (statusLower.contains("pending") || statusLower.contains("menunggu")) {
            holder.tvStatus.setText("PENDING");
            holder.tvStatus.setBackgroundResource(R.drawable.badge_pending);
        } else if (statusLower.contains("proses") || statusLower.contains("progress") ||
                statusLower.contains("dalam proses") || statusLower.contains("on progress")) {
            holder.tvStatus.setText("PROSES");
            holder.tvStatus.setBackgroundResource(R.drawable.badge_proses);
        } else if (statusLower.contains("selesai") || statusLower.contains("completed") ||
                statusLower.contains("done")) {
            holder.tvStatus.setText("SELESAI");
            holder.tvStatus.setBackgroundResource(R.drawable.badge_selesai);
        } else {
            holder.tvStatus.setText(status.toUpperCase());
            holder.tvStatus.setBackgroundResource(R.drawable.badge_default);
        }
    }

    private void setCategoryIcon(ViewHolder holder, String category) {
        if (category == null) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_help);
            return;
        }

        // Set icon berdasarkan kategori
        String categoryLower = category.toLowerCase();

        // Gunakan icon Android built-in yang sesuai
        if (categoryLower.contains("elektronik") || categoryLower.contains("tv") || categoryLower.contains("laptop")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_camera);
        } else if (categoryLower.contains("ac") || categoryLower.contains("pendingin")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_compass);
        } else if (categoryLower.contains("kulkas") || categoryLower.contains("freezer")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_save);
        } else if (categoryLower.contains("mesin") || categoryLower.contains("cuci")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_rotate);
        } else if (categoryLower.contains("air") || categoryLower.contains("pipa") || categoryLower.contains("keran")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_directions);
        } else if (categoryLower.contains("listrik") || categoryLower.contains("lampu") || categoryLower.contains("stop")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_my_calendar);
        } else if (categoryLower.contains("perabotan") || categoryLower.contains("meja") || categoryLower.contains("kursi")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_edit);
        } else if (categoryLower.contains("internet") || categoryLower.contains("wifi") || categoryLower.contains("jaringan")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_share);
        } else if (categoryLower.contains("smartphone") || categoryLower.contains("hp") || categoryLower.contains("tablet")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_call);
        } else {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_help);
        }
    }

    private void handleVisibility(ViewHolder holder, String status, Complaint complaint) {
        if (status == null) return;

        String statusLower = status.toLowerCase();

        // Sembunyikan semua dulu
        holder.layoutTechnician.setVisibility(View.GONE);
        holder.layoutRating.setVisibility(View.GONE);
        holder.tvRejectionReason.setVisibility(View.GONE);

        // ============ LOGIC VISIBILITY DARI DATA REAL ============
        boolean hasTeknisi = complaint.getTeknisi_name() != null &&
                !complaint.getTeknisi_name().isEmpty() &&
                !complaint.getTeknisi_name().equalsIgnoreCase("Belum ditugaskan");

        switch (statusLower) {
            case "selesai":
            case "completed":
                // Tampilkan rating untuk komplain selesai
                holder.layoutRating.setVisibility(View.VISIBLE);

                // Tampilkan teknisi jika ada
                if (hasTeknisi) {
                    holder.layoutTechnician.setVisibility(View.VISIBLE);
                }
                break;

            case "dalam proses":
            case "on_progress":
            case "progress":
            case "assigned":
                // Tampilkan teknisi jika ada
                if (hasTeknisi) {
                    holder.layoutTechnician.setVisibility(View.VISIBLE);
                }
                break;

            case "ditolak":
            case "rejected":
                // Tampilkan alasan penolakan
                holder.tvRejectionReason.setVisibility(View.VISIBLE);
                break;

            case "pending":
                // Tampilkan teknisi jika sudah ditugaskan meski status pending
                if (hasTeknisi) {
                    holder.layoutTechnician.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void setErrorState(ViewHolder holder) {
        holder.tvTitle.setText("Error loading data");
        holder.tvCategory.setText("Error Category");
        holder.tvDate.setText("Date not available");
        holder.tvStatus.setText("ERROR");
        holder.layoutTechnician.setVisibility(View.GONE);
        holder.layoutRating.setVisibility(View.GONE);
        holder.tvRejectionReason.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return complaints != null ? complaints.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardComplaint;
        ImageView ivCategory;
        TextView tvTitle, tvCategory, tvDate, tvStatus, tvTechnician, tvRating, tvRejectionReason;
        LinearLayout layoutTechnician, layoutRating, layoutActions;
        com.google.android.material.button.MaterialButton btnDetail, btnChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardComplaint = itemView.findViewById(R.id.card_complaint);

            // Initialize semua view
            ivCategory = itemView.findViewById(R.id.iv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTechnician = itemView.findViewById(R.id.tv_technician);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvRejectionReason = itemView.findViewById(R.id.tv_rejection_reason);

            layoutTechnician = itemView.findViewById(R.id.layout_technician);
            layoutRating = itemView.findViewById(R.id.layout_rating);
            layoutActions = itemView.findViewById(R.id.layout_actions);

            btnDetail = itemView.findViewById(R.id.btn_detail);
            btnChat = itemView.findViewById(R.id.btn_chat);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Complaint complaint);
    }

    // Method untuk update data
    public void updateData(List<Complaint> newComplaints) {
        complaints.clear();
        complaints.addAll(newComplaints);
        notifyDataSetChanged();
    }
}