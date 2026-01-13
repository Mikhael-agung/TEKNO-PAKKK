package com.example.project_uts.Teknisi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Model.Komplain;

import java.util.List;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.ViewHolder> {

    private List<Komplain> completedList;

    public CompletedAdapter(List<Komplain> completedList) {
        this.completedList = completedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_completed_teknisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komplain komplain = completedList.get(position);

        holder.tvJudul.setText(komplain.getJudul());
        holder.tvPelapor.setText("Pelapor: " + komplain.getUserId());
        holder.tvStatus.setText("✅ " + komplain.getStatus());
        holder.tvWaktu.setText(komplain.getCreatedAt());
        holder.tvCatatan.setText("Catatan: " + komplain.getDeskripsi());
    }

    @Override
    public int getItemCount() {
        return completedList != null ? completedList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvStatus, tvWaktu, tvCatatan;
        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudulCompleted);
            tvPelapor = itemView.findViewById(R.id.tvPelaporCompleted);
            tvStatus = itemView.findViewById(R.id.tvStatusCompleted);
            tvWaktu = itemView.findViewById(R.id.tvWaktuCompleted);
            tvCatatan = itemView.findViewById(R.id.tvCatatanCompleted);
        }
    }
}
