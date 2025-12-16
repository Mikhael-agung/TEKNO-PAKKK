package com.example.project_uts.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.project_uts.R;
import com.example.project_uts.models.Complaint;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CustomerFragment extends Fragment {

    private EditText etJudul, etDeskripsi;
    private Spinner spKategori;
    private ImageView ivFoto;
    private Button btnPilihFoto, btnSubmit;
    private FloatingActionButton fabBack;
    private Uri fotoUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_complain, container, false);

        initViews(view);
        setupKategoriSpinner();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        etJudul = view.findViewById(R.id.et_judul);
        etDeskripsi = view.findViewById(R.id.et_deskripsi);
        spKategori = view.findViewById(R.id.sp_kategori);
        ivFoto = view.findViewById(R.id.iv_foto);
        btnPilihFoto = view.findViewById(R.id.btn_pilih_foto);
        btnSubmit = view.findViewById(R.id.btn_submit);
        fabBack = view.findViewById(R.id.fab_back);
    }

    private void setupKategoriSpinner() {
        String[] kategori = {
                "Pilih Kategori",
                "Elektronik (TV, Laptop, dll)",
                "AC & Pendingin",
                "Kulkas & Freezer",
                "Mesin Cuci",
                "Perbaikan Pipa & Air",
                "Listrik & Lampu",
                "Perabotan Rumah",
                "Internet & Jaringan",
                "Smartphone & Tablet",
                "Lainnya"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                kategori
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);
    }

    private void setupClickListeners() {
        // Back button - SOLUSI REKOMENDASI
        fabBack.setOnClickListener(v -> {
            navigateToDashboard();
        });

        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnSubmit.setOnClickListener(v -> submitKomplain());
    }

    /**
     * NAVIGASI KE DASHBOARD + UPDATE BOTTOM NAV
     */
    private void navigateToDashboard() {
        try {
            // 1. Update bottom navigation selected item
            BottomNavigationView bottomNav = requireActivity().findViewById(R.id.bottom_navigation);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(R.id.nav_dashboard);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // Fallback jika bottom nav tidak ditemukan
            requireActivity().onBackPressed();
        }
    }

    private void pilihFoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            fotoUri = data.getData();
            ivFoto.setImageURI(fotoUri);
            ivFoto.setVisibility(View.VISIBLE);
        }
    }

    private void submitKomplain() {
        String judul = etJudul.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();
        String kategori = spKategori.getSelectedItem().toString();

        // Validasi
        if (judul.isEmpty()) {
            etJudul.setError("Judul komplain harus diisi");
            return;
        }

        if (deskripsi.isEmpty()) {
            etDeskripsi.setError("Deskripsi masalah harus diisi");
            return;
        }

        if (kategori.equals("Pilih Kategori")) {
            Toast.makeText(requireContext(), "Pilih kategori komplain", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Ambil user_id dari AuthManager
        AuthManage authManage = new AuthManage(requireContext());
        String userId = authManage.getUserId();

        if (userId == null) {
            Toast.makeText(requireContext(), "User tidak ditemukan, silakan login ulang", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Disable button selama proses
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Mengirim...");

        // 3. Siapkan data untuk API
        Map<String, String> complaintData = new HashMap<>();
        complaintData.put("judul", judul);
        complaintData.put("kategori", kategori);
        complaintData.put("deskripsi", deskripsi);
        complaintData.put("user_id", userId); // PENTING: Kirim user_id

        // 4. Jika ada foto, perlu multipart request (nanti)
        // Untuk sekarang, tanpa foto dulu

        // 5. Panggil API
        ApiService apiService = ApiClient.getApiService();
        Call<com.example.project_uts.models.ApiResponse<Complaint>> call = apiService.createComplaint(complaintData);

        call.enqueue(new Callback<com.example.project_uts.models.ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<com.example.project_uts.models.ApiResponse<Complaint>> call,
                                   Response<com.example.project_uts.models.ApiResponse<Complaint>> response) {

                btnSubmit.setEnabled(true);
                btnSubmit.setText("Kirim Komplain");

                if (response.isSuccessful() && response.body() != null) {
                    com.example.project_uts.models.ApiResponse<Complaint> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        showSuccessDialog(judul, kategori);
                    } else {
                        showErrorDialog("Gagal: " + apiResponse.getMessage());
                    }
                } else {
                    String errorMsg = "Error: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    showErrorDialog(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<com.example.project_uts.models.ApiResponse<Complaint>> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Kirim Komplain");
                showErrorDialog("Koneksi gagal: " + t.getMessage());
            }
        });
    }

    /**
     * Tampilkan dialog sukses
     */
    private void showSuccessDialog(String judul, String kategori) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.success_dialog);
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText("Komplain \"" + judul + "\" berhasil dikirim!\n\nTeknisi akan segera menghubungi Anda via WhatsApp.");

        Button btnOk = dialog.findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            resetForm();
            navigateToDashboard(); // KE DASHBOARD + UPDATE NAV
        });

        dialog.show();
    }

    /**
     * Tampilkan dialog error
     */
    private void showErrorDialog(String message) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.error_dialog);
        dialog.setCancelable(false);

        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        tvMessage.setText(message); // Pakai parameter message

        Button btnTryAgain = dialog.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(v -> {
            dialog.dismiss();
            submitKomplain(); // Coba lagi
        });

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void resetForm() {
        etJudul.setText("");
        etDeskripsi.setText("");
        spKategori.setSelection(0);
        ivFoto.setVisibility(View.GONE);
        fotoUri = null;
    }
}