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
import com.example.project_uts.Teknisi.Adapter.ProgressAdapter;
import com.example.project_uts.Teknisi.Model.Komplain;
import com.example.project_uts.Teknisi.Model.TeknisiComplaintsResponse;
import com.example.project_uts.Teknisi.Model.DiskusiTeknisi;
import com.example.project_uts.models.ApiResponse;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressFragment extends Fragment {

    private RecyclerView rvProgress;
    private ProgressAdapter adapter;
    private List<Komplain> progressList = new ArrayList<>();
    private ApiService apiService;
    private String namaTeknisi = "Teknisi"; // default

    public ProgressFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_teknisi, container, false);

        apiService = ApiClient.getApiService();

        // Ambil user dari AuthManage
        AuthManage authManage = new AuthManage(requireContext());
        com.example.project_uts.models.User user = authManage.getUser();

        if (user != null && user.getFull_name() != null) {
            namaTeknisi = user.getFull_name();
        }
        Log.d("ProgressFragment", "Nama teknisi final: " + namaTeknisi);

        rvProgress = view.findViewById(R.id.rvProgress);
        rvProgress.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Adapter dengan listener tombol Minta Bantuan
        adapter = new ProgressAdapter(requireContext(), progressList, namaTeknisi, komplain -> {
            addDiskusi(komplain.getId()); // panggil API POST diskusi
        });
        rvProgress.setAdapter(adapter);

        // load awal
        reloadComplaints();

        return view;
    }

    // 👉 method public supaya bisa dipanggil dari MainActivity
    public void reloadComplaints() {
        Call<ApiResponse<TeknisiComplaintsResponse>> call =
                apiService.getProgressComplaints(1, 50);

        call.enqueue(new Callback<ApiResponse<TeknisiComplaintsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TeknisiComplaintsResponse>> call,
                                   Response<ApiResponse<TeknisiComplaintsResponse>> response) {

                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TeknisiComplaintsResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        TeknisiComplaintsResponse data = apiResponse.getData();

                        progressList.clear(); // selalu clear dulu
                        if (data != null && data.getComplaints() != null && !data.getComplaints().isEmpty()) {
                            progressList.addAll(data.getComplaints());
                            Log.d("ProgressFragment", "Loaded " + data.getComplaints().size() + " complaints");
                        } else {
                            Toast.makeText(requireContext(),
                                    "Tidak ada komplain dalam progress",
                                    Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error: " + apiResponse.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(),
                            "Response tidak valid",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TeknisiComplaintsResponse>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Network error: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 👉 method untuk POST diskusi teknisi
    private void addDiskusi(String complaintId) {
        DiskusiTeknisi request = new DiskusiTeknisi();
        request.setComplaintId(complaintId); // pastikan ada setter di model

        Call<ApiResponse<DiskusiTeknisi>> call = apiService.addDiskusi(request);

        call.enqueue(new Callback<ApiResponse<DiskusiTeknisi>>() {
            @Override
            public void onResponse(Call<ApiResponse<DiskusiTeknisi>> call,
                                   Response<ApiResponse<DiskusiTeknisi>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(requireContext(), "Diskusi berhasil ditambahkan", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Gagal menambahkan diskusi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DiskusiTeknisi>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
