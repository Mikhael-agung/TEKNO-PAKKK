package com.example.project_uts.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_uts.adapter.HistoryAdapter;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.R;
import com.google.android.material.button.MaterialButton;
import android.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class HistoryComplainFragment extends Fragment {

    private RecyclerView rvHistoryComplaints;
    private LinearLayout emptyState;
    private HistoryAdapter adapter;
    private List<Complaint> complaints = new ArrayList<>();
    private List<Complaint> allComplaints = new ArrayList<>();

    private MaterialButton btnSemua, btnAktif, btnSelesai;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_complain, container, false);

        initViews(view);
        setupRecyclerView();
        setupFilterButtons();
        loadDummyData();

        return view;
    }

    private void initViews(View view) {
        rvHistoryComplaints = view.findViewById(R.id.rv_history_complaints);
        emptyState = view.findViewById(R.id.empty_state);
        btnSemua = view.findViewById(R.id.btn_semua);
        btnAktif = view.findViewById(R.id.btn_aktif);
        btnSelesai = view.findViewById(R.id.btn_selesai);
    }

    private void setupRecyclerView() {
        try {
            adapter = new HistoryAdapter(complaints, this::showComplaintDetail);
            rvHistoryComplaints.setLayoutManager(new LinearLayoutManager(getContext()));
            rvHistoryComplaints.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
            showEmptyState();
        }
    }

    private void setupFilterButtons() {
        btnSemua.setOnClickListener(v -> filterWithButton("semua", btnSemua));
        btnAktif.setOnClickListener(v -> filterWithButton("aktif", btnAktif));
        btnSelesai.setOnClickListener(v -> filterWithButton("selesai", btnSelesai));
    }

    private void filterWithButton(String filterType, MaterialButton button) {
        filterComplaints(filterType);
        updateButtonStates(button);
    }

    private void filterComplaints(String filter) {
        complaints.clear();

        switch (filter) {
            case "semua":
                complaints.addAll(allComplaints);
                break;
            case "aktif":
                for (Complaint complaint : allComplaints) {
                    if (complaint.getStatus().equals("dalam proses") ||
                            complaint.getStatus().equals("pending")) {
                        complaints.add(complaint);
                    }
                }
                break;
            case "selesai":
                for (Complaint complaint : allComplaints) {
                    if (complaint.getStatus().equals("selesai") ||
                            complaint.getStatus().equals("ditolak")) {
                        complaints.add(complaint);
                    }
                }
                break;
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateUI();
    }

    private void updateButtonStates(MaterialButton activeButton) {
        // Reset semua button
        MaterialButton[] buttons = {btnSemua, btnAktif, btnSelesai};
        for (MaterialButton button : buttons) {
            button.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            button.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }

        // Set active button
        activeButton.setBackgroundColor(getResources().getColor(R.color.primary_color));
        activeButton.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void loadDummyData() {
        try {
            allComplaints.clear();

            // Data dummy dengan berbagai status dan deskripsi detail
            allComplaints.add(new Complaint("COMP001", "TV LED Tidak Bisa Menyala", "Elektronik", "15 Jan 2024", "selesai",
                    "TV Samsung 32 inch tiba-tiba tidak bisa menyala. Lampu indicator tidak menyala sama sekali saat dicolokkan."));

            allComplaints.add(new Complaint("COMP002", "AC Split Bocor Air", "AC & Pendingin", "14 Jan 2024", "dalam proses",
                    "AC Sharp 1 PK mengeluarkan air dari unit indoor. Air menetes terus menerus dan membuat lantai basah."));

            allComplaints.add(new Complaint("COMP003", "Kulkas 2 Pintu Tidak Dingin", "Kulkas", "10 Jan 2024", "ditolak",
                    "Kulkas Polytron 2 pintu bagian freezer masih dingin tetapi bagian chiller tidak dingin sama sekali."));

            allComplaints.add(new Complaint("COMP004", "Mesin Cuci Berisik Saat Berputar", "Mesin Cuci", "08 Jan 2024", "selesai",
                    "Mesin cuci LG front loading mengeluarkan suara berisik seperti logam bergesekan saat proses spin."));

            allComplaints.add(new Complaint("COMP005", "Pipa Air Kamar Mandi Mampet", "Perbaikan Pipa", "05 Jan 2024", "dalam proses",
                    "Pipa pembuangan wastafel kamar mandi tersumbat total. Air tidak bisa turun dan menggenang di bak."));

            allComplaints.add(new Complaint("COMP006", "Instalasi Stop Kontak Baru", "Listrik", "03 Jan 2024", "pending",
                    "Perlu instalasi stop kontak tambahan di ruang tamu untuk TV dan perangkat elektronik."));

            // Default show semua
            filterComplaints("semua");
            updateButtonStates(btnSemua);

        } catch (Exception e) {
            e.printStackTrace();
            showEmptyState();
        }
    }

    /**
     * Tampilkan detail komplain dalam dialog
     */
    private void showComplaintDetail(Complaint complaint) {
        AlertDialog dialog = createDetailDialog(complaint);
        dialog.show();
    }

    /**
     * Method yang diekstrak untuk membuat AlertDialog.Builder
     */
    private AlertDialog createDetailDialog(Complaint complaint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("📋 Detail Komplain");

        String detailMessage = createDetailMessage(complaint);
        builder.setMessage(detailMessage);

        setupDialogButtons(builder, complaint);

        return builder.create();
    }

    /**
     * Method untuk membuat pesan detail
     */
    private String createDetailMessage(Complaint complaint) {
        return "🆔 ID Komplain: " + complaint.getId() + "\n\n" +
                "📝 Judul: " + complaint.getJudul() + "\n\n" +
                "📂 Kategori: " + complaint.getKategori() + "\n\n" +
                "📅 Tanggal: " + complaint.getTanggal() + "\n\n" +
                "📊 Status: " + complaint.getStatus() + "\n\n" +
                "📋 Deskripsi Masalah:\n" + complaint.getDeskripsi();
    }

    /**
     * Method untuk setup button dialog
     */
    private void setupDialogButtons(AlertDialog.Builder builder, Complaint complaint) {
        builder.setPositiveButton("❌ Tutup", (dialog, which) -> dialog.dismiss());

        // Hanya tampilkan chat untuk status aktif
        if (isActiveComplaint(complaint)) {
            builder.setNegativeButton("💬 Chat Teknisi", (dialog, which) -> {
                dialog.dismiss();
                openWhatsApp(complaint);
            });
        }
    }

    /**
     * Method untuk cek apakah komplain masih aktif
     */
    private boolean isActiveComplaint(Complaint complaint) {
        return complaint.getStatus().equals("dalam proses") ||
                complaint.getStatus().equals("pending");
    }

    /**
     * Buka WhatsApp untuk chat teknisi
     */
    private void openWhatsApp(Complaint complaint) {
        try {
            String message = "Halo Teknisi, saya ingin bertanya tentang komplain:\n" +
                    "ID: " + complaint.getId() + "\n" +
                    "Judul: " + complaint.getJudul() + "\n" +
                    "Status: " + complaint.getStatus();

            String encodedMessage = Uri.encode(message);
            String whatsappUrl = "https://api.whatsapp.com/send?phone=6281234567890&text=" + encodedMessage;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl));
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error membuka WhatsApp", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateUI() {
        if (emptyState != null && rvHistoryComplaints != null) {
            boolean isEmpty = complaints.isEmpty();
            emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            rvHistoryComplaints.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        }
    }

    private void showEmptyState() {
        if (emptyState != null) emptyState.setVisibility(View.VISIBLE);
        if (rvHistoryComplaints != null) rvHistoryComplaints.setVisibility(View.GONE);
    }
}