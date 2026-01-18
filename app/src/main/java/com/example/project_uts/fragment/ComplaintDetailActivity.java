package com.example.project_uts.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.HistoryTeknisiAdapter;
import com.example.project_uts.Teknisi.Model.HistoryTeknisi;
import com.example.project_uts.adapter.CustomerTimelineAdapter;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.models.User;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintDetailActivity extends AppCompatActivity {

    private Complaint complaint;
    private RecyclerView rvTimeline;
    private CustomerTimelineAdapter historyAdapter;
    private List<HistoryTeknisi> historyList = new ArrayList<>();
    private ProgressBar progressBar, progressBarDetail;
    private TextView tvNoTimeline;

    // Technician UI elements
    private TextView tvTechnicianName, tvTechnicianStatus;
    private ImageButton btnCall;
    private MaterialButton btnContactTechnician;
    private View technicianCard;

    // Data teknisi dari timeline
    private String teknisiNameFromTimeline = "";
    private String teknisiPhoneFromTimeline = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_detail);

        // Get complaint data from intent (tapi mungkin tidak lengkap)
        complaint = (Complaint) getIntent().getSerializableExtra("complaint");
        if (complaint == null) {
            Toast.makeText(this, "Data complaint tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupToolbar();
        setupViews();
        setupButtonListeners();

        // Tampilkan loading untuk data detail
        if (progressBarDetail != null) {
            progressBarDetail.setVisibility(View.VISIBLE);
        }

        fetchComplaintDetails();
    }

    private void setupToolbar() {
        // Setup tombol back
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // Status chip akan di-set setelah data lengkap didapat
    }

    private void setupViews() {
        rvTimeline = findViewById(R.id.rv_timeline);
        progressBar = findViewById(R.id.progress_bar_timeline);
        progressBarDetail = findViewById(R.id.progress_bar_detail);
        tvNoTimeline = findViewById(R.id.tv_no_timeline);

        // Technician views
        technicianCard = findViewById(R.id.technician_card);
        tvTechnicianName = findViewById(R.id.tv_technician_name);
        tvTechnicianStatus = findViewById(R.id.tv_technician_status);
        btnCall = findViewById(R.id.btn_call);
        btnContactTechnician = findViewById(R.id.btn_contact_technician);

        // Setup RecyclerView
        if (rvTimeline != null) {
            rvTimeline.setLayoutManager(new LinearLayoutManager(this));
            historyAdapter = new CustomerTimelineAdapter(this, historyList);
            rvTimeline.setAdapter(historyAdapter);
        }

        // Hide semua dulu sampai data load
        if (rvTimeline != null) rvTimeline.setVisibility(View.GONE);
        if (tvNoTimeline != null) tvNoTimeline.setVisibility(View.GONE);
        if (progressBar != null) progressBar.setVisibility(View.GONE);

        // Sembunyikan UI detail sementara
        hideDetailUI();
    }

    private void hideDetailUI() {
        // Sembunyikan card-card sampai data lengkap
        View technicianCard = findViewById(R.id.technician_card);
        View progressCard = findViewById(R.id.card_progress);
        View detailsCard = findViewById(R.id.card_details);

        if (technicianCard != null) technicianCard.setVisibility(View.GONE);
        if (progressCard != null) progressCard.setVisibility(View.GONE);
        if (detailsCard != null) detailsCard.setVisibility(View.GONE);
    }

    private void showDetailUI() {
        // Tampilkan semua card setelah data lengkap
        View technicianCard = findViewById(R.id.technician_card);
        View progressCard = findViewById(R.id.card_progress);
        View detailsCard = findViewById(R.id.card_details);

        if (technicianCard != null) technicianCard.setVisibility(View.VISIBLE);
        if (progressCard != null) progressCard.setVisibility(View.VISIBLE);
        if (detailsCard != null) detailsCard.setVisibility(View.VISIBLE);
    }

    private void setupButtonListeners() {
        MaterialButton btnShare = findViewById(R.id.btn_share);

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> shareComplaintDetails());
        }

        // Tombol hubungi teknisi langsung ke WhatsApp
        if (btnContactTechnician != null) {
            btnContactTechnician.setOnClickListener(v -> {
                openWhatsAppDirect();
            });
        }
    }

    private void openWhatsAppDirect() {
        // Cek apakah ada teknisi yang ditugaskan
        boolean hasTeknisiFromComplaint = complaint.getTeknisi_name() != null &&
                !complaint.getTeknisi_name().isEmpty() &&
                !complaint.getTeknisi_name().equalsIgnoreCase("Belum ditugaskan");

        String teknisiPhone = complaint.getTeknisi_phone();

        if ((teknisiPhone == null || teknisiPhone.trim().isEmpty()) && !teknisiPhoneFromTimeline.isEmpty()) {
            teknisiPhone = teknisiPhoneFromTimeline;
           // Log.d("WhatsAppDebug", "📱 Fallback ke phone dari timeline: " + teknisiPhone);
        }

        if (!teknisiNameFromTimeline.isEmpty() && !teknisiPhoneFromTimeline.isEmpty()) {
            hasTeknisiFromComplaint = true;
            teknisiPhone = teknisiPhoneFromTimeline;
            //Log.d("WhatsAppDebug", "📱 Menggunakan teknisi dari timeline");
        }

        if (!hasTeknisiFromComplaint || teknisiPhone == null || teknisiPhone.trim().isEmpty()) {
            // Jika belum ada teknisi atau tidak ada nomor
            new AlertDialog.Builder(this)
                    .setTitle("Hubungi Teknisi")
                    .setMessage(hasTeknisiFromComplaint ?
                            "Teknisi " + complaint.getTeknisi_name() + " sudah ditugaskan, tetapi nomor telepon tidak tersedia." :
                            "Belum ada teknisi yang ditugaskan untuk komplain ini.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Bersihkan nomor telepon
        String cleanPhone = cleanPhoneNumber(teknisiPhone);

        if (cleanPhone == null || cleanPhone.trim().isEmpty() || cleanPhone.length() < 10) {
            new AlertDialog.Builder(this)
                    .setTitle("Nomor Tidak Valid")
                    .setMessage("Nomor telepon teknisi tidak valid: " + teknisiPhone)
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        // Buat pesan WhatsApp
        String message = buildWhatsAppMessage();
        String encodedMessage = Uri.encode(message);

        // URL WhatsApp
        String whatsappUrl = "https://wa.me/" + cleanPhone + "?text=" + encodedMessage;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(whatsappUrl));
        intent.setPackage("com.whatsapp");

        try {
            // Cek apakah WhatsApp terinstall
            PackageManager packageManager = getPackageManager();
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
                //Log.d("WhatsAppDebug", "✅ WhatsApp dibuka");
            } else {
                // WhatsApp tidak terinstall, buka browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl));
                startActivity(browserIntent);
                Toast.makeText(this, "WhatsApp tidak terinstall, membuka browser", Toast.LENGTH_SHORT).show();
                //Log.d("WhatsAppDebug", "🌐 Buka via browser");
            }
        } catch (Exception e) {
            //Log.e("WhatsAppDebug", "❌ Error buka WhatsApp: " + e.getMessage());
            Toast.makeText(this, "Gagal membuka WhatsApp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private String buildWhatsAppMessage() {
        StringBuilder message = new StringBuilder();

        // Salam
        message.append("Halo ");

        // Tambah nama teknisi jika ada
        String teknisiName = complaint.getTeknisi_name();
        if ((teknisiName == null || teknisiName.isEmpty()) && !teknisiNameFromTimeline.isEmpty()) {
            teknisiName = teknisiNameFromTimeline;
        }

        if (teknisiName != null && !teknisiName.isEmpty()) {
            message.append(teknisiName);
        } else {
            message.append("Teknisi");
        }

        message.append(",\n\n");

        // Informasi komplain
        message.append("Saya ingin bertanya tentang komplain saya:\n");
        message.append("• ID Komplain: ").append(complaint.getId() != null ? complaint.getId() : "N/A").append("\n");
        message.append("• Judul: ").append(complaint.getJudul() != null ? complaint.getJudul() : "N/A").append("\n");
        message.append("• Kategori: ").append(complaint.getKategori() != null ? complaint.getKategori() : "N/A").append("\n");
        message.append("• Status: ").append(complaint.getStatus() != null ? complaint.getStatus() : "N/A").append("\n\n");

        // Pesan berdasarkan status
        String status = complaint.getStatus() != null ? complaint.getStatus().toLowerCase() : "";

        if (status.contains("selesai") || status.contains("completed")) {
            message.append("Komplain saya sudah selesai. Terima kasih atas bantuannya!");
        } else if (status.contains("proses") || status.contains("progress") || status.contains("on_progress")) {
            message.append("Bisa dibantu update progress perbaikannya?");
        } else if (status.contains("pending")) {
            message.append("Komplain saya masih pending. Apakah ada kendala?");
        } else {
            message.append("Bisa dibantu update progress-nya?");
        }

        return message.toString();
    }

    private void fetchComplaintDetails() {
        if (complaint.getId() == null) {
            Toast.makeText(this, "ID Complaint tidak valid", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        final String complaintId = complaint.getId();
        ApiService apiService = ApiClient.getFreshApiService();
        apiService.getComplaintDetail(complaintId).enqueue(new Callback<ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<ApiResponse<Complaint>> call, Response<ApiResponse<Complaint>> response) {
                runOnUiThread(() -> {
                    if (progressBarDetail != null) {
                        progressBarDetail.setVisibility(View.GONE);
                    }

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Complaint fullComplaint = response.body().getData();
                        complaint = fullComplaint;

                        // Update UI
                        displayComplaintData();
                        showDetailUI();

                        // Load timeline
                        fetchStatusHistory();

                    } else {
                      //  Log.e("ComplaintDebug", "❌ Gagal ambil data detail");
                        // Tetap tampilkan data yang ada
                        displayComplaintData();
                        showDetailUI();
                        fetchStatusHistory();
                    }
                });
            }

            @Override
            public void onFailure(Call<ApiResponse<Complaint>> call, Throwable t) {
                runOnUiThread(() -> {
                    if (progressBarDetail != null) {
                        progressBarDetail.setVisibility(View.GONE);
                    }
                    //Log.e("ComplaintDebug", "❌ Error: " + t.getMessage());
                    displayComplaintData();
                    showDetailUI();
                    fetchStatusHistory();
                });
            }
        });
    }

    private void displayComplaintData() {
        // Complaint details
        TextView tvId = findViewById(R.id.tv_id);
        TextView tvCategory = findViewById(R.id.tv_category);
        TextView tvDate = findViewById(R.id.tv_date);
        TextView tvDescription = findViewById(R.id.tv_description);
        TextView tvAddress = findViewById(R.id.tv_address);

        // Set status chip dengan data lengkap
        TextView chipStatus = findViewById(R.id.chip_status);
        if (chipStatus != null) {
            setStatusChip(chipStatus, complaint.getStatus());
        }

        if (tvId != null) {
            String id = complaint.getId();
            tvId.setText(id != null ? id : "N/A");
        }

        if (tvCategory != null) {
            String kategori = complaint.getKategori();
            tvCategory.setText(kategori != null ? kategori : "Tidak ada kategori");
        }

        if (tvDate != null) {
            String date = complaint.getFormattedTimelineDate();
            tvDate.setText(date != null && !date.isEmpty() ? date : "Tanggal tidak tersedia");
        }

        if (tvDescription != null) {
            String deskripsi = complaint.getDeskripsi();

            if (deskripsi == null) {
                // Coba alternatif lain
                if (complaint.getResolution_notes() != null &&
                        !complaint.getResolution_notes().trim().isEmpty()) {
                    tvDescription.setText("Catatan penyelesaian: " + complaint.getResolution_notes());
                } else if (complaint.getJudul() != null) {
                    tvDescription.setText("Judul: " + complaint.getJudul());
                } else {
                    tvDescription.setText("Tidak ada deskripsi masalah");
                }
            }
            else if (deskripsi.trim().isEmpty()) {
                tvDescription.setText("Deskripsi: (kosong)");
            }
            else {
                tvDescription.setText(deskripsi);
            }
        }

        if (tvAddress != null) {
            String alamat = complaint.getFullAlamat();
            tvAddress.setText(alamat != null && !alamat.isEmpty() ? alamat : "Alamat tidak tersedia");
        }

        // Setup UI teknisi
        setupTechnicianUI();
    }

    private void setupTechnicianUI() {
        if (!teknisiNameFromTimeline.isEmpty()) {
            //Log.d("ComplaintDebug", "✅ Teknisi ditemukan di timeline: " + teknisiNameFromTimeline);
            showTechnicianUI(teknisiNameFromTimeline, teknisiPhoneFromTimeline);
            return;
        }

        // Jika tidak ada di timeline, cek di complaint data
        boolean hasTeknisiFromComplaint = complaint.getTeknisi_name() != null &&
                !complaint.getTeknisi_name().isEmpty() &&
                !complaint.getTeknisi_name().equalsIgnoreCase("Belum ditugaskan");

        if (hasTeknisiFromComplaint) {
            final String teknisiName = complaint.getTeknisi_name();
            String teknisiPhone = complaint.getTeknisi_phone();

            //Log.d("ComplaintDebug", "✅ Teknisi ditemukan di complaint data: " + teknisiName);
            showTechnicianUI(teknisiName, teknisiPhone != null ? teknisiPhone : "");
            return;
        }

        // Jika benar-benar tidak ada teknisi
       // Log.d("ComplaintDebug", "❌ Belum ada teknisi yang ditugaskan");
        hideTechnicianUI();
    }

    private String cleanPhoneNumber(String phone) {
        if (phone == null) {
            return "";
        }

        // Hapus semua karakter non-digit
        String clean = phone.replaceAll("[^0-9]", "");

        if (clean.isEmpty()) {
            return "";
        }

        // Hapus leading 0 jika ada, ganti dengan 62
        if (clean.startsWith("0")) {
            clean = "62" + clean.substring(1);
        }

        // Jika tidak diawali 62, tambahkan
        if (!clean.startsWith("62") && clean.length() > 0) {
            clean = "62" + clean;
        }

        return clean;
    }

    private void showTechnicianUI(final String teknisiName, final String teknisiPhone) {
       // Log.d("ComplaintDebug", "🔄 Menampilkan UI Teknisi: " + teknisiName);

        // ============ TAMPILKAN CARD TEKNISI ============
        if (technicianCard != null) {
            technicianCard.setVisibility(View.VISIBLE);
        }
        if (tvTechnicianName != null) {
            tvTechnicianName.setText(teknisiName);
        }
        if (tvTechnicianStatus != null) {
            tvTechnicianStatus.setText("Teknisi telah ditugaskan");
        }

        // ============ SETUP TOMBOL TELEPON ============
        if (btnCall != null) {
            // Sembunyikan tombol telepon biasa
            btnCall.setVisibility(View.GONE);
        }

        // ============ SETUP TOMBOL WHATSAPP ============
        if (btnContactTechnician != null) {
            btnContactTechnician.setEnabled(true);
            btnContactTechnician.setBackgroundColor(getResources().getColor(R.color.primary_color));
            btnContactTechnician.setText("Hubungi Teknisi");

            // Pastikan listener sudah ada
            btnContactTechnician.setOnClickListener(v -> {
                openWhatsAppDirect();
            });

            //Log.d("ComplaintDebug", "✅ Tombol WhatsApp AKTIF");
        }

        //Log.d("ComplaintDebug", "✅ UI Teknisi berhasil ditampilkan");
    }

    private void hideTechnicianUI() {
        if (technicianCard != null) {
            technicianCard.setVisibility(View.GONE);
        }
        if (btnContactTechnician != null) {
            btnContactTechnician.setEnabled(false);
            btnContactTechnician.setBackgroundColor(getResources().getColor(R.color.gray));
            btnContactTechnician.setText("Menunggu Teknisi");

            // Setup listener untuk tombol disabled
            btnContactTechnician.setOnClickListener(v -> {
                Toast.makeText(ComplaintDetailActivity.this,
                        "Belum ada teknisi yang ditugaskan", Toast.LENGTH_SHORT).show();
            });
        }
        if (btnCall != null) {
            btnCall.setVisibility(View.GONE);
        }
    }

    private void extractTechnicianInfoFromHistory(List<HistoryTeknisi> historyList) {
        teknisiNameFromTimeline = "";
        teknisiPhoneFromTimeline = "";

        //Log.d("ComplaintDebug", "🔍 Mencari data teknisi dari " + historyList.size() + " items timeline");

        for (int i = 0; i < historyList.size(); i++) {
            HistoryTeknisi history = historyList.get(i);

            if (history.getTeknisi() != null) {
                String fullName = history.getTeknisi().getFull_name();
                String phone = history.getTeknisi().getPhone();

                if (fullName != null && !fullName.isEmpty()) {
                    teknisiNameFromTimeline = fullName;

                    if (phone != null && !phone.trim().isEmpty()) {
                        teknisiPhoneFromTimeline = phone.trim();
                        //Log.d("ComplaintDebug", "✅ Found phone: " + teknisiPhoneFromTimeline);
                    }
                    break;
                }
            }
        }
    }

    private void fetchStatusHistory() {
        if (complaint.getId() == null) {
            showNoTimelineData("ID Complaint tidak valid");
            return;
        }

        final String complaintId = complaint.getId();

        // Show timeline loading
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = ApiClient.getApiService();
        apiService.getComplaintStatuses(complaintId).enqueue(new Callback<ApiResponse<List<HistoryTeknisi>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<HistoryTeknisi>>> call,
                                   Response<ApiResponse<List<HistoryTeknisi>>> response) {

                if (progressBar != null) progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<HistoryTeknisi> apiHistoryList = response.body().getData();

                    if (apiHistoryList != null && !apiHistoryList.isEmpty()) {
                        // Urutkan dari terlama ke terbaru (ascending)
                        Collections.sort(apiHistoryList, (h1, h2) -> {
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                                String date1Str = h1.getCreatedAt() != null ? h1.getCreatedAt().split("\\.")[0] : "";
                                String date2Str = h2.getCreatedAt() != null ? h2.getCreatedAt().split("\\.")[0] : "";

                                Date date1 = sdf.parse(date1Str);
                                Date date2 = sdf.parse(date2Str);
                                return date1.compareTo(date2);
                            } catch (Exception e) {
                                return 0;
                            }
                        });

                        // Reverse menjadi terbaru di atas (descending)
                        Collections.reverse(apiHistoryList);

                        // Ekstrak info teknisi dari timeline
                        extractTechnicianInfoFromHistory(apiHistoryList);

                        // Update list untuk RecyclerView
                        historyList.clear();
                        historyList.addAll(apiHistoryList);
                        if (historyAdapter != null) {
                            historyAdapter.notifyDataSetChanged();
                        }

                        runOnUiThread(() -> {
                            setupTechnicianUI();
                        });

                        // Show timeline
                        if (rvTimeline != null) rvTimeline.setVisibility(View.VISIBLE);
                        if (tvNoTimeline != null) tvNoTimeline.setVisibility(View.GONE);

                    } else {
                        // Tidak ada data timeline
                        showNoTimelineData("Belum ada riwayat status");
                    }

                } else {
                    // API error
                    showNoTimelineData("Gagal memuat riwayat status");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<HistoryTeknisi>>> call, Throwable t) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                showNoTimelineData("Koneksi error");
            }
        });
    }

    private void showNoTimelineData(String message) {
        if (rvTimeline != null) rvTimeline.setVisibility(View.GONE);
        if (tvNoTimeline != null) {
            tvNoTimeline.setVisibility(View.VISIBLE);
            tvNoTimeline.setText(message);
        }
    }

    private void setStatusChip(TextView chip, String status) {
        if (status == null || chip == null) return;

        String statusText;
        int bgRes;

        switch (status.toLowerCase()) {
            case "on_progress":
            case "progress":
                statusText = "ON PROGRESS";
                bgRes = R.drawable.badge_proses;
                break;
            case "completed":
                statusText = "COMPLETED";
                bgRes = R.drawable.badge_selesai;
                break;
            case "pending":
                statusText = "PENDING";
                bgRes = R.drawable.badge_pending;
                break;
            case "complaint":
                statusText = "COMPLAINT";
                bgRes = R.drawable.badge_default;
                break;
            case "assigned":
                statusText = "ASSIGNED";
                bgRes = R.drawable.badge_proses;
                break;
            default:
                statusText = status.toUpperCase();
                bgRes = R.drawable.badge_default;
                break;
        }

        chip.setText(statusText);
        chip.setBackgroundResource(bgRes);
        chip.setTextColor(getResources().getColor(R.color.white));
    }

    private void shareComplaintDetails() {
        String shareText = "Complaint Details:\n" +
                "ID: " + complaint.getId() + "\n" +
                "Category: " + (complaint.getKategori() != null ? complaint.getKategori() : "N/A") + "\n" +
                "Description: " + (complaint.getDeskripsi() != null ? complaint.getDeskripsi() : "Tidak ada deskripsi") + "\n" +
                "Status: " + complaint.getStatus() + "\n" +
                "Date: " + complaint.getFormattedTimelineDate() + "\n" +
                "Address: " + complaint.getFullAlamat();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Complaint Details"));
    }
}