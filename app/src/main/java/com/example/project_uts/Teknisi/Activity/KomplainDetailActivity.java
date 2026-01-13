package com.example.project_uts.Teknisi.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.HistoryTeknisiAdapter;
import com.example.project_uts.Teknisi.Model.HistoryTeknisi;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomplainDetailActivity extends AppCompatActivity {

    private Button btnPending, btnCompleted, btnSavePending, btnSaveCompleted, btnBack;
    private LinearLayout layoutPendingReason, layoutCompletedInfo;
    private EditText etAlasanPendingDetail, etTeknisiNote;
    private TextView tvJudulDetail, tvPelaporDetail, tvAlamatDetail, tvStatusDetail, tvWaktuDetail, tvDeskripsiDetail;
    private RecyclerView rvHistory;
    private HistoryTeknisiAdapter historyAdapter;
    private List<HistoryTeknisi> historyList = new ArrayList<>();

    private String komplainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain_detail_teknisi);

        // Bind views
        btnPending = findViewById(R.id.btnPending);
        btnCompleted = findViewById(R.id.btnCompleted);
        btnSavePending = findViewById(R.id.btnSavePending);
        btnSaveCompleted = findViewById(R.id.btnSaveCompleted);
        btnBack = findViewById(R.id.btnBackDetail);

        layoutPendingReason = findViewById(R.id.layoutPendingReason);
        layoutCompletedInfo = findViewById(R.id.layoutCompletedInfo);

        etAlasanPendingDetail = findViewById(R.id.etAlasanPendingDetail);
        etTeknisiNote = findViewById(R.id.etTeknisiNote);

        tvJudulDetail = findViewById(R.id.tvJudulDetail);
        tvPelaporDetail = findViewById(R.id.tvPelaporDetail);
        tvAlamatDetail = findViewById(R.id.tvAlamatDetail);
        tvStatusDetail = findViewById(R.id.tvStatusDetail);
        tvWaktuDetail = findViewById(R.id.tvWaktuDetail);
        tvDeskripsiDetail = findViewById(R.id.tvDeskripsiDetail);

        rvHistory = findViewById(R.id.rvHistory);

        // Setup RecyclerView
        historyAdapter = new HistoryTeknisiAdapter(historyList);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        // Ambil data dari Intent
        komplainId = getIntent().getStringExtra("complaint_id");
        String judul = getIntent().getStringExtra("komplain_judul");
        String pelapor = getIntent().getStringExtra("komplain_pelapor");
        String status = getIntent().getStringExtra("komplain_status");
        String waktuRaw = getIntent().getStringExtra("komplain_waktu");
        String alamat = getIntent().getStringExtra("komplain_alamat");
        String deskripsi = getIntent().getStringExtra("komplain_deskripsi");

        // Tampilkan data
        tvJudulDetail.setText(judul);
        tvPelaporDetail.setText("Oleh: " + pelapor);
        tvStatusDetail.setText(status);
        tvStatusDetail.setText(status);

        switch (status.toLowerCase()) {
            case "complaint":
                tvStatusDetail.setTextColor(Color.parseColor("#FF9800")); // oranye
                break;
            case "pending":
                tvStatusDetail.setTextColor(Color.parseColor("#F44336")); // merah
                break;
            case "progress":
                tvStatusDetail.setTextColor(Color.parseColor("#2196F3")); // biru
                break;
            case "completed":
                tvStatusDetail.setTextColor(Color.parseColor("#4CAF50")); // hijau
                break;
            default:
                tvStatusDetail.setTextColor(Color.parseColor("#9E9E9E")); // abu-abu default
                break;
        }

        tvAlamatDetail.setText(alamat);
        tvDeskripsiDetail.setText(deskripsi);
        if (waktuRaw != null) {
            try {
                // format input sesuai pola dari backend (ISO tanpa zona)
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                Date date = inputFormat.parse(waktuRaw);
                tvWaktuDetail.setText(outputFormat.format(date)); // hasil: 11/01/2026 23:10
            } catch (Exception e) {
                // kalau parsing gagal, tampilkan raw string
                tvWaktuDetail.setText(waktuRaw);
            }
        }


        // Tombol Pending
        btnPending.setOnClickListener(v -> {
            layoutPendingReason.setVisibility(android.view.View.VISIBLE);
            layoutCompletedInfo.setVisibility(android.view.View.GONE);
        });

        // Tombol Completed
        btnCompleted.setOnClickListener(v -> {
            layoutCompletedInfo.setVisibility(android.view.View.VISIBLE);
            layoutPendingReason.setVisibility(android.view.View.GONE);
        });

        // Tombol Back
        btnBack.setOnClickListener(v -> finish());

        // Simpan Pending
        btnSavePending.setOnClickListener(v -> {
            String alasan = etAlasanPendingDetail.getText().toString().trim();
            if (!alasan.isEmpty()) {
                updateStatusToServer("pending", alasan);
            } else {
                Toast.makeText(KomplainDetailActivity.this, "Harap isi alasan pending", Toast.LENGTH_SHORT).show();
            }
        });

        // Simpan Completed
        btnSaveCompleted.setOnClickListener(v -> {
            String catatanTeknisi = etTeknisiNote.getText().toString().trim();
            if (!catatanTeknisi.isEmpty()) {
                new androidx.appcompat.app.AlertDialog.Builder(KomplainDetailActivity.this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin menyelesaikan komplain ini?")
                        .setPositiveButton("OK", (dialog, which) -> {
                            updateStatusToServer("completed", catatanTeknisi);
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("status_updated", true);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // tetap di DetailActivity
                        })
                        .show();
            } else {
                Toast.makeText(KomplainDetailActivity.this, "Harap isi catatan teknisi", Toast.LENGTH_SHORT).show();
            }
        });

        if("completed".equalsIgnoreCase(status)){
            btnPending.setVisibility(View.GONE);
            btnCompleted.setVisibility(View.GONE);
        }

        // Load riwayat dari backend
        fetchComplaintHistory();
    }

    private void updateStatusToServer(String status, String note) {
        ApiService apiService = ApiClient.getApiService();
        Map<String, String> statusMap = new HashMap<>();
        statusMap.put("status", status);
        statusMap.put("alasan", note); // gunakan field alasan untuk semua catatan

        apiService.updateStatus(komplainId, statusMap).enqueue(new Callback<ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<ApiResponse<Complaint>> call, Response<ApiResponse<Complaint>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    fetchComplaintHistory();

                    tvStatusDetail.setText(status);
                    layoutPendingReason.setVisibility(android.view.View.GONE);
                    layoutCompletedInfo.setVisibility(android.view.View.GONE);
                    etAlasanPendingDetail.setText("");
                    etTeknisiNote.setText("");

                    if (status.equals("completed")) {
                        btnPending.setVisibility(android.view.View.GONE);
                        btnCompleted.setVisibility(android.view.View.GONE);
                    }

                    Toast.makeText(KomplainDetailActivity.this, "Status berhasil diupdate", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(KomplainDetailActivity.this, "Gagal update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Complaint>> call, Throwable t) {
                Toast.makeText(KomplainDetailActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchComplaintHistory() {
        ApiService apiService = ApiClient.getApiService();
        apiService.getComplaintStatuses(komplainId).enqueue(new Callback<ApiResponse<List<HistoryTeknisi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<HistoryTeknisi>>> call,
                                   Response<ApiResponse<List<HistoryTeknisi>>> response) {
                Log.d("HistoryAPI", "Response code: " + response.code());
                if (response.body() != null) {
                    Log.d("HistoryAPI", "Body: " + new Gson().toJson(response.body()));
                }

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    historyList.clear();
                    historyList.addAll(response.body().getData());
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<HistoryTeknisi>>> call, Throwable t) {
                Log.e("HistoryAPI", "Error: " + t.getMessage(), t);
                Toast.makeText(KomplainDetailActivity.this, "Gagal ambil riwayat: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
