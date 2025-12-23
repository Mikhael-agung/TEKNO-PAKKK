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
        holder.tvTimeHistory.setText(history.getTime());

        // Atur warna border & status text sesuai status
        if (history.getStatus().equalsIgnoreCase("Pending")) {
            holder.cardView.setStrokeColor(Color.parseColor("#FF9800")); // oranye
            holder.tvStatusHistory.setTextColor(Color.parseColor("#FF9800"));
        } else if (history.getStatus().equalsIgnoreCase("On Progress")) {
            holder.cardView.setStrokeColor(Color.parseColor("#2196F3")); // biru
            holder.tvStatusHistory.setTextColor(Color.parseColor("#2196F3"));
        } else if (history.getStatus().equalsIgnoreCase("Completed")) {
            holder.cardView.setStrokeColor(Color.parseColor("#4CAF50")); // hijau
            holder.tvStatusHistory.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return historyList != null ? historyList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusHistory, tvReasonHistory, tvTimeHistory;
        MaterialCardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView; // root item adalah MaterialCardView
            tvStatusHistory = itemView.findViewById(R.id.tvStatusHistory);
            tvReasonHistory = itemView.findViewById(R.id.tvReasonHistory);
            tvTimeHistory = itemView.findViewById(R.id.tvTimeHistory);
        }
    }
}
