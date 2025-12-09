package com.example.project_uts;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        // Icon & warna status
        if (history.getStatus().equalsIgnoreCase("Pending")) {
            holder.iconStatus.setText("⏳");
            holder.tvStatusHistory.setTextColor(Color.parseColor("#FF9800"));
        } else if (history.getStatus().equalsIgnoreCase("Completed")) {
            holder.iconStatus.setText("✅");
            holder.tvStatusHistory.setTextColor(Color.parseColor("#4CAF50"));
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatusHistory, tvReasonHistory, tvTimeHistory, iconStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            LinearLayout statusLayout = (LinearLayout) ((LinearLayout) itemView).getChildAt(0);
            iconStatus = (TextView) statusLayout.getChildAt(0);
            tvStatusHistory = itemView.findViewById(R.id.tvStatusHistory);
            tvReasonHistory = itemView.findViewById(R.id.tvReasonHistory);
            tvTimeHistory = itemView.findViewById(R.id.tvTimeHistory);
        }
    }
}
