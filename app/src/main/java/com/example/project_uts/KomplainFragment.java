package com.example.project_uts;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomplainFragment extends Fragment {

    private RecyclerView rvKomplain;
    private KomplainAdapter adapter;
    private List<Komplain> komplainList = new ArrayList<>();

    public KomplainFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_komplain, container, false);

        rvKomplain = view.findViewById(R.id.rvKomplain);
        rvKomplain.setLayoutManager(new LinearLayoutManager(getContext()));
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        adapter = new KomplainAdapter(getContext(), komplainList, apiService);
        rvKomplain.setAdapter(adapter);

        fetchKomplainOnly();

        return view;
    }

    private void fetchKomplainOnly() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getComplaints().enqueue(new Callback<List<Komplain>>() {
            @Override
            public void onResponse(Call<List<Komplain>> call, Response<List<Komplain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    komplainList.clear();

                    for (Komplain k : response.body()) {
                        // Ambil status terbaru untuk setiap complaint
                        apiService.getComplaintStatuses(k.getId()).enqueue(new Callback<List<ComplaintStatus>>() {
                            @Override
                            public void onResponse(Call<List<ComplaintStatus>> call, Response<List<ComplaintStatus>> resp) {
                                if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                                    ComplaintStatus latest = resp.body().get(0); // pastikan API urutkan DESC by created_at
                                    Log.d("DEBUG", "Complaint ID: " + k.getId() + " Latest status: " + latest.getStatus());

                                    // Filter hanya status "Komplain"
                                    if ("Komplain".equalsIgnoreCase(latest.getStatus())) {
                                        k.setStatus(latest.getStatus());
                                        komplainList.add(k);
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
