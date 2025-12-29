package com.example.project_uts.Teknisi.Adapter;

import android.content.Context;
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

    // UPDATE CONSTRUCTOR
    public KomplainAdapter(Context context, List<Komplain> komplainList) {
        this.context = context;
        this.komplainList = komplainList;
        this.apiService = ApiClient.getApiService(); // PAKAI API TEKNISI
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

        holder.tvJudul.setText(komplain.getTitle());
        // UPDATE: Tampilkan nama customer jika ada
        if (komplain.getUser() != null && komplain.getUser().getFull_name() != null) {
            holder.tvPelapor.setText("Pelapor: " + komplain.getUser().getFull_name());
        } else {
            holder.tvPelapor.setText("Pelapor: " + komplain.getUserId());
        }
        holder.tvStatus.setText(komplain.getStatus() != null ? komplain.getStatus() : "Komplain");
        holder.tvWaktu.setText(komplain.getCreatedAt() != null ? komplain.getCreatedAt() : "-");

        // UPDATE: Tombol Ambil - gunakan endpoint teknisi
        holder.btnAmbil.setOnClickListener(v -> {
            String complaintId = komplain.getId();
            if (complaintId == null || complaintId.isEmpty()) {
                Toast.makeText(context, "ID komplain tidak valid", Toast.LENGTH_SHORT).show();
                return;
            }

            // PAKAI ENDPOINT TEKNISI: /api/teknisi/complaints/{id}/take
            Call<com.example.project_uts.models.ApiResponse<Komplain>> call =
                    apiService.takeComplaint(complaintId);

            call.enqueue(new Callback<com.example.project_uts.models.ApiResponse<Komplain>>() {
                @Override
                public void onResponse(Call<com.example.project_uts.models.ApiResponse<Komplain>> call,
                                       Response<com.example.project_uts.models.ApiResponse<Komplain>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().isSuccess()) {
                            Toast.makeText(context, "Komplain berhasil diambil!", Toast.LENGTH_SHORT).show();
                            // Hapus item dari list lokal
                            komplainList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, komplainList.size());
                        } else {
                            Toast.makeText(context,
                                    "Gagal: " + response.body().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context,
                                "Error response: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<com.example.project_uts.models.ApiResponse<Komplain>> call, Throwable t) {
                    Toast.makeText(context,
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return komplainList != null ? komplainList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvStatus, tvWaktu;
        Button btnAmbil;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPelapor = itemView.findViewById(R.id.tvPelapor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            btnAmbil = itemView.findViewById(R.id.btnAmbil);
        }
    }
}