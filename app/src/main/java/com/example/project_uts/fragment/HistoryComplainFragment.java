package com.example.project_uts.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.adapter.HistoryAdapter;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.models.ComplaintResponse;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryComplainFragment extends Fragment {

    private RecyclerView rvHistoryComplaints;
    private LinearLayout emptyState;
    private ProgressBar progressBar;
    private HistoryAdapter adapter;
    private List<Complaint> complaints = new ArrayList<>();
    private List<Complaint> allComplaints = new ArrayList<>();

    private MaterialButton btnSemua, btnAktif, btnSelesai;
    private ApiService apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize API Service
        apiService = ApiClient.getApiService();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_complain, container, false);

        initViews(view);
        setupRecyclerView();
        setupFilterButtons();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupFilterButtons();

        // Check if filter passed from dashboard
        if (getArguments() != null) {
            String filter = getArguments().getString("filter", "semua");

            // Direct button click berdasarkan filter
            if (filter.equals("aktif") && btnAktif != null) {
                btnAktif.performClick();
            } else if (filter.equals("selesai") && btnSelesai != null) {
                btnSelesai.performClick();
            } else if (btnSemua != null) {
                btnSemua.performClick();
            }
        } else {
            // Jika tidak ada filter, load semua
            loadComplaints();
            if (btnSemua != null) updateButtonStates(btnSemua);
        }
    }

    private void initViews(View view) {
        rvHistoryComplaints = view.findViewById(R.id.rv_history_complaints);
        emptyState = view.findViewById(R.id.empty_state);
        progressBar = view.findViewById(R.id.progressBar);
        btnSemua = view.findViewById(R.id.btn_semua);
        btnAktif = view.findViewById(R.id.btn_aktif);
        btnSelesai = view.findViewById(R.id.btn_selesai);

        // Jika progressBar belum ada di XML, tambah programmatically
        if (progressBar == null) {
            progressBar = new ProgressBar(getContext());
            progressBar.setIndeterminate(true);
        }
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

        // DEBUG: CEK STATUS SETIAP COMPLAINT
        //Log.d("HISTORY_DEBUG", "=== FILTER: " + filter + " ===");
        for (Complaint complaint : allComplaints) {
            String status = complaint.getStatus();
            String judul = complaint.getJudul();
            //Log.d("HISTORY_DEBUG", "Complaint: " + judul + " | Status: " + status);
        }

        switch (filter) {
            case "semua":
                complaints.addAll(allComplaints);
                break;
            case "aktif":
                for (Complaint complaint : allComplaints) {
                    String status = complaint.getStatus().toLowerCase();
                    boolean isActive = status.equals("pending") || status.equals("on_progress");
                    if (isActive) {
                        complaints.add(complaint);
                        //Log.d("HISTORY_DEBUG", "✓ Added to AKTIF: " + complaint.getJudul() + " (" + status + ")");
                    }
                }
                break;
            case "selesai":
                for (Complaint complaint : allComplaints) {
                    String status = complaint.getStatus().toLowerCase();
                    boolean isCompleted = status.equals("completed");
                    if (isCompleted) {
                        complaints.add(complaint);
                        //Log.d("HISTORY_DEBUG", "✓ Added to SELESAI: " + complaint.getJudul() + " (" + status + ")");
                    }
                }
                break;
        }

        Log.d("HISTORY_DEBUG", "Total after filter: " + complaints.size());

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        updateUI();
    }

    private boolean isActiveComplaint(Complaint complaint) {
        String status = complaint.getStatus().toLowerCase();
        // Mapping yang benar
        return status.equals("pending") || status.equals("in_progress");
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

    /**
     * LOAD DATA DARI API (REPLACE DUMMY DATA)
     */
    private void loadComplaints() {
        showLoading(true);

        Call<ApiResponse<ComplaintResponse>> call = apiService.getComplaints(1, 100);

        call.enqueue(new Callback<ApiResponse<ComplaintResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ComplaintResponse>> call,
                                   Response<ApiResponse<ComplaintResponse>> response) {
                // ✅ CHECK JIKA FRAGMENT MASIH ATTACHED
                if (!isAdded() || getContext() == null) {
                    Log.e("HISTORY_DEBUG", "Fragment not attached, skipping response");
                    return;
                }

                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ComplaintResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Complaint> allFromApi = apiResponse.getData().getComplaints();

                        // SIMPAN SEMUA
                        allComplaints.clear();
                        allComplaints.addAll(allFromApi);

                        // ✅ DAPATKAN USER ID DENGAN AMAN
                        String currentUserId = getCurrentUserIdSafely();

                        Log.d("HISTORY_DEBUG", "API returned: " + allFromApi.size() + " complaints");
                        Log.d("HISTORY_DEBUG", "Current User ID: " + currentUserId);

                        // ✅ PAKAI FILTER OTOMATIS (TANPA PANGGIL AuthManage DI SINI)
                        filterComplaints("semua");
                        updateButtonStates(btnSemua);

                        updateUI();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ComplaintResponse>> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    showLoading(false);
                }
                Log.e("HISTORY_DEBUG", "API Error: " + t.getMessage());
            }
        });
    }

    private String getCurrentUserIdSafely() {
        try {
            if (getContext() != null) {
                AuthManage authManage = new AuthManage(getContext());
                return authManage.getUserId();
            }
        } catch (Exception e) {
            Log.e("HISTORY_DEBUG", "Error getting user ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * SHOW LOADING STATE
     */
    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (rvHistoryComplaints != null) {
            rvHistoryComplaints.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
        if (emptyState != null) {
            emptyState.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void showComplaintDetail(Complaint complaint) {
        // Buat dialog custom
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate layout custom
        View dialogView = LayoutInflater.from(getContext())
                .inflate(R.layout.dialog_complaint_detail, null);

        // Bind views
        TextView tvId = dialogView.findViewById(R.id.tv_detail_id);
        TextView tvJudul = dialogView.findViewById(R.id.tv_detail_judul);
        TextView tvKategori = dialogView.findViewById(R.id.tv_detail_kategori);
        TextView tvTanggal = dialogView.findViewById(R.id.tv_detail_tanggal);
        TextView tvDeskripsi = dialogView.findViewById(R.id.tv_detail_deskripsi);
        TextView tvStatusBadge = dialogView.findViewById(R.id.tv_detail_status_badge);
        ImageView ivFoto = dialogView.findViewById(R.id.iv_detail_foto);
        TextView tvFotoLabel = dialogView.findViewById(R.id.tv_foto_label);
        LinearLayout layoutTeknisi = dialogView.findViewById(R.id.layout_detail_teknisi);
        TextView tvTeknisi = dialogView.findViewById(R.id.tv_detail_teknisi);
        MaterialButton btnShare = dialogView.findViewById(R.id.btn_detail_share);
        MaterialButton btnClose = dialogView.findViewById(R.id.btn_detail_close);

        // Set data sementara dari list
        tvId.setText(complaint.getId() != null ? complaint.getId() : "N/A");
        tvJudul.setText(complaint.getJudul() != null ? complaint.getJudul() : "-");
        tvKategori.setText(complaint.getKategori() != null ? complaint.getKategori() : "-");
        tvTanggal.setText(complaint.getTanggal() != null ? complaint.getTanggal() : "-");

        // Tampilkan loading untuk deskripsi
        tvDeskripsi.setText("Memuat deskripsi...");

        // Set status badge sementara
        String status = complaint.getStatus() != null ? complaint.getStatus().toLowerCase() : "";
        setStatusBadge(tvStatusBadge, status);

        // Handle foto
        ivFoto.setVisibility(View.GONE);
        tvFotoLabel.setVisibility(View.GONE);

        // Handle teknisi
        if (complaint.getTeknisi_id() != null && !complaint.getTeknisi_id().isEmpty()) {
            layoutTeknisi.setVisibility(View.VISIBLE);
            tvTeknisi.setText("Teknisi #" + complaint.getTeknisi_id());
        } else {
            layoutTeknisi.setVisibility(View.GONE);
        }

        // Setup dialog
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Button listeners
        btnShare.setOnClickListener(v -> {
            shareComplaint(complaint);
            dialog.dismiss();
        });

        btnClose.setOnClickListener(v -> dialog.dismiss());

        // Tampilkan dialog
        dialog.show();

        // Set dialog window size
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        // AMBIL DATA DETAIL LENGKAP DARI API
        ApiService apiService = ApiClient.getApiService();
        apiService.getComplaintDetail(complaint.getId()).enqueue(new Callback<ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<ApiResponse<Complaint>> call, Response<ApiResponse<Complaint>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Complaint detailComplaint = response.body().getData();

                    // Update UI dengan data lengkap
                    requireActivity().runOnUiThread(() -> {
                        // Update deskripsi
                        if (detailComplaint.getDeskripsi() != null &&
                                !detailComplaint.getDeskripsi().isEmpty()) {
                            tvDeskripsi.setText(detailComplaint.getDeskripsi());
                        }

                        // Update status jika berbeda
                        if (detailComplaint.getStatus() != null &&
                                !detailComplaint.getStatus().equals(complaint.getStatus())) {
                            setStatusBadge(tvStatusBadge, detailComplaint.getStatus().toLowerCase());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Complaint>> call, Throwable t) {
                // Biarkan data dari list tetap tampil
            }
        });
    }

    private void setStatusBadge(TextView statusView, String status) {
        int bgRes;

        switch (status) {
            case "selesai":
            case "completed":
                bgRes = R.drawable.badge_pending;
                statusView.setText("SELESAI");
                break;
            case "on progress":
                bgRes = R.drawable.badge_proses;
                statusView.setText("DIPROSES");
                break;
            case "ditolak":
            case "rejected":
                bgRes = R.drawable.badge_selesai;
                statusView.setText("DITOLAK");
                break;
            case "pending":
            default:
                bgRes = R.drawable.badge_default;
                statusView.setText("PENDING");
                break;
        }

        try {
            statusView.setBackgroundResource(bgRes);
        } catch (Exception e) {
            // Fallback
            statusView.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void shareComplaint(Complaint complaint) {
        String shareText = "📋 Detail Komplain TeknoServe:\n\n" +
                "ID: " + complaint.getId() + "\n" +
                "Judul: " + complaint.getJudul() + "\n" +
                "Kategori: " + complaint.getKategori() + "\n" +
                "Status: " + complaint.getStatus() + "\n" +
                "Tanggal: " + complaint.getTanggal() + "\n" +
                "Deskripsi: " + complaint.getDeskripsi() + "\n\n" +
                "Shared via TeknoServe App";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Bagikan detail komplain"));
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
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }
}