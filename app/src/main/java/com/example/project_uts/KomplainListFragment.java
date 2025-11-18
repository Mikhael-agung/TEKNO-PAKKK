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
import java.util.Locale;
import java.util.Date;

import java.text.SimpleDateFormat;

public class KomplainListFragment extends Fragment {

    private RecyclerView rvKomplain;
    private KomplainAdapter adapter;
    private LinearLayout layoutEmpty; // GUNAKAN LinearLayout
    private String userRole;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_komplain_list, container, false);

        // Ambil role user
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        userRole = preferences.getString("role", "customer");

        initViews(view);
        setupKomplainList();

        return view;
    }

    private void initViews(View view) {
        rvKomplain = view.findViewById(R.id.rvKomplain);
        layoutEmpty = view.findViewById(R.id.layoutEmpty); // PASTIKAN ID INI ADA DI XML
    }

    private void setupKomplainList() {
        rvKomplain.setLayoutManager(new LinearLayoutManager(getContext()));

        // Dummy data komplain
        List<Komplain> komplainList = getDummyKomplainData();

        // Tampilkan empty state jika tidak ada data
        if (komplainList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE); // GUNAKAN layoutEmpty
            rvKomplain.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE); // GUNAKAN layoutEmpty
            rvKomplain.setVisibility(View.VISIBLE);

            adapter = new KomplainAdapter(komplainList, new KomplainAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Komplain komplain) {
                    // Buka detail komplain
                    openKomplainDetail(komplain);
                }

                @Override
                public void onWhatsAppClick(Komplain komplain) {
                    // Buka WhatsApp
                    openWhatsApp(komplain);
                }

                @Override
                public void onMintaBantuanClick(Komplain komplain) {
                    // Post minta bantuan ke chat
                    postMintaBantuan(komplain);
                }
            });
            rvKomplain.setAdapter(adapter);
        }
    }

    // ... METHOD LAINNYA TETAP SAMA
    private List<Komplain> getDummyKomplainData() {
        List<Komplain> komplainList = new ArrayList<>();

        if ("teknisi".equals(userRole)) {
            // Data untuk TEKNISI (bisa lihat semua komplain)
            komplainList.add(new Komplain("K001", "AC Tidak Dingin", "Budi Santoso", "Dalam Proses", "2 jam lalu",
                    "ac_rusak", "AC tidak mengeluarkan udara dingin sama sekali. Sudah dicoba setting suhu terendah tetap tidak berfungsi.", "081234567890",
                    "Sudah dilakukan pengecekan freon dan pembersihan filter", "", ""));

            komplainList.add(new Komplain("K002", "Kebocoran Pipa", "Sari Handayani", "Pending", "1 hari lalu",
                    "pipa_bocor", "Pipa air di bawah wastafel kamar mandi bocor dan menyebabkan genangan air. Kebocoran cukup deras.", "081298765432",
                    "", "", "Menunggu sparepart pipa PVC 1/2 inch"));

            komplainList.add(new Komplain("K003", "Listrik Mati", "John Doe", "Selesai", "3 hari lalu",
                    "listrik_mati", "Listrik di rumah mati total. MCB sering turun sendiri ketika menyalakan peralatan elektronik.", "081311223344",
                    "MCB sudah diganti dan kabel diperbaiki. Sistem listrik sudah normal kembali.", "foto_perbaikan_listrik", ""));

        } else {
            // Data untuk CUSTOMER (hanya komplain milik sendiri)
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
            String currentUser = preferences.getString("nama", "Customer");

            komplainList.add(new Komplain("K001", "AC Tidak Dingin", currentUser, "Dalam Proses", "2 jam lalu",
                    "ac_rusak", "AC tidak mengeluarkan udara dingin sama sekali. Mohon segera diperbaiki.", "081234567890",
                    "Teknisi sudah mengecek dan sedang dalam proses perbaikan", "", ""));

            komplainList.add(new Komplain("K004", "Kulkas Tidak Dingin", currentUser, "Pending", "1 hari lalu",
                    "kulkas_rusak", "Kulkas tidak dingin, makanan jadi cepat basi. Tolong diperbaiki segera.", "081234567890",
                    "", "", "Menunggu sparepart compressor"));
        }

        return komplainList;
    }

    private void openKomplainDetail(Komplain komplain) {
        Intent intent = new Intent(getActivity(), KomplainDetailActivity.class);
        intent.putExtra("komplain_id", komplain.getId());
        intent.putExtra("komplain_judul", komplain.getJudul());
        intent.putExtra("komplain_pelapor", komplain.getPelapor());
        intent.putExtra("komplain_status", komplain.getStatus());
        intent.putExtra("komplain_deskripsi", komplain.getDeskripsi());
        intent.putExtra("komplain_waktu", komplain.getWaktu());
        intent.putExtra("komplain_phone", komplain.getPhoneNumber());
        intent.putExtra("komplain_teknisi_note", komplain.getTeknisiNote());
        intent.putExtra("komplain_alasan_pending", komplain.getAlasanPending());
        startActivity(intent);
    }

    private void openWhatsApp(Komplain komplain) {
        try {
            String phoneNumber = komplain.getPhoneNumber();
            String message;

            if ("teknisi".equals(userRole)) {
                // Pesan dari TEKNISI ke CUSTOMER
                message = "Halo " + komplain.getPelapor() + "! 👋\n\n" +
                        "Saya teknisi dari *TeknoServe*. " +
                        "Saya menangani komplain Anda tentang: *" + komplain.getJudul() + "*\n\n" +
                        "Bisakah kita berdiskusi lebih lanjut mengenai perbaikannya?";
            } else {
                // Pesan dari CUSTOMER ke TEKNISI
                message = "Halo! Saya *" + komplain.getPelapor() + "*. \n\n" +
                        "Saya ingin bertanya tentang progress komplain: *" + komplain.getJudul() + "*\n" +
                        "(ID Komplain: " + komplain.getId() + ")";
            }

            String formattedNumber = phoneNumber.replaceAll("[^0-9]", "");
            String url = "https://wa.me/" + formattedNumber + "?text=" + Uri.encode(message);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(getContext(), "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show();
        }
    }

    // Di KomplainListFragment.java - GANTI method postMintaBantuan
    private void postMintaBantuan(Komplain komplain) {
        // Hanya untuk TEKNISI
        if (!"teknisi".equals(userRole)) {
            Toast.makeText(getContext(), "Fitur ini hanya untuk teknisi", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil data teknisi yang login
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        String namaTeknisi = preferences.getString("nama", "Teknisi");
        String noTelpTeknisi = preferences.getString("no_telp", "082222222222"); // AMBIL NO TELEPON

        // Simpan ke SharedPreferences
        saveDiskusiToSharedPref(komplain, namaTeknisi, noTelpTeknisi);

        Toast.makeText(getContext(), "✅ Permintaan bantuan telah diposting ke diskusi teknisi", Toast.LENGTH_SHORT).show();
    }

    private void saveDiskusiToSharedPref(Komplain komplain, String namaTeknisi, String noTelpTeknisi) {
        SharedPreferences preferences = requireActivity().getSharedPreferences("diskusi_pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Generate unique ID
        String diskusiId = "D" + System.currentTimeMillis();

        // Format: ID|komplainId|judul|pelapor|deskripsi|teknisiPeminta|noTelpTeknisi|waktu|status
        String waktu = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        String diskusiData = diskusiId + "|" +
                komplain.getId() + "|" +
                komplain.getJudul() + "|" +
                komplain.getPelapor() + "|" +
                komplain.getDeskripsi() + "|" +
                namaTeknisi + "|" +
                noTelpTeknisi + "|" + // SIMPAN NO TELEPON
                waktu + "|" +
                "pending";

        editor.putString(diskusiId, diskusiData);

        // Simpan list ID diskusi
        String existingIds = preferences.getString("diskusi_ids", "");
        if (existingIds.isEmpty()) {
            existingIds = diskusiId;
        } else {
            existingIds = diskusiId + "," + existingIds;
        }
        editor.putString("diskusi_ids", existingIds);

        editor.apply();
    }

}