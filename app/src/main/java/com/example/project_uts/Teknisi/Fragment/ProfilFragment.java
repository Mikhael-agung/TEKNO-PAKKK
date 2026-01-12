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
import com.example.project_uts.network.AuthManage; // ⚠️ IMPORT INI

public class ProfilFragment extends Fragment {

    private TextView tvNama, tvEmail, tvRole;
    private Button btnLogout;
    private AuthManage authManage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profil_teknisi, container, false);

        tvNama = view.findViewById(R.id.tvNama);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvRole = view.findViewById(R.id.tvRole);
        btnLogout = view.findViewById(R.id.btnLogout);

        authManage = new AuthManage(requireContext());

        loadUserData();
        setupLogout();

        return view;
    }

    private void loadUserData() {
        // ⚠️ GUNAKAN AuthManage, BUKAN SHAREDPREFERENCES LANGSUNG
        com.example.project_uts.models.User user = authManage.getUser();

        if (user != null) {
            tvNama.setText(user.getFull_name() != null ? user.getFull_name() : "Nama User");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "email@example.com");
            tvRole.setText(user.getRole() != null ? user.getRole() : "teknisi");
        } else {
            // Fallback ke SharedPreferences lama (untuk kompatibilitas)
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_pref", Context.MODE_PRIVATE);

            String nama = preferences.getString("nama", "Nama User");
            String email = preferences.getString("email", "email@example.com");
            String role = preferences.getString("role", "teknisi");

            tvNama.setText(nama);
            tvEmail.setText(email);
            tvRole.setText(role);
        }
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            // ⚠️ **PASS EXTRA KE LoginActivity**
            authManage.logout(requireActivity());

            // Optional: Tambahkan intent extra
            Intent intent = new Intent(requireActivity(), LoginActivity.class);
            intent.putExtra("from_logout", true);
            intent.putExtra("force_login", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);
            requireActivity().finishAffinity();

            Toast.makeText(requireActivity(), "Logout berhasil", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data jika perlu
        loadUserData();
    }
}