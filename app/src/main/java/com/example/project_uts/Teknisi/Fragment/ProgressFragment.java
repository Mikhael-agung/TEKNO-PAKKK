package com.example.project_uts.Teknisi.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.ApiClient;
import com.example.project_uts.ApiService;
import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.ProgressAdapter;
import com.example.project_uts.Teknisi.Model.ComplaintStatus;
import com.example.project_uts.Teknisi.Model.Komplain;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressFragment extends Fragment {

    private RecyclerView rvProgress;
    private ProgressAdapter adapter;
    private List<Komplain> progressList = new ArrayList<>();

    public ProgressFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_teknisi, container, false);

        rvProgress = view.findViewById(R.id.rvProgress);
        rvProgress.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ProgressAdapter(getContext(), progressList);
        rvProgress.setAdapter(adapter);


        fetchProgressComplaints();

        return view;
    }

    private void fetchProgressComplaints() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getComplaints().enqueue(new Callback<List<Komplain>>() {
            @Override
            public void onResponse(Call<List<Komplain>> call, Response<List<Komplain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressList.clear();
                    for (Komplain k : response.body()) {
                        apiService.getComplaintStatuses(k.getId()).enqueue(new Callback<List<ComplaintStatus>>() {
                            @Override
                            public void onResponse(Call<List<ComplaintStatus>> call, Response<List<ComplaintStatus>> resp) {
                                if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                                    ComplaintStatus latest = resp.body().get(0);

                                    // Filter dua status: On Progress dan Pending
                                    if ("On Progress".equalsIgnoreCase(latest.getStatus()) ||
                                            "Pending".equalsIgnoreCase(latest.getStatus())) {

                                        k.setStatus(latest.getStatus());
                                        progressList.add(k);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<ComplaintStatus>> call, Throwable t) {
                                Log.e("API_ERROR", "Gagal ambil status: " + t.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Komplain>> call, Throwable t) {
                Log.e("API_ERROR", "Gagal ambil complaints: " + t.getMessage());
            }
        });
    }
}
