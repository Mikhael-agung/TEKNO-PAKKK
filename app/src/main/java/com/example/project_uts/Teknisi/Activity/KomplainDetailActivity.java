package com.example.project_uts.Teknisi.Activity;

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

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.HistoryTeknisiAdapter;
import com.example.project_uts.Teknisi.Model.ComplaintStatusRequest;
import com.example.project_uts.Teknisi.Model.HistoryTeknisi;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;

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
    private TextView tvJudulDetail, tvPelaporDetail, tvStatusDetail, tvWaktuDetail, tvDeskripsiDetail;
    private ImageView ivFotoBarang;
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
                updateStatusToServer("pending", alasan);
            } else {
                Toast.makeText(KomplainDetailActivity.this, "Harap isi alasan pending", Toast.LENGTH_SHORT).show();
            }
        });

        // Simpan Completed
        btnSaveCompleted.setOnClickListener(v -> {
            String catatanTeknisi = etTeknisiNote.getText().toString().trim();
            if (!catatanTeknisi.isEmpty()) {
                updateStatusToServer("completed", catatanTeknisi);
            } else {
                Toast.makeText(KomplainDetailActivity.this, "Harap isi catatan teknisi", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatusToServer(String status, String note) {

        ApiService apiService = ApiClient.getApiService();
        Map<String, String> statusMap = new HashMap<>();
        String backendStatus;

        if (status.equalsIgnoreCase("Mulai Kerja") ||
                status.equalsIgnoreCase("Mulai kerja")) {
            backendStatus = "on_progress";
        } else if (status.equalsIgnoreCase("tunda") ||
                status.equalsIgnoreCase("kendala")) {
            backendStatus = "pending";
        } else if (status.equalsIgnoreCase("selesai")) {
            backendStatus = "completed";
        } else {
            // Fallback ke method helper
            backendStatus = mapStatusToBackendFormat(status);
        }

        final String backendStatusFinal = backendStatus;

        statusMap.put("status", backendStatusFinal);
        statusMap.put("alasan", note);

        Log.d("API_DEBUG", "Updating status: " + backendStatusFinal + ", alasan: " + note);
        Log.d("API_DEBUG", "Complaint ID: " + komplainId);

        apiService.updateStatus(komplainId, statusMap).enqueue(new Callback<ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<ApiResponse<Complaint>> call, Response<ApiResponse<Complaint>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Complaint> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // Success - update UI
                        Complaint updatedComplaint = apiResponse.getData();

                        // Add to history list
                        String time = new SimpleDateFormat("dd MMMM yyyy • HH:mm", Locale.getDefault()).format(new Date());
                        historyList.add(new HistoryTeknisi(backendStatusFinal, note, time));
                        historyAdapter.notifyDataSetChanged();

                        // Update status display
                        tvStatusDetail.setText(backendStatusFinal);

                        layoutPendingReason.setVisibility(View.GONE);
                        layoutCompletedInfo.setVisibility(View.GONE);
                        etAlasanPendingDetail.setText("");
                        etTeknisiNote.setText("");

                        // Jika status completed, hide action buttons
                        if (backendStatusFinal.equals("completed")) {
                            btnPending.setVisibility(View.GONE);
                            btnCompleted.setVisibility(View.GONE);

                        }

                        Toast.makeText(KomplainDetailActivity.this,
                                "Status berhasil diupdate", Toast.LENGTH_SHORT).show();

                        Log.d("API_DEBUG", "Status update successful: " + apiResponse.getMessage());

                    } else {
                        Toast.makeText(KomplainDetailActivity.this,
                                "Gagal: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("API_ERROR", "API Error: " + apiResponse.getMessage());
                    }
                } else {
                    // HTTP error
                    String errorMsg = "Gagal update status";
                    if (response.code() == 403) {
                        errorMsg = "Akses ditolak. Pastikan Anda adalah teknisi.";
                    } else if (response.code() == 404) {
                        errorMsg = "Komplain tidak ditemukan";
                    }

                    Toast.makeText(KomplainDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("API_RESPONSE", "Update gagal. Code: " + response.code() +
                            ", Message: " + (response.errorBody() != null ? response.errorBody().toString() : "No error body"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Complaint>> call, Throwable t) {
                Log.e("API_ERROR", "Network error: " + t.getMessage(), t);
                Toast.makeText(KomplainDetailActivity.this,
                        "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Helper method untuk mapping status dari UI ke format backend
     */
    private String mapStatusToBackendFormat(String uiStatus) {
        // UI mungkin kirim: "Pending", "Completed", "Progress", dll
        // Backend expect: "pending", "completed", "on_progress", "complaint"

        String lowerStatus = uiStatus.toLowerCase();

        switch (lowerStatus) {
            case "pending":
            case "menunggu":
                return "pending";

            case "completed":
            case "selesai":
            case "done":
                return "completed";

            case "progress":
            case "proses":
            case "diproses":
            case "onprogress":
                return "on_progress";

            case "complaint":
            case "pengaduan":
            case "new":
                return "complaint";

            default:
                // Return as-is jika tidak diketahui
                return lowerStatus;
        }
    }
}