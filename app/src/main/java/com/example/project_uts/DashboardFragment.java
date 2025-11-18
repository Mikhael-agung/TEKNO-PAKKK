package com.example.project_uts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
public class DashboardFragment extends Fragment {

    private TextView tvNamaTeknisi, tvCompletedCount, tvPendingCount, tvReadyCount;
    private ImageView ivFotoTeknisi;
    private View rootView; // ← TAMBAH INI

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false); // ← SIMPAN DI VARIABLE

        initViews();
        setupDashboard();

        return rootView;
    }

    private void initViews() {
        // PAKAI rootView, BUKAN parameter view
        tvNamaTeknisi = rootView.findViewById(R.id.tvNamaTeknisi);
        tvCompletedCount = rootView.findViewById(R.id.tvCompletedCount);
        tvPendingCount = rootView.findViewById(R.id.tvPendingCount);
        tvReadyCount = rootView.findViewById(R.id.tvReadyCount);
        ivFotoTeknisi = rootView.findViewById(R.id.ivFotoTeknisi);
    }

    private void setupDashboard() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
        String nama = preferences.getString("nama", "Teknisi");

        tvNamaTeknisi.setText(nama);
        tvCompletedCount.setText("2");
        tvPendingCount.setText("3");
        tvReadyCount.setText("5");

        // SEKARANG PAKAI rootView
        View guideCard = rootView.findViewById(R.id.guideCard);
        if (guideCard != null) {
            guideCard.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.google.com/search?q=guide+perbaikan+alat+rumah+tangga"));
                startActivity(intent);
            });
        }
    }
}