package com.example.project_uts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KomplainAdapter extends RecyclerView.Adapter<KomplainAdapter.ViewHolder> {

    private List<Komplain> komplainList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Komplain komplain);
        void onWhatsAppClick(Komplain komplain);
        void onMintaBantuanClick(Komplain komplain);
    }

    public KomplainAdapter(List<Komplain> komplainList, OnItemClickListener listener) {
        this.komplainList = komplainList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_komplain, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komplain komplain = komplainList.get(position);

        // Set data ke view
        holder.tvJudul.setText(komplain.getJudul());
        holder.tvPelapor.setText("Oleh: " + komplain.getPelapor());
        holder.tvStatus.setText(komplain.getStatus());
        holder.tvWaktu.setText(komplain.getWaktu());

        // Set warna status
        int statusColor = getStatusColor(komplain.getStatus());
        holder.tvStatus.setTextColor(statusColor);

        // Tampilkan catatan teknisi jika ada
        if (komplain.getTeknisiNote() != null && !komplain.getTeknisiNote().isEmpty()) {
            holder.tvCatatan.setVisibility(View.VISIBLE);
            holder.tvCatatan.setText("📝 " + komplain.getTeknisiNote());
        } else {
            holder.tvCatatan.setVisibility(View.GONE);
        }

        // Tampilkan alasan pending jika ada
        if (komplain.getAlasanPending() != null && !komplain.getAlasanPending().isEmpty()) {
            holder.tvAlasanPending.setVisibility(View.VISIBLE);
            holder.tvAlasanPending.setText("⏳ " + komplain.getAlasanPending());
        } else {
            holder.tvAlasanPending.setVisibility(View.GONE);
        }

        // Click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(komplain);
            }
        });

        holder.btnWhatsApp.setOnClickListener(v -> {
            if (listener != null) {
                listener.onWhatsAppClick(komplain);
            }
        });

        holder.btnMintaBantuan.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMintaBantuanClick(komplain);
            }
        });
    }

    private int getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "selesai":
                return Color.parseColor("#4CAF50"); // Hijau
            case "dalam proses":
                return Color.parseColor("#2196F3"); // Biru
            case "pending":
                return Color.parseColor("#FF9800"); // Orange
            default:
                return Color.parseColor("#666666"); // Abu-abu
        }
    }

    @Override
    public int getItemCount() {
        return komplainList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvStatus, tvWaktu, tvCatatan, tvAlasanPending;
        ImageView btnWhatsApp, btnMintaBantuan;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPelapor = itemView.findViewById(R.id.tvPelapor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvCatatan = itemView.findViewById(R.id.tvCatatan);
            tvAlasanPending = itemView.findViewById(R.id.tvAlasanPending);
            btnWhatsApp = itemView.findViewById(R.id.btnWhatsApp);
            btnMintaBantuan = itemView.findViewById(R.id.btnMintaBantuan);
        }
    }
}