package com.example.project_uts.Teknisi.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
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
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomplainAdapter extends RecyclerView.Adapter<KomplainAdapter.ViewHolder> {

    private List<Komplain> komplainList;
    private Context context;
    private ApiService apiService;
    private String namaTeknisi;

    // Constructor
    public KomplainAdapter(Context context, List<Komplain> komplainList, String namaTeknisi) {
        this.context = context;
        this.komplainList = komplainList;
        this.apiService = ApiClient.getApiService(); // API teknisi
        this.namaTeknisi = namaTeknisi;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_komplain_teknisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Komplain komplain = komplainList.get(position);
        Log.d("Adapter", "Bind item: " + komplain.getJudul() + " | ID: " + komplain.getId());

        // Judul
        holder.tvJudul.setText(komplain.getJudul());

        // Pelapor
        if (komplain.getUser() != null && komplain.getUser().getFull_name() != null) {
            holder.tvPelapor.setText("Pelapor: " + komplain.getUser().getFull_name());
        } else {
            holder.tvPelapor.setText("Pelapor: -");
        }

        // Alamat (gabungan kota + alamat)
        String alamatText = "-";
        if (komplain.getKota() != null || komplain.getAlamat() != null) {
            alamatText = (komplain.getKota() != null ? komplain.getKota() : "")
                    + (komplain.getAlamat() != null ? ", " + komplain.getAlamat() : "");
        }
        holder.tvAlamat.setText("Alamat: " + alamatText);

        // Deskripsi (kalau ada field description di model)
        holder.tvDeskripsi.setText(komplain.getDeskripsi() != null ? komplain.getDeskripsi() : "-");

        // Status
        holder.tvStatus.setText(komplain.getStatus() != null ? komplain.getStatus() : "Komplain");

        // Waktu
        holder.tvWaktu.setText(komplain.getTanggal() != null ? komplain.getTanggal() : "-");

        // Tombol Ambil
        holder.btnAmbil.setOnClickListener(v -> {
            String complaintId = komplain.getId();
            if (complaintId == null || complaintId.isEmpty()) {
                Toast.makeText(context, "ID komplain tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            apiService.takeComplaint(complaintId).enqueue(new Callback<ApiResponse<Komplain>>() {
                @Override
                public void onResponse(Call<ApiResponse<Komplain>> call,
                                       Response<ApiResponse<Komplain>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(context, "Komplain berhasil diambil!", Toast.LENGTH_SHORT).show();

                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            komplainList.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    } else {
                        Toast.makeText(context, "Gagal: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Komplain>> call, Throwable t) {
                    Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Tombol Chat Customer
        holder.btnChat.setOnClickListener(v -> {
            if (komplain.getUser() != null && komplain.getUser().getPhone() != null) {
                String phoneNumber = komplain.getUser().getPhone();
                String idKomplain = komplain.getId();
                String judulKomplain = komplain.getJudul();

                String pesan = "Halo, perkenalkan saya " + namaTeknisi + "\n"
                        + "Saya teknisi yang akan menangani komplain Anda.\n"
                        + "ID Komplain: " + idKomplain + "\n"
                        + "Judul: " + judulKomplain + "\n\n"
                        + "Mohon konfirmasi jika informasi ini sudah sesuai.\n"
                        + "Terima kasih 🙏";

                String url = "https://wa.me/" + phoneNumber + "?text=" + Uri.encode(pesan);


                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Nomor customer tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return komplainList != null ? komplainList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvAlamat, tvDeskripsi, tvStatus, tvWaktu;
        Button btnAmbil, btnChat;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPelapor = itemView.findViewById(R.id.tvPelapor);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            tvDeskripsi = itemView.findViewById(R.id.tvDeskripsi);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            btnAmbil = itemView.findViewById(R.id.btnAmbil);
            btnChat = itemView.findViewById(R.id.btnChat);
        }
    }
}
