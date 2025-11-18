package com.example.project_uts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterCustomerActivity extends AppCompatActivity {

    private EditText etNama, etEmail, etNoTelp, etPassword, etKonfirmasiPassword;
    private Button btnDaftar, btnSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etNoTelp = findViewById(R.id.etNoTelp);
        etPassword = findViewById(R.id.etPassword);
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnSignIn = findViewById(R.id.btnSignIn);
    }

    private void setupClickListeners() {
        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pindah ke Login Activity
                Intent intent = new Intent(RegisterCustomerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registerUser() {
        String nama = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String noTelp = etNoTelp.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String konfirmasiPassword = etKonfirmasiPassword.getText().toString().trim();

        // Validasi sederhana
        if (nama.isEmpty()) {
            etNama.setError("Nama tidak boleh kosong");
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email tidak boleh kosong");
            return;
        }

        if (noTelp.isEmpty()) {
            etNoTelp.setError("Nomor telepon tidak boleh kosong");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password tidak boleh kosong");
            return;
        }

        if (!password.equals(konfirmasiPassword)) {
            etKonfirmasiPassword.setError("Password tidak cocok");
            return;
        }

        // Simpan data sementara (nanti diganti dengan backend)
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nama", nama);
        editor.putString("email", email);
        editor.putString("no_telp", noTelp);
        editor.putBoolean("is_logged_in", true);
        editor.apply();

        // Pindah ke MainActivity
        Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterCustomerActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
