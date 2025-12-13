package com.example.project_uts.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.adapter.HistoryAdapter;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
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

        // LOAD DATA DARI API (bukan dummy)
        loadComplaintsFromApi();

        return view;
    }

    private void initViews(View view) {
        rvHistoryComplaints = view.findViewById(R.id.rv_history_complaints);
        emptyState = view.findViewById(R.id.empty_state);
        progressBar = view.findViewById(R.id.progressBar); // Tambah ini di XML nanti
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

    /**
     * LOAD DATA DARI API (REPLACE DUMMY DATA)
     */
    private void loadComplaintsFromApi() {
        showLoading(true);

        // Panggil API untuk get complaints
        Call<ApiResponse<List<Complaint>>> call = apiService.getComplaints(1, 20);

        call.enqueue(new Callback<ApiResponse<List<Complaint>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Complaint>>> call,
                                   Response<ApiResponse<List<Complaint>>> response) {
                showLoading(false);

                if (!isAdded() || getContext() == null) {
                    return; // Fragment udah destroyed, jangan update UI
                }

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Complaint>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        // Update data dari API
                        allComplaints.clear();
                        allComplaints.addAll(apiResponse.getData());

                        // Default show semua
                        filterComplaints("semua");
                        updateButtonStates(btnSemua);

                        Toast.makeText(getContext(),
                                "Data berhasil dimuat", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),
                                apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Complaint>>> call, Throwable t) {
                showLoading(false);
                if (isAdded() && getContext() != null) {
                    Toast.makeText(requireContext(),
                            "Gagal terhubung ke server", Toast.LENGTH_SHORT).show();
                }
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
            emptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Tampilkan detail komplain dalam dialog
     */
    private void showComplaintDetail(Complaint complaint) {
        // Pakai AlertDialog yang udah ada (gak perlu diubah)
        // ... kode dialog tetap sama
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