package com.example.project_uts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_uts.models.LoginResponse;
import com.example.project_uts.models.User;
import com.example.project_uts.network.ApiClient;
import com.example.project_uts.network.ApiService;
import com.example.project_uts.network.AuthManage;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

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
        boolean fromLogout = getIntent().getBooleanExtra("from_logout", false);
        boolean forceLogin = getIntent().getBooleanExtra("force_login", false);

        Log.d(TAG, "LoginActivity started - fromLogout: " + fromLogout + ", forceLogin: " + forceLogin);

        if (fromLogout || forceLogin) {
            Log.d(TAG, "Forcing login screen (from logout)");
            // Lanjutkan ke login screen
        }
        // Cek jika user sudah login dengan AuthManager
        else if (authManage.isLoggedIn()) {
            Log.d(TAG, "User is logged in, redirecting...");
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
            Intent intent = new Intent(LoginActivity.this, RegisterCustomerActivity.class);
            startActivity(intent);
        });
    }

    private void redirectToMainActivity() {
        Log.d(TAG, "=== REDIRECTING TO MAINACTIVITY ===");

        // get role from  authmanage
        User user = authManage.getUser();
        String role = "customer"; // default
        if (user != null && user.getRole() != null) {
            role = user.getRole();
        }

        Log.d(TAG, "Redirecting with role: " + role);

        Intent intent;

        if ("teknisi".equalsIgnoreCase(role)) {
            Log.d(TAG, "Launching TEKNISI MainActivity");
            intent = new Intent(LoginActivity.this,
                    com.example.project_uts.Teknisi.Activity.MainActivity.class);
        } else {
            Log.d(TAG, "Launching CUSTOMER MainActivity");
            intent = new Intent(LoginActivity.this, MainActivity.class);
        }

        // FLAG PENTING: Clear task agar tidak bisa back ke login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // PASS USER DATA
        if (user != null) {
            intent.putExtra("role", user.getRole());
            intent.putExtra("user_id", user.getId());
            intent.putExtra("username", user.getUsername());
            intent.putExtra("full_name", user.getFull_name());
            intent.putExtra("email", user.getEmail());
        }

        // DEBUG INFO
        intent.putExtra("from_login", true);
        intent.putExtra("login_time", System.currentTimeMillis());

        // START ACTIVITY
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

                        // 1. SIMPAN KE AUTHMANAGE (UNTUK TOKEN DAN DATA USER)
                        authManage.saveAuthData(token, user);

                        // 2. SIMPAN KE SHAREDPREFERENCES LAMA UNTUK KOMPATIBILITAS
                        saveToLegacyPreferences(user);

                        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE); prefs.edit().putString("jwt_token", token).apply();

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

    private void saveToLegacyPreferences(com.example.project_uts.models.User user) {
        // 1. Untuk DashboardFragment (menggunakan "user_prefs")
        SharedPreferences prefs1 = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor1 = prefs1.edit();
        editor1.putString("user_name", user.getFull_name());
        editor1.putString("user_email", user.getEmail());
        editor1.putBoolean("isLoggedIn", true);
        editor1.apply();

        // 2. Untuk ProfilFragment (menggunakan "user_pref")
        SharedPreferences prefs2 = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor2 = prefs2.edit();
        editor2.putString("nama", user.getFull_name());
        editor2.putString("email", user.getEmail());
        editor2.putString("role", user.getRole());
        editor2.putString("username", user.getUsername());
        editor2.putBoolean("isLoggedIn", true);
        editor2.apply();

        Log.d("LoginActivity", "Saved to legacy prefs: " + user.getFull_name() + ", " + user.getEmail());
    }

    // PERBAIKI fallbackLogin juga
    private void fallbackLogin(String username, String password) {
        // Simple authentication fallback (sama seperti sebelumnya)
        String role = checkLogin(username, password);

        if (role != null) {
            // Buat user object untuk AuthManage
            com.example.project_uts.models.User user = new com.example.project_uts.models.User();
            user.setUsername(username);
            user.setEmail(username + "@example.com");
            user.setFull_name(getDisplayName(username, role));
            user.setRole(role);

            // 1. Simpan ke AuthManage (dengan token dummy)
            authManage.saveAuthData("dummy_token_fallback", user);

            // 2. Simpan ke SharedPreferences lama
            saveToLegacyPreferences(user);

            // Login berhasil
            redirectToMainActivity();
            Toast.makeText(this, "Login berhasil sebagai " + role + " (offline)", Toast.LENGTH_SHORT).show();
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

    private void debugLegacyPreferences() {
        Log.d(TAG, "=== LEGACY PREFERENCES DEBUG ===");

        // user_prefs (untuk DashboardFragment)
        SharedPreferences prefs1 = getSharedPreferences("user_prefs", MODE_PRIVATE);
        Log.d(TAG, "user_prefs - user_name: " + prefs1.getString("user_name", "NOT FOUND"));
        Log.d(TAG, "user_prefs - user_email: " + prefs1.getString("user_email", "NOT FOUND"));
        Log.d(TAG, "user_prefs - isLoggedIn: " + prefs1.getBoolean("isLoggedIn", false));

        // user_pref (untuk ProfilFragment)
        SharedPreferences prefs2 = getSharedPreferences("user_pref", MODE_PRIVATE);
        Log.d(TAG, "user_pref - nama: " + prefs2.getString("nama", "NOT FOUND"));
        Log.d(TAG, "user_pref - email: " + prefs2.getString("email", "NOT FOUND"));
        Log.d(TAG, "user_pref - role: " + prefs2.getString("role", "NOT FOUND"));
        Log.d(TAG, "user_pref - username: " + prefs2.getString("username", "NOT FOUND"));
        Log.d(TAG, "user_pref - isLoggedIn: " + prefs2.getBoolean("isLoggedIn", false));
    }
}