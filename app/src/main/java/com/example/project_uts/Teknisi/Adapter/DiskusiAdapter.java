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

import java.util.ArrayList;
import java.util.List;

public class DiskusiAdapter extends RecyclerView.Adapter<DiskusiAdapter.ViewHolder> {

    private List<DiskusiTeknisi> diskusiList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onBantuClick(DiskusiTeknisi diskusi);
    }

    public DiskusiAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<DiskusiTeknisi> newData) {
        this.diskusiList.clear();
        if (newData != null) {
            this.diskusiList.addAll(newData);
        }
        notifyDataSetChanged();
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

        // Ambil data dari nested object
        if (diskusi.getComplaint() != null) {
            holder.tvJudul.setText(diskusi.getComplaint().getJudul());
            if (diskusi.getComplaint().getUser() != null) {
                holder.tvPelapor.setText("Pelapor: " + diskusi.getComplaint().getUser().getFull_name());
            }
        }

        if (diskusi.getTechnician() != null) {
            holder.tvTeknisiPeminta.setText("Diminta oleh: " + diskusi.getTechnician().getFull_name());
        }

        holder.tvWaktu.setText(diskusi.getCreated_at());

        holder.btnBantu.setOnClickListener(v -> {
            if (listener != null) listener.onBantuClick(diskusi);
        });
    }

    @Override
    public int getItemCount() {
        return diskusiList.size();
    }
}
