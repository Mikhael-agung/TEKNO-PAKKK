package com.example.project_uts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText etUsername, etPassword;
    Button btnLogin, btnDaftar;

    // Data dummy lengkap
    private Map<String, User> dummyUsers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin   = findViewById(R.id.btnLogin);
        btnDaftar  = findViewById(R.id.btnDaftar);

        // Setup dummy data
        setupDummyData();

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString();
            String password = etPassword.getText().toString();

            btnDaftar.setOnClickListener( view -> {
                Intent intent = new Intent(LoginActivity.this, RegisterCustomerActivity.class);
                startActivity(intent);
            });

            if(username.isEmpty() || password.isEmpty()){
                Toast.makeText(LoginActivity.this, "Isi semua field dulu!", Toast.LENGTH_SHORT).show();
            } else {
                // Cek di dummy data
                if (dummyUsers.containsKey(username) && dummyUsers.get(username).getPassword().equals(password)) {
                    User user = dummyUsers.get(username);

                    // Simpan data user ke SharedPreferences
                    saveUserData(user);

                    Toast.makeText(LoginActivity.this, "Login berhasil sebagai " + user.getRole() + "!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("role", user.getRole());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Username/Password salah!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupDummyData() {
        // Dummy Customer
//        dummyUsers.put("customer", new User("Budi Santoso", "budi@customer.com", "1234", "customer", "085232978270"));
//        dummyUsers.put("john", new User("John Doe", "john@customer.com", "1234", "customer", "085232978270"));

        // Dummy Teknisi
        dummyUsers.put("teknisi", new User("Dicky YP", "DickyYP@teknisi.com", "1234", "teknisi", "+628978845390"));
        dummyUsers.put("Agung", new User("M Agung", "Agung@teknisi.com", "1234", "teknisi", "+6285232978270"));
    }

    private void saveUserData(User user) {
        SharedPreferences preferences = getSharedPreferences("user_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("nama", user.getNama());
        editor.putString("email", user.getEmail());
        editor.putString("no_telp", user.getNoTelp());
        editor.putString("role", user.getRole());
        editor.putBoolean("is_logged_in", true);
        editor.apply();
    }

    // Model Class User
    private static class User {
        private String nama, email, password, role, noTelp;

        public User(String nama, String email, String password, String role, String noTelp) {
            this.nama = nama;
            this.email = email;
            this.password = password;
            this.role = role;
            this.noTelp = noTelp;
        }

        public String getNama() { return nama; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
        public String getNoTelp() { return noTelp; }
    }
}