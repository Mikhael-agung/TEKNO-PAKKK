package com.example.project_uts.Teknisi.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Model.DiskusiTeknisi;

import java.util.List;

public class DiskusiAdapter extends RecyclerView.Adapter<DiskusiAdapter.ViewHolder> {

    private List<DiskusiTeknisi> diskusiList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onBantuClick(DiskusiTeknisi diskusi);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvTeknisiPeminta, tvWaktu, tvPelapor;
        Button btnBantu;

        public ViewHolder(View view) {
            super(view);
            tvJudul = view.findViewById(R.id.tvJudulDiskusi);
            tvTeknisiPeminta = view.findViewById(R.id.tvTeknisiPeminta);
            tvWaktu = view.findViewById(R.id.tvWaktuDiskusi);
            tvPelapor = view.findViewById(R.id.tvPelaporDiskusi);
            btnBantu = view.findViewById(R.id.btnBantuDiskusi);
        }
    }

    public DiskusiAdapter(List<DiskusiTeknisi> diskusiList, OnItemClickListener listener) {
        this.diskusiList = diskusiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_diskusi_teknisi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DiskusiTeknisi diskusi = diskusiList.get(position);

        holder.tvJudul.setText(diskusi.getJudulKomplain());
        holder.tvTeknisiPeminta.setText("Diminta oleh: " + diskusi.getTeknisiPeminta());
        holder.tvPelapor.setText("Pelapor: " + diskusi.getPelapor());
        holder.tvWaktu.setText(diskusi.getWaktu());

        holder.btnBantu.setOnClickListener(v -> {
            listener.onBantuClick(diskusi);
        });

        holder.itemView.setOnClickListener(v -> {
            // TODO: bisa tambahin detail kalau mau
        });
    }

    @Override
    public int getItemCount() {
        return diskusiList.size();
    }
}
