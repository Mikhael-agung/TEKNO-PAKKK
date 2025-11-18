package com.example.project_uts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class KomplainDetailActivity extends AppCompatActivity {

    private static final String TAG = "KomplainDetail";

    private Komplain komplain;
    private TextView tvJudul, tvPelapor, tvStatus, tvDeskripsi, tvWaktu;
    private ImageView ivFotoBarang;
    private LinearLayout layoutActionButtons;
    private Button btnProgress, btnPending, btnComplete, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_komplai_detail);
            Log.d(TAG, "Layout loaded successfully");

            initViews();
            loadKomplainData();
            setupClickListeners();

            Log.d(TAG, "Activity created successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            // Text Views
            tvJudul = findViewById(R.id.tvJudul);
            tvPelapor = findViewById(R.id.tvPelapor);
            tvStatus = findViewById(R.id.tvStatus);
            tvDeskripsi = findViewById(R.id.tvDeskripsi);
            tvWaktu = findViewById(R.id.tvWaktu);
            Log.d(TAG, "TextViews initialized");

            // Image View
            ivFotoBarang = findViewById(R.id.ivFotoBarang);
            Log.d(TAG, "ImageView initialized");

            // Layouts
            layoutActionButtons = findViewById(R.id.layoutActionButtons);
            Log.d(TAG, "Layouts initialized");

            // Buttons
            btnProgress = findViewById(R.id.btnProgress);
            btnPending = findViewById(R.id.btnPending);
            btnComplete = findViewById(R.id.btnComplete);
            btnBack = findViewById(R.id.btnBack);
            Log.d(TAG, "Buttons initialized");

        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage(), e);
            throw e; // Re-throw biar ketahuan di onCreate
        }
    }

    private void loadKomplainData() {
        try {
            // Ambil data dari intent
            String id = getIntent().getStringExtra("komplain_id");
            String judul = getIntent().getStringExtra("komplain_judul");
            String pelapor = getIntent().getStringExtra("komplain_pelapor");
            String status = getIntent().getStringExtra("komplain_status");
            String deskripsi = getIntent().getStringExtra("komplain_deskripsi");
            String waktu = getIntent().getStringExtra("komplain_waktu");
            String phoneNumber = getIntent().getStringExtra("komplain_phone");

            Log.d(TAG, "Data from intent - Judul: " + judul + ", Status: " + status);

            // Buat object komplain
            komplain = new Komplain(id, judul, pelapor, status, waktu,
                    "", deskripsi, phoneNumber, "", "", "");

            // Set data ke view
            tvJudul.setText(judul != null ? judul : "Judul tidak tersedia");
            tvPelapor.setText("Pelapor: " + (pelapor != null ? pelapor : "Tidak diketahui"));
            tvStatus.setText(status != null ? status : "Tidak diketahui");
            tvDeskripsi.setText(deskripsi != null ? deskripsi : "Deskripsi tidak tersedia");
            tvWaktu.setText("Waktu: " + (waktu != null ? waktu : "Tidak diketahui"));

            // Set status color
            setStatusColor(status);

            Log.d(TAG, "Data loaded to views successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in loadKomplainData: " + e.getMessage(), e);
            Toast.makeText(this, "Error loading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners() {
        try {
            // Tombol Tandai Dalam Proses
            btnProgress.setOnClickListener(v -> {
                updateStatus("Dalam Proses");
            });

            // Tombol Tandai Pending
            btnPending.setOnClickListener(v -> {
                updateStatus("Pending");
            });

            // Tombol Tandai Selesai
            btnComplete.setOnClickListener(v -> {
                updateStatus("Selesai");
            });

            btnBack.setOnClickListener(v -> {
                finish();
            });

            Log.d(TAG, "Click listeners setup successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error in setupClickListeners: " + e.getMessage(), e);
        }
    }

    private void updateStatus(String status) {
        try {
            // Simpan ke SharedPreferences
            SharedPreferences preferences = getSharedPreferences("komplain_pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(komplain.getId() + "_status", status);
            editor.apply();

            Toast.makeText(this, "Status berhasil diupdate: " + status, Toast.LENGTH_SHORT).show();

            // Kembali ke previous activity
            finish();

        } catch (Exception e) {
            Log.e(TAG, "Error in updateStatus: " + e.getMessage(), e);
        }
    }

    private void setStatusColor(String status) {
        try {
            int color;

            switch (status) {
                case "Selesai":
                    color = Color.parseColor("#4CAF50"); // Hijau
                    break;
                case "Dalam Proses":
                    color = Color.parseColor("#2196F3"); // Biru
                    break;
                case "Pending":
                    color = Color.parseColor("#FF9800"); // Orange
                    break;
                default:
                    color = Color.parseColor("#666666"); // Abu-abu
                    break;
            }
            tvStatus.setTextColor(color);

        } catch (Exception e) {
            Log.e(TAG, "Error in setStatusColor: " + e.getMessage(), e);
        }
    }

    private void openWhatsApp() {
        try {
            String phoneNumber = komplain.getPhoneNumber();
            String message = "Halo " + komplain.getPelapor() + "! \n\n" +
                    "Saya dari TeknoServe. " +
                    "Saya menangani komplain Anda: *" + komplain.getJudul() + "*\n\n" +
                    "Status: " + komplain.getStatus();

            String formattedNumber = phoneNumber.replaceAll("[^0-9]", "");
            String url = "https://wa.me/" + formattedNumber + "?text=" + Uri.encode(message);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Error in openWhatsApp: " + e.getMessage(), e);
            Toast.makeText(this, "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show();
        }
    }
}