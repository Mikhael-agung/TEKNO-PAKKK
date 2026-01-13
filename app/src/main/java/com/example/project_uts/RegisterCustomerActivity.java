package com.example.project_uts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_uts.models.RegisterResponse;
import com.example.project_uts.models.User;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCustomerActivity extends AppCompatActivity {


    private EditText etNama, etEmail, etUsername, etNoTelp, etPassword, etKonfirmasiPassword;
    private Button btnDaftar, btnSignIn;
    private ProgressBar progressBar;

    private ApiService apiService;
    private AuthManage authManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize API Service
        ApiClient.init(getApplicationContext());
        apiService = ApiClient.getApiService();
        authManage = new AuthManage(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etNama = findViewById(R.id.etNama);
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etNoTelp = findViewById(R.id.etNoTelp);
        etPassword = findViewById(R.id.etPassword);
        etKonfirmasiPassword = findViewById(R.id.etKonfirmasiPassword);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnSignIn = findViewById(R.id.btnSignIn);

        progressBar = findViewById(R.id.progressBar);
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnDaftar.setOnClickListener(v -> registerUser());

        btnSignIn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterCustomerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private String formatPhoneNumber(String phone) {
        phone = phone.replaceAll("[^0-9]", ""); // hapus spasi/simbol

        if (phone.startsWith("0")) {
            phone = "62" + phone.substring(1);
        } else if (!phone.startsWith("62")) {
            phone = "62" + phone;
        }
        return phone;
    }


    private void registerUser() {
        // Collect form data
        String full_name = etNama.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String phone = etNoTelp.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String konfirmasiPassword = etKonfirmasiPassword.getText().toString().trim();

        phone = formatPhoneNumber(phone);
        // Validation dengan username
        if (!validateForm(full_name, email, username, phone, password, konfirmasiPassword)) {
            return;
        }

        // Show loading
        setLoading(true);

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("email", email);
        userData.put("password", password);
        userData.put("full_name", full_name);
        userData.put("phone", phone);
        userData.put("role", "customer");

        // API Call
        Call<RegisterResponse> call = apiService.register(userData);

        call.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                setLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    RegisterResponse registerResponse = response.body();

                    if (registerResponse.isSuccess()) {
                        handleRegistrationSuccess(registerResponse);
                    } else {
                        handleRegistrationError(registerResponse.getMessage());
                    }
                } else {
                    handleApiError(response.code());
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                setLoading(false);
                handleNetworkError(t);
            }
        });
    }


    private boolean validateForm(String full_name, String email, String username,
                                 String phone, String password, String konfirmasiPassword) {

        if (full_name.isEmpty()) {
            etNama.setError("Nama lengkap harus diisi");
            etNama.requestFocus();
            return false;
        }

        if (email.isEmpty()) {
            etEmail.setError("Email harus diisi");
            etEmail.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return false;
        }

        if (username.isEmpty()) {
            etUsername.setError("Username harus diisi");
            etUsername.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            etUsername.setError("Username minimal 3 karakter");
            etUsername.requestFocus();
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            etUsername.setError("Username hanya boleh huruf, angka, dan underscore");
            etUsername.requestFocus();
            return false;
        }

        if (phone.isEmpty()) {
            etNoTelp.setError("Nomor telepon harus diisi");
            etNoTelp.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Password harus diisi");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return false;
        }

        // client-side validation only
        if (!password.equals(konfirmasiPassword)) {
            etKonfirmasiPassword.setError("Password tidak cocok");
            etKonfirmasiPassword.requestFocus();
            return false;
        }

        return true;
    }

    // FIX: HAPUS PARAMETER email
    private void handleRegistrationSuccess(RegisterResponse response) {
        // Save auth data if token is returned
        if (response.getData() != null && response.getData().getToken() != null) {
            String token = response.getData().getToken();
            User user = response.getData().getUser();

            // Save to AuthManage
            authManage.saveAuthData(token, user);

            Toast.makeText(this,
                    "Registrasi berhasil! Anda telah login otomatis.",
                    Toast.LENGTH_LONG).show();

            // Redirect to MainActivity based on role
            redirectToMainActivity(user.getRole());
        } else {
            // Registration success but no auto-login
            Toast.makeText(this,
                    "Registrasi berhasil! Silakan login.",
                    Toast.LENGTH_LONG).show();

            // Go back to LoginActivity
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void handleRegistrationError(String message) {
        Toast.makeText(this,
                "Registrasi gagal: " + message,
                Toast.LENGTH_LONG).show();
    }

    private void handleApiError(int statusCode) {
        String errorMessage;

        switch (statusCode) {
            case 400:
                errorMessage = "Data tidak valid. Periksa kembali form.";
                break;
            case 409:
                errorMessage = "Username atau email sudah terdaftar.";
                break;
            case 500:
                errorMessage = "Server error. Silakan coba lagi nanti.";
                break;
            default:
                errorMessage = "Error " + statusCode + ". Silakan coba lagi.";
        }

        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    private void handleNetworkError(Throwable t) {
        Log.e("RegisterActivity", "Network error: " + t.getMessage());
        Toast.makeText(this,
                "Koneksi gagal. Periksa internet Anda.",
                Toast.LENGTH_LONG).show();
    }

    private void redirectToMainActivity(String role) {
        Intent intent;

        if ("teknisi".equalsIgnoreCase(role)) {
            intent = new Intent(this,
                    com.example.project_uts.Teknisi.Activity.MainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        btnDaftar.setEnabled(!isLoading);
        btnSignIn.setEnabled(!isLoading);

        btnDaftar.setText(isLoading ? "Mendaftarkan..." : "Daftar");
    }
}