package com.example.project_uts.Teknisi.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.project_uts.R;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardTeknisiFragment extends Fragment {

    // UI Components - SESUAI XML 2x2
    private TextView tvNamaTeknisi;
    private TextView tvReadyCount, tvProgressCount, tvPendingCount, tvCompletedCount;
    private TextView tvTotalComplaints, tvActiveComplaints, tvAssignedCount, tvTodayTasks;

    // Services
    private ApiService apiService;
    private AuthManage authManage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard_teknisi, container, false);

        // Initialize services
        apiService = ApiClient.getApiService();
        authManage = new AuthManage(requireContext());

        // Bind all views
        bindViews(view);

        // Set user data
        setUserData();

        // Load dashboard stats
        loadDashboardStats();

        // Setup click listeners
        setupClickListeners(view);

        return view;
    }

    private void bindViews(View view) {
        // 4 Cards - SESUAI XML 2x2
        tvReadyCount = view.findViewById(R.id.tvReadyCount);
        tvProgressCount = view.findViewById(R.id.tvProgressCount);
        tvPendingCount = view.findViewById(R.id.tvPendingCount);
        tvCompletedCount = view.findViewById(R.id.tvCompletedCount);

        // Summary
        tvTotalComplaints = view.findViewById(R.id.tvTotalComplaints);
        tvActiveComplaints = view.findViewById(R.id.tvActiveComplaints);
        tvAssignedCount = view.findViewById(R.id.tvAssignedCount);
        tvTodayTasks = view.findViewById(R.id.tvTodayTasks);

        // User info
        tvNamaTeknisi = view.findViewById(R.id.tvNamaTeknisi);
    }

    private void setUserData() {
        com.example.project_uts.models.User user = authManage.getUser();
        if (user != null && user.getFull_name() != null) {
            tvNamaTeknisi.setText(user.getFull_name());
        } else {
            tvNamaTeknisi.setText("Teknisi");
        }
    }

    private void loadDashboardStats() {
        Call<com.example.project_uts.models.ApiResponse<Map<String, Object>>> call =
                apiService.getDashboardStats();

        call.enqueue(new Callback<com.example.project_uts.models.ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<com.example.project_uts.models.ApiResponse<Map<String, Object>>> call,
                                   Response<com.example.project_uts.models.ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        Map<String, Object> data = response.body().getData();
                        updateDashboardUI(data);
                        Log.d("Dashboard", "Stats loaded successfully");
                    } else {
                        showError("Gagal load stats: " + response.body().getMessage());
                    }
                } else {
                    showError("Response error: " + response.code());
                    }
            }

            @Override
            public void onFailure(Call<com.example.project_uts.models.ApiResponse<Map<String, Object>>> call, Throwable t) {
                showError("Network error: " + t.getMessage());
                showPlaceholderData();
            }
        });
    }

    private void updateDashboardUI(Map<String, Object> data) {
        if (data == null) {
            Log.e("Dashboard", "Data is NULL!");
            showPlaceholderData();
            return;
        }

        try {
            // DEBUG: Log semua field
            Log.d("Dashboard", "Backend Response Data: " + data.toString());

            // Extract values dari backend response
            int ready = getIntValue(data, "ready_count");
            int progress = getIntValue(data, "progress_count");
            int completed = getIntValue(data, "completed_count");
            int pending = getIntValue(data, "pending_count");
            int assigned = getIntValue(data, "total_assigned");

            // Calculate totals
            int totalComplaints = ready + progress + completed + pending;
            int activeComplaints = ready + progress + pending; // completed tidak aktif

            Log.d("Dashboard", String.format(
                    "Stats - Ready: %d, Progress: %d, Completed: %d, Pending: %d, Total: %d, Assigned: %d",
                    ready, progress, completed, pending, totalComplaints, assigned
            ));

            // Update 4 Cards - SEPARATE, TIDAK DIGABUNG!
            tvReadyCount.setText(String.valueOf(ready));
            tvProgressCount.setText(String.valueOf(progress));   // progress_count
            tvPendingCount.setText(String.valueOf(pending));     // pending_count
            tvCompletedCount.setText(String.valueOf(completed));

            // Update Summary
            tvTotalComplaints.setText(String.valueOf(totalComplaints));
            tvActiveComplaints.setText(String.valueOf(activeComplaints));
            tvAssignedCount.setText(String.valueOf(assigned));

            // Update today's tasks
            updateTodayTasks(activeComplaints);

        } catch (Exception e) {
            Log.e("Dashboard", "Error updating UI", e);
            showPlaceholderData();
        }
    }

    private int getIntValue(Map<String, Object> data, String key) {
        if (data.containsKey(key)) {
            Object value = data.get(key);
            if (value instanceof Number) {
                return ((Number) value).intValue();
            } else if (value instanceof String) {
                try {
                    return Integer.parseInt((String) value);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }
        return 0;
    }

    private void updateTodayTasks(int activeTasks) {
        if (activeTasks == 0) {
            tvTodayTasks.setText("Tidak ada tugas aktif untuk hari ini");
        } else if (activeTasks == 1) {
            tvTodayTasks.setText("Ada 1 tugas yang perlu diselesaikan hari ini");
        } else {
            tvTodayTasks.setText("Ada " + activeTasks + " tugas yang perlu diselesaikan hari ini");
        }
    }

    private void showPlaceholderData() {
        tvReadyCount.setText("0");
        tvProgressCount.setText("0");
        tvPendingCount.setText("0");
        tvCompletedCount.setText("0");
        tvTotalComplaints.setText("0");
        tvActiveComplaints.setText("0");
        tvAssignedCount.setText("0");
        tvTodayTasks.setText("Tidak ada data statistik");
    }

    private void showError(String message) {
        Log.e("Dashboard", message);
        if (getActivity() != null) {
            getActivity().runOnUiThread(() ->
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void setupClickListeners(View view) {
        View guideCard = view.findViewById(R.id.guideCard);
        if (guideCard != null) {
            guideCard.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Guide akan datang", Toast.LENGTH_SHORT).show();
            });
        }

        View todayTaskCard = view.findViewById(R.id.todayTaskCard);
        if (todayTaskCard != null) {
            todayTaskCard.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Lihat detail tugas", Toast.LENGTH_SHORT).show();
            });
        }
    }
}