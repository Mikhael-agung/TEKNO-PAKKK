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

public class CompletedFragment extends Fragment {

    private RecyclerView rvCompleted;
    private KomplainAdapter adapter;
    private List<Komplain> completedList = new ArrayList<>();

    public CompletedFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed, container, false);

        rvCompleted = view.findViewById(R.id.rvCompleted);
        rvCompleted.setLayoutManager(new LinearLayoutManager(getContext()));
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        adapter = new KomplainAdapter(getContext(), completedList, apiService);
        rvCompleted.setAdapter(adapter);

        rvCompleted.setAdapter(adapter);

        fetchCompletedComplaints();

        return view;
    }

    private void fetchCompletedComplaints() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.getComplaints().enqueue(new Callback<List<Komplain>>() {
            @Override
            public void onResponse(Call<List<Komplain>> call, Response<List<Komplain>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    completedList.clear();
                    for (Komplain k : response.body()) {
                        apiService.getComplaintStatuses(k.getId()).enqueue(new Callback<List<ComplaintStatus>>() {
                            @Override
                            public void onResponse(Call<List<ComplaintStatus>> call, Response<List<ComplaintStatus>> resp) {
                                if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                                    ComplaintStatus latest = resp.body().get(0);
                                    if ("Completed".equalsIgnoreCase(latest.getStatus())) {
                                        k.setStatus(latest.getStatus());
                                        completedList.add(k);
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
