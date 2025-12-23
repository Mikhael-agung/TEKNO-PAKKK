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
import android.widget.Toast;

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

import java.io.IOException;
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

        switch (filter) {
            case "semua":
                complaints.addAll(allComplaints);
                break;
            case "aktif":
                for (Complaint complaint : allComplaints) {
                    String status = complaint.getStatus().toLowerCase();
                    // Mapping yang benar
                    if (status.equals("pending") || status.equals("in_progress")) {
                        complaints.add(complaint);
                    }
                }
                break;
            case "selesai":
                for (Complaint complaint : allComplaints) {
                    String status = complaint.getStatus().toLowerCase();
                    // Mapping yang benar
                    if (status.equals("completed") || status.equals("rejected")) {
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

        Call<ApiResponse<ComplaintResponse>> call = apiService.getComplaints(1, 20);

        call.enqueue(new Callback<ApiResponse<ComplaintResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ComplaintResponse>> call,
                                   Response<ApiResponse<ComplaintResponse>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ComplaintResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // AMBIL DATA DARI ComplaintResponse
                        List<Complaint> complaintsFromApi = apiResponse.getData().getComplaints();

                        allComplaints.clear();
                        allComplaints.addAll(complaintsFromApi);

                        filterComplaints("semua");
                        updateButtonStates(btnSemua);

                        Log.d("HISTORY_DEBUG", "Loaded " + allComplaints.size() + " complaints");
                        updateUI();

                        if (allComplaints.isEmpty()) {
                            Toast.makeText(getContext(), "Belum ada komplain", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                } else {
                    // Handle error
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ComplaintResponse>> call, Throwable t) {
                showLoading(false);
                Log.e("HISTORY_DEBUG", "API Call Failed: " + t.getMessage());
                showEmptyState();
            }
        });
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

    /**
     * Tampilkan detail komplain dalam dialog
     */
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

        // Set data
        tvId.setText(complaint.getId() != null ? complaint.getId() : "N/A");
        tvJudul.setText(complaint.getJudul() != null ? complaint.getJudul() : "-");
        tvKategori.setText(complaint.getKategori() != null ? complaint.getKategori() : "-");
        tvTanggal.setText(complaint.getTanggal() != null ? complaint.getTanggal() : "-");
        tvDeskripsi.setText(complaint.getDeskripsi() != null ? complaint.getDeskripsi() : "-");

        // Set status badge
        String status = complaint.getStatus() != null ? complaint.getStatus().toLowerCase() : "";
        setStatusBadge(tvStatusBadge, status);

        // Handle foto (jika ada URL foto di model, tambahkan field nanti)
        // Untuk sekarang, sembunyikan
        ivFoto.setVisibility(View.GONE);
        tvFotoLabel.setVisibility(View.GONE);

        // Handle teknisi (jika ada)
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

        // Set dialog window size jika perlu
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void setStatusBadge(TextView statusView, String status) {
        int bgRes;

        switch (status) {
            case "selesai":
            case "completed":
                bgRes = R.drawable.badge_success;
                statusView.setText("SELESAI");
                break;
            case "dalam proses":
            case "in_progress":
                bgRes = R.drawable.badge_warning;
                statusView.setText("DIPROSES");
                break;
            case "ditolak":
            case "rejected":
                bgRes = R.drawable.badge_danger;
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