package com.example.project_uts;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CustomerFragment extends Fragment {

    private EditText etJudul, etDeskripsi;
    private Spinner spKategori;
    private ImageView ivFoto;
    private Button btnPilihFoto, btnSubmit;
    private Uri fotoUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    public CustomerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_customer_complain, container, false);

        // Inisialisasi view
        etJudul = view.findViewById(R.id.et_judul);
        etDeskripsi = view.findViewById(R.id.et_deskripsi);
        spKategori = view.findViewById(R.id.sp_kategori);
        ivFoto = view.findViewById(R.id.iv_foto);
        btnPilihFoto = view.findViewById(R.id.btn_pilih_foto);
        btnSubmit = view.findViewById(R.id.btn_submit);

        // Setup spinner kategori
        setupKategoriSpinner();

        // Setup click listeners
        btnPilihFoto.setOnClickListener(v -> pilihFoto());
        btnSubmit.setOnClickListener(v -> submitKomplain());

        return view;
    }

    private void setupKategoriSpinner() {
        String[] kategori = {"Pilih Kategori", "Elektronik", "Perabotan", "Kendaraan", "Lainnya"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                kategori
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKategori.setAdapter(adapter);
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
        if (judul.isEmpty() || deskripsi.isEmpty() || kategori.equals("Pilih Kategori")) {
            Toast.makeText(requireContext(), "Harap lengkapi semua field", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Implement API call dengan Multipart
        // Untuk sekarang tampilkan Toast dulu
        Toast.makeText(requireContext(), "Komplain berhasil dikirim!", Toast.LENGTH_SHORT).show();

        // Reset form
        resetForm();

        // Optional: Redirect ke Status Komplain setelah submit
        // ((BottomNavigationView) requireActivity().findViewById(R.id.bottom_navigation))
        //     .setSelectedItemId(R.id.nav_komplain);
    }

    private void resetForm() {
        etJudul.setText("");
        etDeskripsi.setText("");
        spKategori.setSelection(0);
        ivFoto.setVisibility(View.GONE);
        fotoUri = null;
    }
}