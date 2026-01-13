package com.example.project_uts.fragment;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.MainActivity;
import com.example.project_uts.adapter.ComplaintGridAdapter;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.Complaint;
import com.example.project_uts.models.ComplaintResponse;
import com.example.project_uts.models.User;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardCustomerFragment extends Fragment {

    private TextView tvWelcome, tvEmail, tvPendingCount, tvProgressCount, tvCompletedCount;
    private MaterialButton btnBuatKomplain;
    private RecyclerView rvComplaintGrid;
    private ComplaintGridAdapter complaintGridAdapter;
    private List<Complaint> allComplaints = new ArrayList<>();
    private List<Complaint> filteredComplaints = new ArrayList<>();

    private AuthManage authManage;

    private MaterialButton filterAll, filterPending, filterProses, filterSelesai;
    private View emptyGrid;

    public DashboardCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            authManage = new AuthManage(getContext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_dasboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        setupFilters();
        setupClickListeners();
        loadUserData();
        loadComplaintsData();
    }

    private void initViews(View view) {
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvProgressCount = view.findViewById(R.id.tv_progress_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);

        btnBuatKomplain = view.findViewById(R.id.btn_buat_komplain);

        filterAll = view.findViewById(R.id.filter_all);
        filterPending = view.findViewById(R.id.filter_pending);
        filterProses = view.findViewById(R.id.filter_proses);
        filterSelesai = view.findViewById(R.id.filter_selesai);

        rvComplaintGrid = view.findViewById(R.id.rv_complaint_grid);
        emptyGrid = view.findViewById(R.id.empty_grid);
    }

    private void setupRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
        rvComplaintGrid.setLayoutManager(layoutManager);

        if (rvComplaintGrid == null) {
            Log.e("Dashboard", "RecyclerView is null!");
            return;
        }

        complaintGridAdapter = new ComplaintGridAdapter(getContext(), filteredComplaints);
        rvComplaintGrid.setAdapter(complaintGridAdapter);

        complaintGridAdapter.setOnItemClickListener(complaint -> {
            navigateToComplaintDetail(complaint);
        });

        Log.d("Dashboard", "RecyclerView setup complete");
    }

    private void setupFilters() {
        setActiveFilter(filterAll);

        filterAll.setOnClickListener(v -> {
            setActiveFilter(filterAll);
            applyFilter("all");
        });

        filterPending.setOnClickListener(v -> {
            setActiveFilter(filterPending);
            applyFilter("pending");
        });

        filterProses.setOnClickListener(v -> {
            setActiveFilter(filterProses);
            applyFilter("on_progress");
        });

        filterSelesai.setOnClickListener(v -> {
            setActiveFilter(filterSelesai);
            applyFilter("completed");
        });
    }

    private void setActiveFilter(MaterialButton activeButton) {
        MaterialButton[] filters = {filterAll, filterPending, filterProses, filterSelesai};

        for (MaterialButton filter : filters) {
            filter.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
            filter.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            filter.setTextColor(Color.parseColor("#666666"));
        }

        int primaryColor = getResources().getColor(R.color.colorPrimary);
        activeButton.setStrokeColor(ColorStateList.valueOf(primaryColor));
        activeButton.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
        activeButton.setTextColor(Color.WHITE);
    }

    private void applyFilter(String filterType) {
        filteredComplaints.clear();

        if (filterType.equals("all")) {
            filteredComplaints.addAll(allComplaints);
        } else {
            for (Complaint complaint : allComplaints) {
                String status = complaint.getStatus();
                if (status != null) {
                    status = status.toLowerCase();

                    if (filterType.equals("on_progress")) {
                        if (status.equals("on_progress") || status.equals("dalam proses")) {
                            filteredComplaints.add(complaint);
                        }
                    } else if (filterType.equals("completed")) {
                        if (status.equals("completed") || status.equals("selesai")) {
                            filteredComplaints.add(complaint);
                        }
                    } else if (status.equals(filterType)) {
                        filteredComplaints.add(complaint);
                    }
                }
            }
        }

        if (complaintGridAdapter != null) {
            complaintGridAdapter.updateData(filteredComplaints);
        }

        updateEmptyState();
        Log.d("Dashboard", "Filter applied: " + filterType + ", items: " + filteredComplaints.size());
    }

    private void updateEmptyState() {
        if (emptyGrid != null && rvComplaintGrid != null) {
            if (filteredComplaints.isEmpty()) {
                emptyGrid.setVisibility(View.VISIBLE);
                rvComplaintGrid.setVisibility(View.GONE);
            } else {
                emptyGrid.setVisibility(View.GONE);
                rvComplaintGrid.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setupClickListeners() {
        btnBuatKomplain.setOnClickListener(v -> {
            navigateToCustomerFragment();
        });
    }

    private void loadUserData() {
        if (getContext() == null || !isAdded()) return;

        try {
            if (authManage == null) {
                authManage = new AuthManage(getContext());
            }

            User currentUser = authManage.getUser();

            if (currentUser != null) {
                String userName = currentUser.getFull_name();
                String userEmail = currentUser.getEmail();

                tvWelcome.setText("Selamat Datang, " + userName + "!");
                if (tvEmail != null) {
                    tvEmail.setText(userEmail);
                }
            } else {
                SharedPreferences prefs = getActivity().getSharedPreferences("user_prefs", getActivity().MODE_PRIVATE);
                String userName = prefs.getString("user_name", "Customer");
                String userEmail = prefs.getString("user_email", "user@email.com");

                tvWelcome.setText("Selamat Datang, " + userName + "!");
                if (tvEmail != null) {
                    tvEmail.setText(userEmail);
                }
            }

        } catch (Exception e) {
            tvWelcome.setText("Selamat Datang!");
            if (tvEmail != null) {
                tvEmail.setText("user@email.com");
            }
        }
    }

    private void loadComplaintsData() {
        if (!isAdded()) return;

        tvPendingCount.setText("...");
        tvProgressCount.setText("...");
        tvCompletedCount.setText("...");

        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse<ComplaintResponse>> call = apiService.getComplaints(1, 100);

        call.enqueue(new Callback<ApiResponse<ComplaintResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ComplaintResponse>> call,
                                   Response<ApiResponse<ComplaintResponse>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ComplaintResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Complaint> complaints = apiResponse.getData().getComplaints();

                        allComplaints = complaints;

                        int pendingCount = 0;
                        int progressCount = 0;
                        int completedCount = 0;

                        for (Complaint complaint : complaints) {
                            String status = complaint.getStatus();
                            if (status != null) {
                                status = status.toLowerCase();

                                switch (status) {
                                    case "pending":
                                        pendingCount++;
                                        break;
                                    case "on_progress":
                                    case "dalam proses":
                                        progressCount++;
                                        break;
                                    case "completed":
                                    case "selesai":
                                        completedCount++;
                                        break;
                                }
                            }
                        }

                        final int finalPendingCount = pendingCount;
                        final int finalProgressCount = progressCount;
                        final int finalCompletedCount = completedCount;

                        // ✅ FIXED: Gunakan check isAdded() dan getActivity()
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                tvPendingCount.setText(String.valueOf(finalPendingCount));
                                tvProgressCount.setText(String.valueOf(finalProgressCount));
                                tvCompletedCount.setText(String.valueOf(finalCompletedCount));

                                applyFilter("all");

                                Log.d("Dashboard", "API data loaded: " + complaints.size() + " items");
                            });
                        }

                    } else {
                        if (isAdded() && getActivity() != null) {
                            getActivity().runOnUiThread(() -> {
                                showDummyData();
                            });
                        }
                    }
                } else {
                    if (isAdded() && getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            showDummyData();
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ComplaintResponse>> call, Throwable t) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        showDummyData();
                        Log.e("Dashboard", "API failed: " + t.getMessage());
                    });
                }
            }
        });
    }

    private void showDummyData() {
        if (!isAdded()) return;

        int pendingCount = 3;
        int progressCount = 2;
        int completedCount = 5;

        tvPendingCount.setText(String.valueOf(pendingCount));
        tvProgressCount.setText(String.valueOf(progressCount));
        tvCompletedCount.setText(String.valueOf(completedCount));

        allComplaints.clear();

        Complaint c1 = new Complaint();
        c1.setId("COMP-001");
        c1.setJudul("Printer Error");
        c1.setDeskripsi("Printer tidak bisa mencetak dokumen");
        c1.setStatus("pending");
        c1.setCreated_at("2024-12-15T10:30:00.000Z");
        allComplaints.add(c1);

        Complaint c2 = new Complaint();
        c2.setId("COMP-002");
        c2.setJudul("Network Issue");
        c2.setDeskripsi("Koneksi internet lambat di lantai 2");
        c2.setStatus("in_progress");
        c2.setCreated_at("2024-12-14T14:20:00.000Z");
        allComplaints.add(c2);

        Complaint c3 = new Complaint();
        c3.setId("COMP-003");
        c3.setJudul("Software Bug");
        c3.setDeskripsi("Aplikasi crash saat buka file besar");
        c3.setStatus("completed");
        c3.setCreated_at("2024-12-13T09:15:00.000Z");
        allComplaints.add(c3);

        Complaint c4 = new Complaint();
        c4.setId("COMP-004");
        c4.setJudul("Account Locked");
        c4.setDeskripsi("Akun terkunci setelah 3x salah password");
        c4.setStatus("pending");
        c4.setCreated_at("2024-12-12T16:45:00.000Z");
        allComplaints.add(c4);

        Complaint c5 = new Complaint();
        c5.setId("COMP-005");
        c5.setJudul("Monitor Flicker");
        c5.setDeskripsi("Monitor berkedip setiap 5 menit");
        c5.setStatus("in_progress");
        c5.setCreated_at("2024-12-11T11:10:00.000Z");
        allComplaints.add(c5);

        Complaint c6 = new Complaint();
        c6.setId("COMP-006");
        c6.setJudul("Software License");
        c6.setDeskripsi("Lisensi software tidak valid");
        c6.setStatus("ditolak");
        c6.setCreated_at("2024-12-10T13:25:00.000Z");
        allComplaints.add(c6);

        applyFilter("pending");

        if (isAdded() && getContext() != null) {
            Toast.makeText(getContext(), "Menggunakan data demo", Toast.LENGTH_SHORT).show();
        }

        Log.d("Dashboard", "Dummy data loaded: " + allComplaints.size() + " items");
    }

    private void navigateToCustomerFragment() {
        if (getActivity() instanceof MainActivity && isAdded()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CustomerFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToHistory() {
        if (getActivity() instanceof MainActivity && isAdded()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HistoryComplainFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToProfile() {
        if (getActivity() instanceof MainActivity && isAdded()) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfilFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToHistoryWithFilter(String filterType) {
        if (!isAdded()) return;

        HistoryComplainFragment historyFragment = new HistoryComplainFragment();
        Bundle args = new Bundle();

        String historyFilter = "semua";
        switch (filterType) {
            case "pending":
                historyFilter = "pending";
                break;
            case "progress":
                historyFilter = "on_progress";
                break;
            case "completed":
                historyFilter = "completed";
                break;
        }

        args.putString("filter", historyFilter);
        historyFragment.setArguments(args);

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, historyFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToComplaintDetail(Complaint complaint) {
        HistoryComplainFragment detailFragment = new HistoryComplainFragment();
        Bundle args = new Bundle();
        args.putString("complaint_id", complaint.getId());
        detailFragment.setArguments(args);

        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAdded()) {
            loadUserData();
            loadComplaintsData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        authManage = null;
    }
}