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
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.models.User;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;

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
    private String namaTeknisi = "Teknisi"; // default

    public KomplainFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_komplain_teknisi, container, false);

        // INIT API SERVICE TEKNISI
        apiService = ApiClient.getApiService();

        // Ambil user dari AuthManage (lebih aman daripada decode JWT manual)
        AuthManage authManage = new AuthManage(requireContext());
        User user = authManage.getUser();

        if (user != null && user.getFull_name() != null) {
            namaTeknisi = user.getFull_name();
        }
        Log.d("KomplainFragment", "Nama teknisi final: " + namaTeknisi);

        rvKomplain = view.findViewById(R.id.rvKomplain);
        rvKomplain.setLayoutManager(new LinearLayoutManager(getContext()));

        // Adapter sekarang pakai nama teknisi dari AuthManage
        adapter = new KomplainAdapter(getContext(), komplainList, namaTeknisi);
        rvKomplain.setAdapter(adapter);

        fetchReadyComplaints();

        return view;
    }

    private void fetchReadyComplaints() {
        Call<ApiResponse<TeknisiComplaintsResponse>> call =
                apiService.getReadyComplaints(1, 50); // page 1, limit 50

        call.enqueue(new Callback<ApiResponse<TeknisiComplaintsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeknisiComplaintsResponse>> call,
                                   Response<ApiResponse<TeknisiComplaintsResponse>> response) {

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
                            Toast.makeText(getContext(),
                                    "Tidak ada komplain yang siap ditangani",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(),
                                "Error: " + response.body().getMessage(),
                                Toast.LENGTH_SHORT).show();
                        Log.e("KomplainFragment", "API Error: " + response.body().getMessage());
                    }
                } else {
                    Toast.makeText(getContext(),
                            "Response error: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    Log.e("API_ERROR", "Response code: " + response.code());

                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "null";
                        Log.e("API_ERROR", "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Cannot read error body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeknisiComplaintsResponse>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Network error: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                Log.e("API_ERROR", "Network error: ", t);
            }
        });
    }
}
