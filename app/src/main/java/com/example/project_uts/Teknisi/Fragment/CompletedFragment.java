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
import com.example.project_uts.Teknisi.Adapter.CompletedAdapter;
import com.example.project_uts.Teknisi.Model.Komplain;
import com.example.project_uts.Teknisi.Model.TeknisiComplaintsResponse;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompletedFragment extends Fragment {

    private RecyclerView rvCompleted;
    private CompletedAdapter adapter;
    private List<Komplain> completedList = new ArrayList<>();
    private ApiService apiService;

    public CompletedFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_teknisi, container, false);

        apiService = ApiClient.getApiService();

        rvCompleted = view.findViewById(R.id.rvCompleted);
        rvCompleted.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CompletedAdapter(completedList);
        rvCompleted.setAdapter(adapter);

        // load data awal
        reloadComplaints();

        return view;
    }

    // 👉 method public supaya bisa dipanggil dari MainActivity kalau perlu refresh
    public void reloadComplaints() {
        Call<ApiResponse<TeknisiComplaintsResponse>> call =
                apiService.getCompletedComplaints(1, 50); // page=1, limit=50

        call.enqueue(new Callback<ApiResponse<TeknisiComplaintsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeknisiComplaintsResponse>> call,
                                   Response<ApiResponse<TeknisiComplaintsResponse>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TeknisiComplaintsResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        TeknisiComplaintsResponse data = apiResponse.getData();

                        completedList.clear();
                        if (data != null && data.getComplaints() != null && !data.getComplaints().isEmpty()) {
                            completedList.addAll(data.getComplaints());
                            Log.d("CompletedFragment", "Loaded " + data.getComplaints().size() + " complaints");
                        } else {
                            Toast.makeText(requireContext(),
                                    "Belum ada komplain yang selesai",
                                    Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e("CompletedFragment", "API Error: " + apiResponse.getMessage());
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Response error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("CompletedFragment", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeknisiComplaintsResponse>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("CompletedFragment", "Network error: ", t);
                }
            }
        });
    }
}
