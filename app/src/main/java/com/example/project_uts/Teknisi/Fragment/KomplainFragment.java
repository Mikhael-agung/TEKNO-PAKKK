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
import com.example.project_uts.Teknisi.Model.TeknisiComplaintsResponse; // IMPORT INI
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KomplainFragment extends Fragment {

    private RecyclerView rvKomplain;
    private KomplainAdapter adapter;
    private List<Komplain> komplainList = new ArrayList<>();
    private ApiService apiService;

    public KomplainFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_komplain_teknisi, container, false);

        // INIT API SERVICE TEKNISI
        apiService = ApiClient.getApiService();

        rvKomplain = view.findViewById(R.id.rvKomplain);
        rvKomplain.setLayoutManager(new LinearLayoutManager(getContext()));

        // UPDATE ADAPTER CONSTRUCTOR
        adapter = new KomplainAdapter(getContext(), komplainList);
        rvKomplain.setAdapter(adapter);

        fetchReadyComplaints();

        return view;
    }

    private void fetchReadyComplaints() {
        Call<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> call =
                apiService.getReadyComplaints(1, 50); // page 1, limit 50

        call.enqueue(new Callback<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>>() {
            @Override
            public void onResponse(Call<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> call,
                                   Response<com.example.project_uts.models.ApiResponse<TeknisiComplaintsResponse>> response) {

                if (!isAdded() || getContext() == null) {
                    Log.e("KomplainFragment", "Fragment not attached, skipping UI update");
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        TeknisiComplaintsResponse data = response.body().getData();

                        if (data != null && data.getComplaints() != null) {
                            komplainList.clear();
                            komplainList.addAll(data.getComplaints());
                            adapter.notifyDataSetChanged();

                            Log.d("KomplainFragment", "Loaded " + data.getComplaints().size() + " complaints");
                            Log.d("KomplainFragment", "Total: " + data.getTotal() +
                                    ", Page: " + data.getPage() +
                                    ", Limit: " + data.getLimit());
                        }

                        if (data == null || data.getComplaints() == null || data.getComplaints().isEmpty()) {
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(() ->
                                        Toast.makeText(getActivity(),
                                                "Tidak ada komplain yang siap ditangani",
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
                        Log.e("KomplainFragment", "API Error: " + response.body().getMessage());
                    }
                } else {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getActivity(),
                                        "Response error: " + response.code(),
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                    Log.e("API_ERROR", "Response code: " + response.code());

                    // DEBUG: Log response error body
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("API_ERROR", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Cannot read error body", e);
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
                Log.e("API_ERROR", "Network error: ", t);
            }
        });
    }
}