package com.example.project_uts;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomplainDetailActivity extends AppCompatActivity {

    private Button btnPending, btnCompleted, btnSavePending, btnSaveCompleted, btnBack;
    private LinearLayout layoutPendingReason, layoutCompletedInfo;
    private EditText etAlasanPendingDetail, etTeknisiNote;
    private TextView tvJudulDetail, tvPelaporDetail, tvStatusDetail, tvWaktuDetail, tvDeskripsiDetail;
    private ImageView ivFotoBarang;
    private RecyclerView rvHistory;
    private HistoryTeknisiAdapter historyAdapter;
    private List<HistoryTeknisi> historyList = new ArrayList<>();

    private String komplainId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_komplain_detail); // pakai layout XML kamu

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
        tvStatusDetail = findViewById(R.id.tvStatusDetail);
        tvWaktuDetail = findViewById(R.id.tvWaktuDetail);
        tvDeskripsiDetail = findViewById(R.id.tvDeskripsiDetail);
        ivFotoBarang = findViewById(R.id.ivFotoBarang);
        rvHistory = findViewById(R.id.rvHistory);

        // Setup RecyclerView
        historyAdapter = new HistoryTeknisiAdapter(historyList);
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setAdapter(historyAdapter);

        // Ambil data dari Intent
        komplainId = getIntent().getStringExtra("komplain_id");
        String judul = getIntent().getStringExtra("komplain_judul");
        String pelapor = getIntent().getStringExtra("komplain_pelapor");
        String status = getIntent().getStringExtra("komplain_status");
        String waktu = getIntent().getStringExtra("komplain_waktu");
        String deskripsi = getIntent().getStringExtra("komplain_deskripsi");
        String catatan = getIntent().getStringExtra("komplain_catatan");

        // Tampilkan data
        tvJudulDetail.setText(judul);
        tvPelaporDetail.setText("Oleh: " + pelapor);
        tvStatusDetail.setText(status);
        tvWaktuDetail.setText(waktu);
        tvDeskripsiDetail.setText(deskripsi);
        // kalau ada foto dari API, bisa load pakai Glide/Picasso ke ivFotoBarang

        // Tombol Pending
        btnPending.setOnClickListener(v -> {
            layoutPendingReason.setVisibility(View.VISIBLE);
            layoutCompletedInfo.setVisibility(View.GONE);
        });

        // Tombol Completed
        btnCompleted.setOnClickListener(v -> {
            layoutCompletedInfo.setVisibility(View.VISIBLE);
            layoutPendingReason.setVisibility(View.GONE);
        });

        // Tombol Back
        btnBack.setOnClickListener(v -> finish());

        // Simpan Pending
        btnSavePending.setOnClickListener(v -> {
            String alasan = etAlasanPendingDetail.getText().toString().trim();
            if (!alasan.isEmpty()) {
                updateStatusToServer("Pending", alasan);
            }
        });

        // Simpan Completed
        btnSaveCompleted.setOnClickListener(v -> {
            String catatanTeknisi = etTeknisiNote.getText().toString().trim();
            if (!catatanTeknisi.isEmpty()) {
                updateStatusToServer("Completed", catatanTeknisi);
            }
        });
    }

    private void updateStatusToServer(String status, String note) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // bikin body request
        ComplaintStatusRequest req = new ComplaintStatusRequest(status, "tech_001", note);

        apiService.updateStatus(komplainId, req).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    String time = new SimpleDateFormat("dd MMMM yyyy • HH:mm", Locale.getDefault()).format(new Date());
                    historyList.add(new HistoryTeknisi(status, note, time));
                    historyAdapter.notifyDataSetChanged();
                    tvStatusDetail.setText(status);

                    layoutPendingReason.setVisibility(View.GONE);
                    layoutCompletedInfo.setVisibility(View.GONE);
                    etAlasanPendingDetail.setText("");
                    etTeknisiNote.setText("");

                    if (status.equals("Completed")) {
                        btnPending.setVisibility(View.GONE);
                        btnCompleted.setVisibility(View.GONE);
                    }

                    Toast.makeText(KomplainDetailActivity.this, "Status berhasil diupdate", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("API_RESPONSE", "Update gagal: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                Toast.makeText(KomplainDetailActivity.this, "Gagal update status", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
