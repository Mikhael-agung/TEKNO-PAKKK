package com.example.project_uts.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project_uts.adapter.HistoryAdapter;
import com.example.project_uts.models.Complaint;
import java.util.ArrayList;
import java.util.List;

public class HistoryComplainFragment extends Fragment {

    private RecyclerView rvHistoryComplaints;
    private HistoryAdapter adapter;
    private final List<Complaint> complaints = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_complain, container, false);

        initViews(view);
        setupRecyclerView();
        loadDummyData(); // TODO: Replace with real data from Firebase

        return view;
    }

    private void initViews(View view) {
        rvHistoryComplaints = view.findViewById(R.id.rv_history_complaints);
    }

    private void setupRecyclerView() {
        adapter = new HistoryAdapter(complaints, complaint -> {
            // Handle item click - navigate to detail
            // navigateToComplaintDetail(complaint);
        });

        rvHistoryComplaints.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistoryComplaints.setAdapter(adapter);
    }

    private void loadDummyData() {
        // Dummy data for testing
        complaints.add(new Complaint("TV Rusak", "Elektronik", "15 Jan 2024", "Selesai"));
        complaints.add(new Complaint("AC Bocor", "Perabotan", "14 Jan 2024", "Dalam Proses"));
        complaints.add(new Complaint("Kulkas Tidak Dingin", "Elektronik", "10 Jan 2024", "Ditolak"));

        adapter.notifyDataSetChanged();
    }
}