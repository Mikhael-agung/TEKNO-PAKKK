package com.example.project_uts.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.project_uts.R;
import com.example.project_uts.activity.MainActivity;
import com.google.android.material.button.MaterialButton;

public class DashboardCustomerFragment extends Fragment {

    private TextView tvWelcome, tvEmail, tvPendingCount, tvProgressCount, tvCompletedCount;
    private MaterialButton btnBuatKomplain;
    private CardView cardHome, cardHistory, cardProfile;

    public DashboardCustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_dasboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupClickListeners();
        loadUserData();
        loadDashboardStats();
    }

    private void initViews(View view) {
        // TextViews
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvEmail = view.findViewById(R.id.tv_email); // Pastikan ada di XML
        tvPendingCount = view.findViewById(R.id.tv_pending_count);
        tvProgressCount = view.findViewById(R.id.tv_progress_count);
        tvCompletedCount = view.findViewById(R.id.tv_completed_count);

        // Buttons
        btnBuatKomplain = view.findViewById(R.id.btn_buat_komplain);

        // Quick Action Cards
        cardHome = view.findViewById(R.id.card_home); // Pastikan ada ID di XML
        cardHistory = view.findViewById(R.id.card_history); // Pastikan ada ID di XML
        cardProfile = view.findViewById(R.id.card_profile); // Pastikan ada ID di XML
    }

    private void setupClickListeners() {
        // Button Buat Komplain
        btnBuatKomplain.setOnClickListener(v -> {
            navigateToCustomerFragment();
        });

        // Quick Action - Home (Refresh dashboard)
        cardHome.setOnClickListener(v -> {
            // Refresh data
            loadDashboardStats();
            // Show refresh message
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), "Dashboard diperbarui", android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        // Quick Action - History
        cardHistory.setOnClickListener(v -> {
            navigateToHistory();
        });

        // Quick Action - Profile
        cardProfile.setOnClickListener(v -> {
            navigateToProfile();
        });

        // Statistics Cards juga bisa diklik (optional)
        View pendingCard = getView().findViewById(R.id.card_pending); // Tambah ID di XML
        View progressCard = getView().findViewById(R.id.card_progress); // Tambah ID di XML
        View completedCard = getView().findViewById(R.id.card_completed); // Tambah ID di XML

        if (pendingCard != null) {
            pendingCard.setOnClickListener(v -> navigateToHistoryWithFilter("pending"));
        }
        if (progressCard != null) {
            progressCard.setOnClickListener(v -> navigateToHistoryWithFilter("progress"));
        }
        if (completedCard != null) {
            completedCard.setOnClickListener(v -> navigateToHistoryWithFilter("completed"));
        }
    }

    private void loadUserData() {
        // Load data user dari SharedPreferences
        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
        String userName = prefs.getString("user_name", "Customer");
        String userEmail = prefs.getString("user_email", "user@email.com");

        tvWelcome.setText("Welcome, " + userName + "!");

        // Jika tvEmail ada di layout, set text-nya
        if (tvEmail != null) {
            tvEmail.setText(userEmail);
        }
    }

    private void loadDashboardStats() {
        // TODO: Replace dengan data real dari API/Firebase
        // Untuk sekarang pakai dummy data

        int pendingCount = 3;
        int progressCount = 2;
        int completedCount = 5;

        tvPendingCount.setText(String.valueOf(pendingCount));
        tvProgressCount.setText(String.valueOf(progressCount));
        tvCompletedCount.setText(String.valueOf(completedCount));
    }

    private void navigateToCustomerFragment() {
        // Navigate ke CustomerFragment (Buat Komplain Baru)
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new CustomerFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToHistory() {
        // Navigate ke HistoryFragment
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HistoryComplainFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToProfile() {
        // Navigate ke ProfileFragment
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ProfilFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void navigateToHistoryWithFilter(String filter) {
        // Navigate ke HistoryFragment dengan filter tertentu
        HistoryComplainFragment historyFragment = new HistoryComplainFragment();

        Bundle args = new Bundle();
        args.putString("filter", filter);
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

    // Method untuk refresh data ketika fragment menjadi visible
    @Override
    public void onResume() {
        super.onResume();
        loadDashboardStats(); // Refresh data setiap kali fragment ditampilkan
    }
}