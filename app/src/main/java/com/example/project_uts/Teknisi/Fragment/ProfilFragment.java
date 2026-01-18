package com.example.project_uts.Teknisi.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.project_uts.R;
import com.example.project_uts.models.User;
import com.example.project_uts.network.AuthManage;

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

        // Init AuthManage
        authManage = new AuthManage(requireContext());

        // Load user data dari AuthManage
        loadUserData();

        // Setup tombol logout
        setupLogout();

        return view;
    }

    private void loadUserData() {
        User user = authManage.getUser();

        if (user != null) {
            tvNama.setText(user.getFull_name() != null ? user.getFull_name() : "Nama User");
            tvEmail.setText(user.getEmail() != null ? user.getEmail() : "email@example.com");
            tvRole.setText(user.getRole() != null ? user.getRole() : "customer");
        } else {
            tvNama.setText("Nama User");
            tvEmail.setText("email@example.com");
            tvRole.setText("customer");
        }
    }

    private void setupLogout() {
        btnLogout.setOnClickListener(v -> {
            // Panggil AuthManage.logout biar semua prefs + token kehapus
            authManage.logout(requireContext());

            Toast.makeText(requireActivity(), "Logout berhasil", Toast.LENGTH_SHORT).show();
        });
    }
}
