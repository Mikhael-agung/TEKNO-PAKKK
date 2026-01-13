package com.example.project_uts.Teknisi.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Model.HistoryTeknisi;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class HistoryTeknisiAdapter extends RecyclerView.Adapter<HistoryTeknisiAdapter.ViewHolder> {

    private List<HistoryTeknisi> historyList;

    public HistoryTeknisiAdapter(List<HistoryTeknisi> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_teknisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryTeknisi history = historyList.get(position);

        holder.tvStatusHistory.setText(history.getStatus());
        holder.tvReasonHistory.setText(history.getReason());

        // Format tanggal lebih rapi
        String rawDate = history.getCreatedAt();
        if (rawDate != null) {
            try {
                // Sesuaikan pola input dengan format dari backend (ISO biasanya)
                java.text.SimpleDateFormat inputFormat =
                        new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                java.text.SimpleDateFormat outputFormat =
                        new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());

                java.util.Date date = inputFormat.parse(rawDate);
                holder.tvTimeHistory.setText(outputFormat.format(date));
            } catch (Exception e) {
                // fallback kalau parsing gagal
                holder.tvTimeHistory.setText(rawDate);
            }
        } else {
            holder.tvTimeHistory.setText("-");
        }

        if (history.getTeknisi() != null) {
            holder.tvTeknisi.setText(history.getTeknisi().getFull_name());
        } else {
            holder.tvTeknisi.setText("-");
        }

        // Atur warna border & status text sesuai status dari backend
        String status = history.getStatus().toLowerCase();
        switch (status) {
            case "complaint":
                holder.cardView.setStrokeColor(Color.parseColor("#FF9800")); // oranye
                holder.tvStatusHistory.setTextColor(Color.parseColor("#FF9800"));
                break;
            case "pending":
                holder.cardView.setStrokeColor(Color.parseColor("#F44336")); // merah
                holder.tvStatusHistory.setTextColor(Color.parseColor("#F44336"));
                break;
            case "progress":
                holder.cardView.setStrokeColor(Color.parseColor("#2196F3")); // biru
                holder.tvStatusHistory.setTextColor(Color.parseColor("#2196F3"));
                break;
            case "completed":
                holder.cardView.setStrokeColor(Color.parseColor("#4CAF50")); // hijau
                holder.tvStatusHistory.setTextColor(Color.parseColor("#4CAF50"));
                break;
            default:
                holder.cardView.setStrokeColor(Color.parseColor("#9E9E9E")); // abu-abu default
                holder.tvStatusHistory.setTextColor(Color.parseColor("#9E9E9E"));
                break;
        }
    }



    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusHistory, tvReasonHistory, tvTimeHistory, tvTeknisi;
        MaterialCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView); // id root card
            tvStatusHistory = itemView.findViewById(R.id.tvStatusHistory);
            tvReasonHistory = itemView.findViewById(R.id.tvReasonHistory);
            tvTimeHistory = itemView.findViewById(R.id.tvTimeHistory);
            tvTeknisi = itemView.findViewById(R.id.tvTeknisi);
        }
    }

}
