package com.example.project_uts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnDaftar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("user_pref", Context.MODE_PRIVATE);

        // Cek jika user sudah login, langsung redirect ke MainActivity
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            redirectToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

        // Initialize views
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnDaftar = findViewById(R.id.btnDaftar);

        // Login button click
        btnLogin.setOnClickListener(v -> loginUser());

        // Daftar button click
        btnDaftar.setOnClickListener(v -> {
            Toast.makeText(this, "Fitur pendaftaran coming soon!", Toast.LENGTH_SHORT).show();
        });

        // Auto-fill for development
        etUsername.setText("customer");
        etPassword.setText("123");
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Validasi input
        if (username.isEmpty()) {
            etUsername.setError("Username harus diisi");
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password harus diisi");
            return;
        }

        // Cek login
        String role = checkLogin(username, password);

        if (role != null) {
            // SIMPAN DATA KE SHAREDPREFERENCES
            saveUserData(username, role);

            // Login berhasil
            redirectToMainActivity();
            Toast.makeText(this, "Login berhasil sebagai " + role, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData(String username, String role) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("role", role);
        editor.putString("nama", getDisplayName(username, role));
        editor.putString("email", username + "@example.com");
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private String getDisplayName(String username, String role) {
        switch (username) {
            case "customer":
                return "Customer User";
            case "teknisi":
                return "Teknisi Handal";
            case "admin":
                return "Administrator";
            default:
                return "User " + username;
        }
    }

    private String checkLogin(String username, String password) {
        // Simple authentication
        if ("customer".equals(username) && "123".equals(password)) {
            return "customer";
        }
        else if ("teknisi".equals(username) && "123".equals(password)) {
            return "teknisi";
        }
        else if ("admin".equals(username) && "123".equals(password)) {
            return "admin";
        }
        return null;
    }
}