package com.example.project_uts.Teknisi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Activity.KomplainDetailActivity;
import com.example.project_uts.Teknisi.Model.Komplain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.ViewHolder> {

    private Context context;
    private List<Komplain> completedList;

    public CompletedAdapter(List<Komplain> completedList) {
        this.completedList = completedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_completed_teknisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komplain komplain = completedList.get(position);

        holder.tvJudulCompleted.setText(komplain.getJudul());
        holder.tvPelaporCompleted.setText("Pelapor: " + komplain.getUser().getFull_name());
        holder.tvAlamatCompleted.setText("Alamat: " + komplain.getAlamat());
        holder.tvDeskripsiCompleted.setText(komplain.getDeskripsi());

        // Status selalu "Completed"
        holder.tvStatusCompleted.setText("Completed");
        holder.tvStatusCompleted.setTextColor(android.graphics.Color.parseColor("#4CAF50"));

        // Format waktu
        String rawDate = komplain.getTanggal();
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy • HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(rawDate);
            holder.tvWaktuCompleted.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvWaktuCompleted.setText(rawDate);
        }

        // Catatan teknisi tidak dipakai → bisa diisi default
        holder.tvCatatanCompleted.setText("Riwayat komplain selesai");

        // Klik item → buka detail activity (tanpa tombol aksi)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, KomplainDetailActivity.class);
            intent.putExtra("complaint_id", komplain.getId());
            intent.putExtra("komplain_judul", komplain.getJudul());
            intent.putExtra("komplain_pelapor", komplain.getUser().getFull_name());
            intent.putExtra("komplain_status", komplain.getStatus()); // penting untuk hide tombol
            intent.putExtra("komplain_waktu", komplain.getTanggal());
            intent.putExtra("komplain_alamat", komplain.getAlamat());
            intent.putExtra("komplain_deskripsi", komplain.getDeskripsi());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return completedList != null ? completedList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudulCompleted, tvPelaporCompleted, tvAlamatCompleted,
                tvDeskripsiCompleted, tvStatusCompleted, tvWaktuCompleted, tvCatatanCompleted;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudulCompleted = itemView.findViewById(R.id.tvJudulCompleted);
            tvPelaporCompleted = itemView.findViewById(R.id.tvPelaporCompleted);
            tvAlamatCompleted = itemView.findViewById(R.id.tvAlamatCompleted);
            tvDeskripsiCompleted = itemView.findViewById(R.id.tvDeskripsiCompleted);
            tvStatusCompleted = itemView.findViewById(R.id.tvStatusCompleted);
            tvWaktuCompleted = itemView.findViewById(R.id.tvWaktuCompleted);
            tvCatatanCompleted = itemView.findViewById(R.id.tvCatatanCompleted);
        }
    }
}
