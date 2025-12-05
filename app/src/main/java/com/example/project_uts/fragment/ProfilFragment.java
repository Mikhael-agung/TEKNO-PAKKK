package com.example.project_uts.fragment;

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

    private TextView tvNama, tvEmail, tvRole, tvUsername;
    private Button btnLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        tvNama = view.findViewById(R.id.tvNama);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        tvUsername = view.findViewById(R.id.tvUsername);
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
        String username = preferences.getString("username", "user");

        tvNama.setText(nama);
        tvEmail.setText(email);
        tvRole.setText(role);
        tvUsername.setText(username);

        // Set role color based on role
        setRoleColor(role);
    }

    private void setRoleColor(String role) {
        int colorRes = R.color.primary_color; // default

        switch (role) {
            case "customer":
                colorRes = R.color.primary_color; // blue
                break;
            case "teknisi":
                colorRes = R.color.green; // green
                break;
            case "admin":
                colorRes = R.color.red; // red
                break;
        }

        // You'll need to define these colors in colors.xml
        tvRole.setTextColor(getResources().getColor(colorRes));
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
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();

            Toast.makeText(requireActivity(), "Logout berhasil", Toast.LENGTH_SHORT).show();
        });
    }
}