package com.example.project_uts.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_uts.R;
import com.example.project_uts.models.Complaint;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<Complaint> complaints;
    private OnItemClickListener listener;

    private static final String WHATSAPP_NUMBER = "6281234567890"; // Format: 62 tanpa +

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
            Complaint complaint = complaints.get(position);

            // Set basic data
            holder.tvTitle.setText(complaint.getJudul() != null ? complaint.getJudul() : "");
            holder.tvCategory.setText(complaint.getKategori() != null ? complaint.getKategori() : "");
            holder.tvDate.setText(complaint.getTanggal() != null ? complaint.getTanggal() : "");

            String status = complaint.getStatus() != null ? complaint.getStatus() : "";
            holder.tvStatus.setText(status);

            // Set category icon
            setCategoryIcon(holder, complaint.getKategori());

            // Set status style
            setStatusStyle(holder, status);

            // Handle visibility based on status
            handleVisibility(holder, status);

            // Set technician name jika ada
            if (holder.layoutTechnician.getVisibility() == View.VISIBLE) {
                holder.tvTechnician.setText("Budi Santoso"); // Default technician
            }

            // Set rating jika ada
            if (holder.layoutRating.getVisibility() == View.VISIBLE) {
                holder.tvRating.setText("4.5/5"); // Default rating
            }

            // Set rejection reason jika ada
            if (holder.tvRejectionReason.getVisibility() == View.VISIBLE) {
                holder.tvRejectionReason.setText("Alasan: Kerusakan di luar cakupan layanan");
            }

            // Button click listeners
            holder.btnDetail.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(complaint);
                }
            });

            holder.btnChat.setOnClickListener(v -> {
                openWhatsApp(holder.itemView.getContext(), complaint);
            });

        } catch (Exception e) {
            e.printStackTrace();
            setErrorState(holder);
        }
    }

    /**
     * Fungsi untuk membuka WhatsApp dengan pesan otomatis
     */
    private void openWhatsApp(Context context, Complaint complaint) {
        try {
            // Format pesan default
            String message = "Halo Teknisi TeknoServe,\n\n" +
                    "Saya ingin bertanya tentang komplain saya:\n" +
                    "• ID Komplain: " + (complaint.getId() != null ? complaint.getId() : "N/A") + "\n" +
                    "• Judul: " + complaint.getJudul() + "\n" +
                    "• Kategori: " + complaint.getKategori() + "\n" +
                    "• Tanggal: " + complaint.getTanggal() + "\n" +
                    "• Status: " + complaint.getStatus() + "\n\n" +
                    "Bisa dibantu update progress-nya?";

            // Encode message untuk URL
            String encodedMessage = Uri.encode(message);

            // Buat URI WhatsApp
            Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + WHATSAPP_NUMBER + "&text=" + encodedMessage);

            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.whatsapp");

            // Cek apakah WhatsApp terinstall
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                // Jika WhatsApp tidak terinstall, buka browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(browserIntent);
                Toast.makeText(context, "WhatsApp tidak terinstall, membuka browser", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error membuka WhatsApp", Toast.LENGTH_SHORT).show();
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
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_camera); // TV/Electronics
        } else if (categoryLower.contains("ac") || categoryLower.contains("pendingin")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_compass); // AC
        } else if (categoryLower.contains("kulkas") || categoryLower.contains("freezer")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_save); // Kulkas
        } else if (categoryLower.contains("mesin") || categoryLower.contains("cuci")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_rotate); // Mesin Cuci
        } else if (categoryLower.contains("air") || categoryLower.contains("pipa") || categoryLower.contains("keran")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_directions); // Pipa Air
        } else if (categoryLower.contains("listrik") || categoryLower.contains("lampu") || categoryLower.contains("stop")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_my_calendar); // Listrik
        } else if (categoryLower.contains("perabotan") || categoryLower.contains("meja") || categoryLower.contains("kursi")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_edit); // Perabotan
        } else if (categoryLower.contains("internet") || categoryLower.contains("wifi") || categoryLower.contains("jaringan")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_share); // Internet
        } else if (categoryLower.contains("smartphone") || categoryLower.contains("hp") || categoryLower.contains("tablet")) {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_call); // Smartphone
        } else {
            holder.ivCategory.setImageResource(android.R.drawable.ic_menu_help); // Default
        }
    }

    private void setStatusStyle(ViewHolder holder, String status) {
        if (status == null) return;

        String statusLower = status.toLowerCase();
        int backgroundRes;

        switch (statusLower) {
            case "selesai":
            case "completed":
                backgroundRes = R.drawable.badge_success;
                break;
            case "dalam proses":
            case "in progress":
                backgroundRes = R.drawable.badge_warning;
                break;
            case "ditolak":
            case "rejected":
                backgroundRes = R.drawable.badge_danger;
                break;
            default:
                backgroundRes = R.drawable.badge_default;
        }

        try {
            holder.tvStatus.setBackgroundResource(backgroundRes);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback ke background color jika drawable tidak ada
            setStatusColorFallback(holder, statusLower);
        }
    }

    private void setStatusColorFallback(ViewHolder holder, String statusLower) {
        int colorRes;
        switch (statusLower) {
            case "selesai":
                colorRes = android.R.color.holo_green_dark;
                break;
            case "dalam proses":
                colorRes = android.R.color.holo_orange_dark;
                break;
            case "ditolak":
                colorRes = android.R.color.holo_red_dark;
                break;
            default:
                colorRes = android.R.color.darker_gray;
        }

        try {
            holder.tvStatus.setBackgroundColor(holder.itemView.getContext().getResources().getColor(colorRes));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleVisibility(ViewHolder holder, String status) {
        if (status == null) return;

        String statusLower = status.toLowerCase();

        // Sembunyikan semua dulu
        holder.layoutTechnician.setVisibility(View.GONE);
        holder.layoutRating.setVisibility(View.GONE);
        holder.tvRejectionReason.setVisibility(View.GONE);

        // Tampilkan berdasarkan status
        switch (statusLower) {
            case "selesai":
                holder.layoutRating.setVisibility(View.VISIBLE);
                holder.layoutTechnician.setVisibility(View.VISIBLE);
                break;
            case "dalam proses":
                holder.layoutTechnician.setVisibility(View.VISIBLE);
                break;
            case "ditolak":
                holder.tvRejectionReason.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setErrorState(ViewHolder holder) {
        holder.tvTitle.setText("Error loading data - This is a long title to demonstrate scrolling functionality within the card item layout");
        holder.tvCategory.setText("Error Category with long text");
        holder.tvDate.setText("Date not available");
        holder.tvStatus.setText("error");
        holder.layoutTechnician.setVisibility(View.GONE);
        holder.layoutRating.setVisibility(View.GONE);
        holder.tvRejectionReason.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return complaints != null ? complaints.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategory;
        TextView tvTitle, tvCategory, tvDate, tvStatus, tvTechnician, tvRating, tvRejectionReason;
        LinearLayout layoutTechnician, layoutRating, layoutActions;
        com.google.android.material.button.MaterialButton btnDetail, btnChat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

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
}