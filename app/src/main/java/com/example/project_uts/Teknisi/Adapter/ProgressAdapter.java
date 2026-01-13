package com.example.project_uts.Teknisi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.ViewHolder> {
    private final List<Komplain> progressList;
    private final Context context;
    private final String namaTeknisi;
    private final OnItemClickListener listener;

    // Listener interface untuk aksi tombol
    public interface OnItemClickListener {
        void onMintaBantuanClick(Komplain komplain);
    }

    public ProgressAdapter(Context context, List<Komplain> progressList, String namaTeknisi, OnItemClickListener listener) {
        this.context = context;
        this.progressList = progressList;
        this.namaTeknisi = namaTeknisi;
        this.listener = listener;
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

        holder.tvJudul.setText(komplain.getJudul());
        holder.tvAlamat.setText("Alamat: " + (komplain.getAlamat() != null ? komplain.getAlamat() : "-"));
        holder.tvDeskripsi.setText("Deskripsi: " + (komplain.getDeskripsi() != null ? komplain.getDeskripsi() : "-"));

        if (komplain.getUser() != null) {
            holder.tvPelapor.setText("Pelapor: " + komplain.getUser().getFull_name());
        } else {
            holder.tvPelapor.setText("Pelapor: -");
        }

        // Format tanggal ISO ke format lokal
        try {
            String rawDate = komplain.getTanggal();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(rawDate);
            holder.tvWaktu.setText(outputFormat.format(date));
        } catch (Exception e) {
            holder.tvWaktu.setText(komplain.getTanggal());
        }

        // Status handling
        String status = komplain.getStatus();
        if ("on_progress".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Progress");
            holder.tvStatus.setTextColor(Color.parseColor("#2196F3")); // biru
        } else if ("pending".equalsIgnoreCase(status)) {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setTextColor(Color.parseColor("#FF9800")); // oranye
        } else {
            holder.tvStatus.setText(status);
            holder.tvStatus.setBackgroundColor(Color.GRAY);
        }

        // Tombol aksi
        holder.btnCustomer.setOnClickListener(v -> {
            if (komplain.getUser() != null && komplain.getUser().getPhone() != null) {
                String phoneNumber = komplain.getUser().getPhone();
                String idKomplain = komplain.getId();
                String judulKomplain = komplain.getJudul();

                String pesan = "Halo, perkenalkan saya " + namaTeknisi + "\n"
                        + "Saya teknisi yang akan menangani komplain Anda.\n"
                        + "ID Komplain: " + idKomplain + "\n"
                        + "Judul: " + judulKomplain + "\n\n"
                        + "Mohon konfirmasi jika informasi ini sudah sesuai.\n"
                        + "Terima kasih ";

                String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(pesan);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });

        holder.btnMintaBantuan.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMintaBantuanClick(komplain); // kirim komplain ke fragment
            }
        });

        holder.btnDetail.setOnClickListener(v -> {
            Intent intent = new Intent(context, KomplainDetailActivity.class);
            intent.putExtra("complaint_id", komplain.getId());
            intent.putExtra("komplain_judul", komplain.getJudul());
            intent.putExtra("komplain_pelapor", komplain.getUser() != null ? komplain.getUser().getFull_name() : "-");
            intent.putExtra("komplain_status", komplain.getStatus());
            intent.putExtra("komplain_waktu", komplain.getTanggal());
            intent.putExtra("komplain_alamat", komplain.getAlamat());
            intent.putExtra("komplain_deskripsi", komplain.getDeskripsi());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return progressList != null ? progressList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvStatus, tvWaktu, tvAlamat, tvDeskripsi;
        Button btnCustomer, btnMintaBantuan, btnDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPelapor = itemView.findViewById(R.id.tvPelapor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            btnCustomer = itemView.findViewById(R.id.btnCustomer);
            btnMintaBantuan = itemView.findViewById(R.id.btnMintaBantuan);
            btnDetail = itemView.findViewById(R.id.btnDetail);
        }
    }
}
