package com.example.project_uts.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.project_uts.LoginActivity;
import com.example.project_uts.R;
import com.example.project_uts.models.User;
import com.example.project_uts.network.AuthManage;

public class ProfilFragment extends Fragment {

    private TextView tvNama, tvEmail, tvRole, tvUsername;
    private Button btnLogout;
    private Switch switchDarkMode;

    private static final String PREFS_NAME = "app_settings";
    private static final String THEME_PREF_KEY = "dark_mode_enabled";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil, container, false);

        tvNama = view.findViewById(R.id.tvNama);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchDarkMode = view.findViewById(R.id.switch_dark_mode);

        loadUserData();
        setupDarkModeSwitch();
        setupLogout();

        return view;
    }

    private void loadUserData() {
        if (!isAdded()) return;

        AuthManage authManage = new AuthManage(requireContext());
        User user = authManage.getUser();

        if (user != null) {
            tvNama.setText(user.getFull_name());
            tvEmail.setText(user.getEmail());
            tvRole.setText(user.getRole());
            tvUsername.setText(user.getUsername());

            setRoleColor(user.getRole());

            SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("nama", user.getFull_name());
            editor.putString("email", user.getEmail());
            editor.putString("role", user.getRole());
            editor.putString("username", user.getUsername());
            editor.apply();
        } else {
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
            String nama = preferences.getString("nama", "Nama User");
            String email = preferences.getString("email", "email@example.com");
            String role = preferences.getString("role", "customer");
            String username = preferences.getString("username", "user");

            tvNama.setText(nama);
            tvEmail.setText(email);
            tvRole.setText(role);
            tvUsername.setText(username);
            setRoleColor(role);
        }
    }

    private void setRoleColor(String role) {
        int colorRes = R.color.primary_color;

        if (role != null) {
            switch (role.toLowerCase()) {
                case "customer":
                    colorRes = R.color.primary_color;
                    break;
                case "teknisi":
                    colorRes = R.color.green;
                    break;
                case "admin":
                    colorRes = R.color.red;
                    break;
            }
        }

        try {
            tvRole.setTextColor(getResources().getColor(colorRes));
        } catch (Exception e) {
            tvRole.setTextColor(getResources().getColor(R.color.primary_color));
        }
    }

    private void setupDarkModeSwitch() {
        // Set initial state berdasarkan mode saat ini
        updateSwitchState();

        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // LOG
                android.util.Log.d("DARK_MODE_DEBUG", "Switch changed to: " + isChecked);

                // Simpan preference
                SharedPreferences.Editor editor = requireContext()
                        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                        .edit();
                editor.putBoolean(THEME_PREF_KEY, isChecked);
                editor.apply();

                // LOG saved value
                android.util.Log.d("DARK_MODE_DEBUG", "Saved to prefs: " + isChecked);

                // Apply theme change
                int newMode = isChecked ?
                        AppCompatDelegate.MODE_NIGHT_YES :
                        AppCompatDelegate.MODE_NIGHT_NO;

                AppCompatDelegate.setDefaultNightMode(newMode);

                // LOG mode applied
                android.util.Log.d("DARK_MODE_DEBUG", "Applied mode: " + newMode);

                // Force recreate activity
                requireActivity().recreate();
            }
        });
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            AuthManage authManage = new AuthManage(requireContext());
            authManage.logout(requireContext());

            SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);
            preferences.edit().clear().apply();

            SharedPreferences appSettings = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            appSettings.edit().clear().apply();

            Toast.makeText(requireActivity(), "Logout berhasil", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
    }

    private void updateSwitchState() {
        // Deteksi mode saat ini
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isCurrentlyDarkMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        // LOG UNTUK DEBUG
        android.util.Log.d("DARK_MODE_DEBUG",
                "Current UI Mode: " + currentNightMode +
                        ", Is Dark: " + isCurrentlyDarkMode);

        switchDarkMode.setChecked(isCurrentlyDarkMode);

        // Simpan ke preferences
        SharedPreferences.Editor editor = requireContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit();
        editor.putBoolean(THEME_PREF_KEY, isCurrentlyDarkMode);
        editor.apply();
    }
}