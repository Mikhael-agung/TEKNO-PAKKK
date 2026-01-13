package com.example.project_uts.Teknisi.Adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.project_uts.Teknisi.Activity.KomplainDetailActivity;
import com.example.project_uts.Teknisi.Model.Komplain;
import com.example.project_uts.Teknisi.Utils.WhatsAppHelper;

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

        // Set data ke view
        holder.tvJudul.setText(komplain.getJudul());

        // Pelapor - dengan phone check
        String pelaporText = "Pelapor: ";
        if (komplain.getUser() != null) {
            String name = komplain.getUser().getFull_name();
            if (name != null && !name.isEmpty()) {
                pelaporText += name;
            } else {
                pelaporText += "User-" + komplain.getUserId();
            }
        } else {
            pelaporText += "User-" + komplain.getUserId();
        }
        holder.tvPelapor.setText(pelaporText);

        holder.tvWaktu.setText(komplain.getCreatedAt());

        // Status handling
        String statusText = komplain.getStatus();
        holder.tvStatus.setText(statusText);

        if ("on_progress".equalsIgnoreCase(statusText)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#2196F3"));
        } else if ("pending".equalsIgnoreCase(statusText)) {
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FF9800"));
        }

        // ===== TOMBOL CUSTOMER =====
        holder.btnCustomer.setOnClickListener(v -> {
            Komplain.User user = komplain.getUser();
            String customerPhone = null;

            // CEK APAKAH ADA USER DAN PHONE
            if (user != null && user.getPhone() != null && !user.getPhone().isEmpty()) {
                customerPhone = user.getPhone();
            }

            if (customerPhone != null) {
                // Format nomor (pastikan +62 atau 0 di-handle)
                String formattedPhone = formatPhoneNumber(customerPhone);

                WhatsAppHelper.contactCustomer(
                        context,
                        formattedPhone,
                        komplain.getId(),
                        komplain.getJudul()
                );
            } else {
                Toast.makeText(context,
                        "Nomor telepon customer tidak tersedia",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ===== TOMBOL BANTUAN =====
        holder.btnMintaBantuan.setOnClickListener(v -> {
            String pelaporName = "Unknown";
            if (komplain.getUser() != null && komplain.getUser().getFull_name() != null) {
                pelaporName = komplain.getUser().getFull_name();
            }

            WhatsAppHelper.requestHelpToTeknisi(
                    context,
                    komplain.getId(),
                    komplain.getJudul(),
                    pelaporName,
                    komplain.getStatus(),
                    komplain.getDeskripsi()
            );
        });

        // ===== TOMBOL DETAIL =====
        holder.btnDetail.setOnClickListener(v -> {
            String pelaporName = "Unknown";
            if (komplain.getUser() != null && komplain.getUser().getFull_name() != null) {
                pelaporName = komplain.getUser().getFull_name();
            }

            Intent intent = new Intent(context, KomplainDetailActivity.class);
            intent.putExtra("komplain_id", komplain.getId());
            intent.putExtra("komplain_judul", komplain.getJudul());
            intent.putExtra("komplain_pelapor", pelaporName);
            intent.putExtra("komplain_status", komplain.getStatus());
            intent.putExtra("komplain_waktu", komplain.getCreatedAt());
            intent.putExtra("komplain_deskripsi", komplain.getDeskripsi());

            context.startActivity(intent);
        });
    }

    // Helper method untuk format nomor telepon
    private String formatPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return phone;
        }

        // Hilangkan semua karakter non-digit
        String digitsOnly = phone.replaceAll("[^0-9]", "");

        // Jika diawali 0, ganti dengan +62
        if (digitsOnly.startsWith("0")) {
            return "62" + digitsOnly.substring(1);
        }

        // Jika diawali 62, tetap
        if (digitsOnly.startsWith("62")) {
            return digitsOnly;
        }

        // Jika diawali 8 (tanpa 0), tambah 62
        if (digitsOnly.startsWith("8")) {
            return "62" + digitsOnly;
        }

        // Default return as-is
        return digitsOnly;
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