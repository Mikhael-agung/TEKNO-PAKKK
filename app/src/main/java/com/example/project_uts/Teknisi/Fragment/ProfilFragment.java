package com.example.project_uts.Teknisi.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.project_uts.LoginActivity;
import com.example.project_uts.R;

public class ProfilFragment extends Fragment {

    private TextView tvNama, tvEmail, tvRole;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil_teknisi, container, false);

        tvNama = view.findViewById(R.id.tvNama);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);

        loadUserData();
        setupLogout();

        return view;
    }

    private void loadUserData() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);

        String nama = preferences.getString("nama", "Nama User");
        String email = preferences.getString("email", "email@example.com");
        String role = preferences.getString("role", "customer");

        tvNama.setText(nama);
        tvEmail.setText(email);
        tvRole.setText(role);
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            // Clear shared preferences
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            // Redirect to Login Activity
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();

            Toast.makeText(requireActivity(), "Logout berhasil", Toast.LENGTH_SHORT).show();
        });
    }
}