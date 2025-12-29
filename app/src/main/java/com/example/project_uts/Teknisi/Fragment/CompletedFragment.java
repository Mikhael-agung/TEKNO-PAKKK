package com.example.project_uts.Teknisi.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.KomplainAdapter;
import com.example.project_uts.Teknisi.Model.Komplain;
import com.example.project_uts.Teknisi.Model.TeknisiComplaintsResponse;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedFragment extends Fragment {

    private RecyclerView rvCompleted;
    private KomplainAdapter adapter;
    private List<Komplain> completedList = new ArrayList<>();
    private ApiService apiService;

    public CompletedFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_teknisi, container, false);

        // INIT API SERVICE
        apiService = ApiClient.getApiService();

        rvCompleted = view.findViewById(R.id.rvCompleted);
        rvCompleted.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new KomplainAdapter(getContext(), completedList);
        rvCompleted.setAdapter(adapter);

        fetchCompletedComplaints();

        return view;
    }

    private void fetchCompletedComplaints() {
        Call<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> call =
                apiService.getCompletedComplaints(1, 50);

        call.enqueue(new Callback<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>>() {
            @Override
            public void onResponse(Call<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> call,
                                   Response<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> response) {

                if (!isAdded() || getContext() == null) {
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        TeknisiComplaintsResponse data = response.body().getData();

                        if (data != null && data.getComplaints() != null) {
                            completedList.clear();
                            completedList.addAll(data.getComplaints());
                            adapter.notifyDataSetChanged();

                            Log.d("CompletedFragment", "Loaded " + data.getComplaints().size() + " complaints");
                        }

                        if (data == null || data.getComplaints() == null || data.getComplaints().isEmpty()) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(),
                                                "Tidak ada komplain yang selesai",
                                                Toast.LENGTH_SHORT).show()
                                );
                            }
                        }
                    } else {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(),
                                            "Error: " + response.body().getMessage(),
                                            Toast.LENGTH_SHORT).show()
                            );
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> call, Throwable t) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getActivity(),
                                    "Network error: " + t.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}