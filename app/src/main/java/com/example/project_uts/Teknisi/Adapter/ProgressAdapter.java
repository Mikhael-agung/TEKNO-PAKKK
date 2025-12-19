package com.example.project_uts.Teknisi.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Model.Komplain;

import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {
    private List<Komplain> progressList;
    private Context context;

    public ProgressAdapter(Context context, List<Komplain> progressList) {
        this.context = context;
        this.progressList = progressList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress_teknisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komplain komplain = progressList.get(position);

        holder.tvJudul.setText(komplain.getTitle());
        holder.tvPelapor.setText("Pelapor: " + komplain.getUserId()); // kalau sudah join nama
        holder.tvWaktu.setText(komplain.getCreatedAt());

        // Status handling
        String status = komplain.getStatus();
        holder.tvStatus.setText(status);

        if ("on_progress".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#2196F3")); // biru
            holder.btnMintaBantuan.setVisibility(View.VISIBLE);
            holder.btnDetail.setVisibility(View.VISIBLE);
            // misal tombol khusus "Pending" di-hide
            // holder.btnPending.setVisibility(View.GONE);
        } else if ("pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FF9800")); // oranye
            holder.btnMintaBantuan.setVisibility(View.VISIBLE);
            holder.btnDetail.setVisibility(View.VISIBLE);
            // kalau ada tombol "On Progress" → hide
            // holder.btnOnProgress.setVisibility(View.GONE);
        }

        // tombol aksi
        holder.btnCustomer.setOnClickListener(v -> {
            Toast.makeText(context, "Hubungi customer", Toast.LENGTH_SHORT).show();
        });
        holder.btnMintaBantuan.setOnClickListener(v -> {
            Toast.makeText(context, "Minta bantuan teknisi lain", Toast.LENGTH_SHORT).show();
        });
        holder.btnDetail.setOnClickListener(v -> {
            // buka detail activity
        });
    }


    @Override
    public int getItemCount() {
        return progressList != null ? progressList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvStatus, tvWaktu;
        Button btnCustomer, btnMintaBantuan, btnDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPelapor = itemView.findViewById(R.id.tvPelapor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            btnCustomer = itemView.findViewById(R.id.btnCustomer);
            btnMintaBantuan = itemView.findViewById(R.id.btnMintaBantuan);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}
