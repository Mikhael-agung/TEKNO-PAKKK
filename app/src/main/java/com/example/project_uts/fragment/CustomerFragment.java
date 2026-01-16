package com.example.project_uts.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.cardview.widget.CardView;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CustomerFragment extends Fragment {

    private EditText etJudul, etDeskripsi;
    private Spinner spKategori;
    private ImageView ivFoto;
    private Button btnPilihFoto, btnSubmit;
    private FloatingActionButton fabBack;
    private Uri fotoUri;
    private MaterialButton btnHapusFoto;
    private CardView cardFotoPreview;
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText etAlamat;
    private EditText etKota;
    private EditText etKecamatan;
    private EditText etTelepon;
    private EditText etCatatan;

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // HAPUS SEMUA AppCompatDelegate.setDefaultNightMode DARI SINI!
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        // HAPUS SEMUA AppCompatDelegate.setDefaultNightMode DARI SINI!
    }

    // ... METODE LAINNYA TETAP SAMA ...

    private void initViews(View view) {
        etJudul = view.findViewById(R.id.et_judul);
        etDeskripsi = view.findViewById(R.id.et_deskripsi);
        spKategori = view.findViewById(R.id.sp_kategori);
        ivFoto = view.findViewById(R.id.iv_foto);
        btnPilihFoto = view.findViewById(R.id.btn_pilih_foto);
        btnSubmit = view.findViewById(R.id.btn_submit);
        fabBack = view.findViewById(R.id.fab_back);
        cardFotoPreview = view.findViewById(R.id.card_foto_preview);
        btnHapusFoto = view.findViewById(R.id.btn_hapus_foto);
        etAlamat = view.findViewById(R.id.et_alamat);
        etKota = view.findViewById(R.id.et_kota);
        etKecamatan = view.findViewById(R.id.et_kecamatan);
        etTelepon = view.findViewById(R.id.et_telepon_alamat);
        etCatatan = view.findViewById(R.id.et_catatan_alamat);
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
        fabBack.setOnClickListener(v -> {
            navigateToDashboard();
        });

        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnSubmit.setOnClickListener(v -> submitKomplain());
        btnHapusFoto.setOnClickListener(v -> hapusFoto());
    }

    /**
     * NAVIGASI KE DASHBOARD + UPDATE BOTTOM NAV
     */
    private void navigateToDashboard() {
        try {
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

            if (cardFotoPreview != null) {
                cardFotoPreview.setVisibility(View.VISIBLE);
            }

            Toast.makeText(requireContext(), "Foto berhasil dipilih", Toast.LENGTH_SHORT).show();
        }
    }

    private void submitKomplain() {
        String judul = etJudul.getText().toString().trim();
        String deskripsi = etDeskripsi.getText().toString().trim();
        String kategori = spKategori.getSelectedItem().toString();
        String finalJudul = judul;
        String finalKategori = kategori;

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

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Mengirim...");

        Map<String, String> complaintData = new HashMap<>();
        complaintData.put("judul", judul);
        complaintData.put("kategori", kategori);
        complaintData.put("deskripsi", deskripsi);
        complaintData.put("user_id", userId);
        complaintData.put("alamat", etAlamat.getText().toString());
        complaintData.put("kota", etKota.getText().toString());
        complaintData.put("kecamatan", etKecamatan.getText().toString());
        complaintData.put("telepon_alamat", etTelepon.getText().toString());
        complaintData.put("catatan_alamat", etCatatan.getText().toString());

        ApiService apiService = ApiClient.getApiService();
        Call<com.example.project_uts.models.ApiResponse<Complaint>> call = apiService.createComplaint(complaintData);

        call.enqueue(new Callback<com.example.project_uts.models.ApiResponse<Complaint>>() {
            @Override
            public void onResponse(Call<com.example.project_uts.models.ApiResponse<Complaint>> call,
                                   Response<com.example.project_uts.models.ApiResponse<Complaint>> response) {

                requireActivity().runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Kirim Komplain");
                });

                if (response.isSuccessful() && response.body() != null) {
                    com.example.project_uts.models.ApiResponse<Complaint> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Complaint complaint = apiResponse.getData();
                        Log.d("CREATE_COMPLAINT", "Success! ID: " + complaint.getId());

                        requireActivity().runOnUiThread(() -> {
                            showSuccessDialog(complaint.getJudul(), complaint.getKategori());
                        });

                    } else {
                        showErrorDialog("Gagal: " + apiResponse.getMessage());
                    }
                } else {
                    String tempErrorMsg = "Error: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            tempErrorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    final String finalErrorMsg = tempErrorMsg;
                    requireActivity().runOnUiThread(() -> {
                        showErrorDialog(finalErrorMsg);
                    });
                }
            }

            @Override
            public void onFailure(Call<com.example.project_uts.models.ApiResponse<Complaint>> call, Throwable t) {
                final String failureMsg = "Koneksi gagal: " + t.getMessage();
                requireActivity().runOnUiThread(() -> {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Kirim Komplain");
                    showErrorDialog(failureMsg);
                });
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
            navigateToDashboard();
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
        tvMessage.setText(message);

        Button btnTryAgain = dialog.findViewById(R.id.btn_try_again);
        btnTryAgain.setOnClickListener(v -> {
            dialog.dismiss();
            submitKomplain();
        });

        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    /**
     * Hapus foto yang sudah dipilih
     */
    private void hapusFoto() {
        if (cardFotoPreview != null) {
            cardFotoPreview.setVisibility(View.GONE);
        }
        ivFoto.setImageDrawable(null);
        fotoUri = null;
        Toast.makeText(requireContext(), "Foto dihapus", Toast.LENGTH_SHORT).show();
    }

    private void resetForm() {
        etJudul.setText("");
        etDeskripsi.setText("");
        spKategori.setSelection(0);
        ivFoto.setVisibility(View.GONE);
        fotoUri = null;
        if (cardFotoPreview != null) {
            cardFotoPreview.setVisibility(View.GONE);
        }
    }
}