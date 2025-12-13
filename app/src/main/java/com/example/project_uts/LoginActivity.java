package com.example.project_uts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;
import com.example.project_uts.models.LoginResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnDaftar;
    private AuthManage authManage;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize ApiClient dan AuthManager
        ApiClient.init(getApplicationContext());
        authManage = new AuthManage(this);
        apiService = ApiClient.getApiService();

        // Cek jika user sudah login dengan AuthManager (not SharedPreferences)
        if (authManage.isLoggedIn()) {
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
        etUsername.setText("customer1");
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

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Loading...");

        // Siapkan data untuk API
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", username);
        credentials.put("password", password);

        // Panggil API Login dengan Retrofit
        Call<LoginResponse> call = apiService.login(credentials);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse.isSuccess()) {
                        // Simpan token dan user data
                        String token = loginResponse.getData().getToken();
                        com.example.project_uts.models.User user = loginResponse.getData().getUser();

                        authManage.saveAuthData(token, user);

                        // Login berhasil
                        redirectToMainActivity();
                        Toast.makeText(LoginActivity.this,
                                "Login berhasil sebagai " + user.getRole(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this,
                                loginResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle error response
                    if (response.code() == 401) {
                        Toast.makeText(LoginActivity.this,
                                "Username atau password salah", Toast.LENGTH_SHORT).show();
                    } else {
                        // Fallback ke login lokal jika API error
                        fallbackLogin(username, password);
                        Toast.makeText(LoginActivity.this,
                                "API error, menggunakan mode offline", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Login");

                // Fallback ke login lokal jika network error
                fallbackLogin(username, password);
                Toast.makeText(LoginActivity.this,
                        "Koneksi gagal, menggunakan mode offline", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // FALLBACK API JIKA TIDAK RESPONSIVE
    private void fallbackLogin(String username, String password) {
        // Simple authentication fallback (sama seperti sebelumnya)
        String role = checkLogin(username, password);

        if (role != null) {
            // Simpan ke SharedPreferences lama (untuk compatibility)
            getSharedPreferences("user_pref", MODE_PRIVATE)
                    .edit()
                    .putString("username", username)
                    .putString("role", role)
                    .putString("nama", getDisplayName(username, role))
                    .putString("email", username + "@example.com")
                    .putBoolean("isLoggedIn", true)
                    .apply();

            // Login berhasil
            redirectToMainActivity();
            Toast.makeText(this, "Login berhasil sebagai " + role, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show();
        }
    }

    private String checkLogin(String username, String password) {
        // Simple authentication fallback
        if ("customer1".equals(username) && "123".equals(password)) {
            return "customer";
        }
        else if ("teknisi1".equals(username) && "123".equals(password)) {
            return "teknisi";
        }
        else if ("admin".equals(username) && "123".equals(password)) {
            return "admin";
        }
        return null;
    }

    private String getDisplayName(String username, String role) {
        switch (username) {
            case "customer1": return "Customer User";
            case "teknisi1": return "Teknisi Handal";
            case "admin": return "Administrator";
            default: return "User " + username;
        }
    }
}