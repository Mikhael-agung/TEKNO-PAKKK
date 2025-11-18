package com.example.project_uts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DiskusiTeknisiFragment extends Fragment {

    private RecyclerView rvDiskusi;
    private LinearLayout layoutEmpty;
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diskusi_teknisi, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        userRole = preferences.getString("role", "customer");

        initViews(view);
        setupDiskusiList();

        return view;
    }

    private void initViews(View view) {
        rvDiskusi = view.findViewById(R.id.rvDiskusi);
        layoutEmpty = view.findViewById(R.id.layoutEmptyDiskusi);
    }

    private void setupDiskusiList() {
        rvDiskusi.setLayoutManager(new LinearLayoutManager(getContext()));

        List<DiskusiTeknisi> diskusiList = getDiskusiData();

        if (diskusiList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvDiskusi.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvDiskusi.setVisibility(View.VISIBLE);

            DiskusiAdapter adapter = new DiskusiAdapter(diskusiList, new DiskusiAdapter.OnItemClickListener() {
                @Override
                public void onWhatsAppClick(DiskusiTeknisi diskusi) {
                    openWhatsAppToTechnician(diskusi);
                }
            });
            rvDiskusi.setAdapter(adapter);
        }
    }

    private List<DiskusiTeknisi> getDiskusiData() {
        List<DiskusiTeknisi> diskusiList = new ArrayList<>();

        // Hanya tampilkan untuk teknisi
        if (!"teknisi".equals(userRole)) {
            Toast.makeText(getContext(), "Hanya teknisi yang bisa melihat diskusi", Toast.LENGTH_SHORT).show();
            return diskusiList;
        }

        // Ambil data dari SharedPreferences
        SharedPreferences preferences = requireActivity().getSharedPreferences("diskusi_pref", Context.MODE_PRIVATE);
        String diskusiIds = preferences.getString("diskusi_ids", "");

        if (!diskusiIds.isEmpty()) {
            String[] ids = diskusiIds.split(",");

            for (String id : ids) {
                String diskusiData = preferences.getString(id, "");
                if (!diskusiData.isEmpty()) {
                    DiskusiTeknisi diskusi = parseDiskusiFromString(diskusiData);
                    if (diskusi != null) {
                        diskusiList.add(diskusi);
                    }
                }
            }
        }

        return diskusiList;
    }

    private DiskusiTeknisi parseDiskusiFromString(String diskusiData) {
        try {
            String[] parts = diskusiData.split("\\|");
            if (parts.length >= 9) {
                DiskusiTeknisi diskusi = new DiskusiTeknisi();
                diskusi.setId(parts[0]);
                diskusi.setKomplainId(parts[1]);
                diskusi.setJudulKomplain(parts[2]);
                diskusi.setPelapor(parts[3]);
                diskusi.setDeskripsiKomplain(parts[4]);
                diskusi.setTeknisiPeminta(parts[5]);
                diskusi.setNoTelpTeknisi(parts[6]);
                diskusi.setWaktu(parts[7]);
                diskusi.setStatus(parts[8]);
                return diskusi;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openWhatsAppToTechnician(DiskusiTeknisi diskusi) {
        try {
            String phoneNumber = diskusi.getNoTelpTeknisi();

            // Format pesan
            String message = "Halo " + diskusi.getTeknisiPeminta() + "! 👋\n\n" +
                    "Saya lihat kamu minta bantuan untuk komplain:\n\n" +
                    "📋 *" + diskusi.getJudulKomplain() + "*\n" +
                    "👤 Pelapor: " + diskusi.getPelapor() + "\n\n" +
                    "Ada yang bisa saya bantu?";

            // Format nomor (hapus karakter selain angka)
            String formattedNumber = phoneNumber.replaceAll("[^0-9]", "");

            // Buka WhatsApp
            String url = "https://wa.me/" + formattedNumber + "?text=" + Uri.encode(message);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(getContext(), "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show();
        }
    }
}