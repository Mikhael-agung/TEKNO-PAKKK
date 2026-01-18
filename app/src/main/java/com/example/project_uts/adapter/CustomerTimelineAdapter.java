package com.example.project_uts.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Model.HistoryTeknisi;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomerTimelineAdapter extends RecyclerView.Adapter<CustomerTimelineAdapter.ViewHolder> {

    private Context context;
    private List<HistoryTeknisi> timelineItems;

    public CustomerTimelineAdapter(Context context, List<HistoryTeknisi> timelineItems) {
        this.context = context;
        this.timelineItems = timelineItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryTeknisi history = timelineItems.get(position);

        // 1. STATUS BADGE - PAKAI YANG SUDAH ADA
        String status = history.getStatus().toLowerCase();
        String statusText = formatStatusText(status);
        int badgeRes = getBadgeResource(status);

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setBackgroundResource(badgeRes);
        holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));

        // 2. REASON/MESSAGE - PAKAI YANG SUDAH ADA
        if (history.getReason() != null && !history.getReason().isEmpty()) {
            holder.tvReason.setText(history.getReason());
            holder.tvReason.setVisibility(View.VISIBLE);
        } else {
            holder.tvReason.setVisibility(View.GONE);
        }

        // 3. DATE - PAKAI YANG SUDAH ADA
        holder.tvDate.setText(formatDate(history.getCreatedAt()));

        // 4. TEKNISI - PAKAI YANG SUDAH ADA
        if (history.getTeknisi() != null && history.getTeknisi().getFull_name() != null) {
            holder.tvTechnician.setText(history.getTeknisi().getFull_name());
            holder.tvTechnician.setVisibility(View.VISIBLE);
        } else {
            holder.tvTechnician.setVisibility(View.GONE);
        }

        // 5. OUTLINE WARNA SESUAI STATUS - TAMBAHKAN INI SAJA
        setOutlineColor(holder, status);
    }

    // TAMBAHKAN METHOD INI SAJA
    private void setOutlineColor(ViewHolder holder, String status) {
        int outlineColor;

        // PAKAI WARNA YANG SUDAH ADA DI PROJECT
        switch (status.toLowerCase()) {
            case "completed":
            case "selesai":
                outlineColor = ContextCompat.getColor(context, R.color.status_completed);
                break;
            case "on_progress":
            case "proses":
                outlineColor = ContextCompat.getColor(context, R.color.status_progress);
                break;
            case "pending":
                outlineColor = ContextCompat.getColor(context, R.color.status_pending);
                break;
            case "complaint":
                outlineColor = ContextCompat.getColor(context, R.color.colorPrimary); // Pakai warna primary
                break;
            default:
                outlineColor = ContextCompat.getColor(context, R.color.gray); // Pakai gray yang sudah ada
        }

        holder.cardView.setStrokeColor(outlineColor);
        holder.cardView.setStrokeWidth(dpToPx(2));
    }

    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private boolean isDarkMode() {
        int nightModeFlags = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }

    private String formatStatusText(String status) {
        switch (status) {
            case "completed": return "SELESAI";
            case "on_progress": return "DIPROSES";
            case "pending": return "PENDING";
            case "complaint": return "KOMPLAIN DIBUAT";
            default: return status.toUpperCase();
        }
    }

    private int getBadgeResource(String status) {
        switch (status) {
            case "completed": return R.drawable.badge_selesai;
            case "on_progress": return R.drawable.badge_proses;
            case "pending": return R.drawable.badge_pending;
            default: return R.drawable.badge_default;
        }
    }

    private String formatDate(String dateStr) {
        try {
            String cleanDate = dateStr;
            if (dateStr.contains(".")) {
                cleanDate = dateStr.split("\\.")[0];
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            Date date = inputFormat.parse(cleanDate);
            return outputFormat.format(date);
        } catch (Exception e) {
            try {
                return dateStr.substring(0, 16).replace("T", " ");
            } catch (Exception ex) {
                return dateStr;
            }
        }
    }

    @Override
    public int getItemCount() {
        return timelineItems != null ? timelineItems.size() : 0;
    }

    public void updateData(List<HistoryTeknisi> newItems) {
        timelineItems.clear();
        timelineItems.addAll(newItems);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvStatus, tvReason, tvDate, tvTechnician;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_timeline);
            tvStatus = itemView.findViewById(R.id.tv_timeline_status);
            tvReason = itemView.findViewById(R.id.tv_timeline_title);
            tvDate = itemView.findViewById(R.id.tv_timeline_date);
            tvTechnician = itemView.findViewById(R.id.tv_timeline_note);
        }
    }
}