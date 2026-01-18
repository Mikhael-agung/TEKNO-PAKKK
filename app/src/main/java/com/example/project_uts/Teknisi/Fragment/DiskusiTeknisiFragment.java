package com.example.project_uts.Teknisi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.DiskusiAdapter;
import com.example.project_uts.Teknisi.Model.DiskusiTeknisi;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.models.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiskusiTeknisiFragment extends Fragment {

    private RecyclerView rvDiskusi;
    private LinearLayout layoutEmpty;
    private String userRole;
    private DiskusiAdapter adapter;
    private ApiService apiService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diskusi_teknisi, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        userRole = preferences.getString("role", "customer");

        initViews(view);
        setupRecyclerView();

        // Inisialisasi ApiClient dengan context
        ApiClient.init(requireContext());
        apiService = ApiClient.getApiService();

        if ("teknisi".equals(userRole)) {
            fetchDiskusiData();
        } else {
            Toast.makeText(getContext(), "Hanya teknisi yang bisa melihat diskusi", Toast.LENGTH_SHORT).show();
            showEmptyState();
        }

        return view;
    }


    private void initViews(View view) {
        rvDiskusi = view.findViewById(R.id.rvDiskusi);
        layoutEmpty = view.findViewById(R.id.layoutEmptyDiskusi);
    }

    private void setupRecyclerView() {
        rvDiskusi.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DiskusiAdapter(diskusi -> openBantuToTechnician(diskusi));
        rvDiskusi.setAdapter(adapter);
    }

    private void fetchDiskusiData() {
        apiService.getAllDiskusi().enqueue(new Callback<ApiResponse<List<DiskusiTeknisi>>>() {
            @Override
            public void onResponse(@NonNull Call<ApiResponse<List<DiskusiTeknisi>>> call,
                                   @NonNull Response<ApiResponse<List<DiskusiTeknisi>>> response) {
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null) {
                    List<DiskusiTeknisi> diskusiList = response.body().getData();
                    if (diskusiList != null && !diskusiList.isEmpty()) {
                        adapter.setData(diskusiList);
                        showListState();
                    } else {
                        showEmptyState();
                    }
                } else {
                    showEmptyState();
                    Toast.makeText(getContext(), "Gagal memuat diskusi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResponse<List<DiskusiTeknisi>>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Log.e("DiskusiFragment", "Error: " + t.getMessage());
                showEmptyState();
                Toast.makeText(getContext(), "Terjadi kesalahan jaringan", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEmptyState() {
        layoutEmpty.setVisibility(View.VISIBLE);
        rvDiskusi.setVisibility(View.GONE);
    }

    private void showListState() {
        layoutEmpty.setVisibility(View.GONE);
        rvDiskusi.setVisibility(View.VISIBLE);
    }

    private void openBantuToTechnician(DiskusiTeknisi diskusi) {
        try {
            String phoneNumber = null;
            String teknisiName = null;
            String judul = null;
            String pelapor = null;

            if (diskusi.getTechnician() != null) {
                phoneNumber = diskusi.getTechnician().getPhone();
                teknisiName = diskusi.getTechnician().getFull_name();
            }
            if (diskusi.getComplaint() != null) {
                judul = diskusi.getComplaint().getJudul();
                if (diskusi.getComplaint().getUser() != null) {
                    pelapor = diskusi.getComplaint().getUser().getFull_name();
                }
            }

            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                Toast.makeText(getContext(), "Nomor teknisi tidak tersedia", Toast.LENGTH_SHORT).show();
                return;
            }

            String message = "Halo " + (teknisiName != null ? teknisiName : "Teknisi") + "! 👋\n\n" +
                    "Saya lihat kamu minta bantuan untuk komplain:\n\n" +
                    "📋 *" + (judul != null ? judul : "-") + "*\n" +
                    "👤 Pelapor: " + (pelapor != null ? pelapor : "-") + "\n\n" +
                    "Ada yang bisa saya bantu?";

            String formattedNumber = phoneNumber.replaceAll("[^0-9]", "");
            String url = "https://wa.me/" + formattedNumber + "?text=" + Uri.encode(message);

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);

        } catch (Exception e) {
            Toast.makeText(getContext(), "WhatsApp tidak terinstall", Toast.LENGTH_SHORT).show();
        }
    }
}
