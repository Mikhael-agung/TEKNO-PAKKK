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

import com.example.project_uts.ApiService;
import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Model.ComplaintStatusRequest;
import com.example.project_uts.Teknisi.Model.Komplain;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomplainAdapter extends RecyclerView.Adapter<KomplainAdapter.ViewHolder> {

    private List<Komplain> komplainList;
    private Context context;
    private ApiService api; // Retrofit service

    public KomplainAdapter(Context context, List<Komplain> komplainList, ApiService api) {
        this.context = context;
        this.komplainList = komplainList;
        this.api = api;
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
        holder.tvPelapor.setText("Pelapor: " + komplain.getUserId());
        holder.tvStatus.setText(komplain.getStatus() != null ? komplain.getStatus() : "Belum ada status");
        holder.tvWaktu.setText(komplain.getCreatedAt());

        // 👉 Listener tombol Ambil
        holder.btnAmbil.setOnClickListener(v -> {
            ComplaintStatusRequest req = new ComplaintStatusRequest(
                    "On Progress", "tech_001", "Komplain sedang ditangani"
            );

            api.addComplaintStatus(komplain.getId(), req).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Komplain diambil!", Toast.LENGTH_SHORT).show();
                        // refresh list kalau perlu
                        // notifyDataSetChanged(); atau callback ke Fragment
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(context, "Gagal ambil komplain", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return komplainList != null ? komplainList.size() : 0;
    }

    // 🔎 ViewHolder dengan tombol Ambil
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvPelapor, tvStatus, tvWaktu;
        Button btnAmbil;

        public ViewHolder(View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvJudul);
            tvPelapor = itemView.findViewById(R.id.tvPelapor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvWaktu = itemView.findViewById(R.id.tvWaktu);
            btnAmbil = itemView.findViewById(R.id.btnAmbil); // 👉 ini tombol Ambil
        }
    }
}
