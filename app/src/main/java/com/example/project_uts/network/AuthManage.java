package com.example.project_uts.network;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.project_uts.LoginActivity;
import com.example.project_uts.models.User;
import com.google.gson.Gson;

public class AuthManage {
    private static final String PREF_NAME = "auth_pref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER = "user_data";
    private static final String TAG = "AuthManage";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public AuthManage(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
        gson = new Gson();
    }

    // Simpan token dan user setelah login
    public void saveAuthData(String token, User user) {
        editor.putString(KEY_TOKEN, token);
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();

        Log.d(TAG, "Auth data saved - Token: " +
                (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "NULL"));
        Log.d(TAG, "User saved - Full Name: " + user.getFull_name() +
                ", Email: " + user.getEmail() +
                ", Role: " + user.getRole());

        // Verifikasi data tersimpan
        String savedJson = pref.getString(KEY_USER, null);
        Log.d(TAG, "Saved JSON length: " + (savedJson != null ? savedJson.length() : 0));
    }

    // Ambil user data - HAPUS method kedua yang dikomentari!
    public User getUser() {
        String userJson = pref.getString(KEY_USER, null);
        Log.d(TAG, "Getting user - JSON exists: " + (userJson != null));

        if (userJson != null) {
            try {
                Log.d(TAG, "JSON content: " + userJson);
                User user = gson.fromJson(userJson, User.class);
                Log.d(TAG, "User parsed - Name: " + user.getFull_name() +
                        ", Email: " + user.getEmail() +
                        ", Role: " + user.getRole() +
                        ", ID: " + user.getId());
                return user;
            } catch (Exception e) {
                Log.e(TAG, "Error parsing user JSON: " + e.getMessage());
                return null;
            }
        }
        Log.d(TAG, "No user data found in preferences");
        return null;
    }

    // Ambil token untuk API calls
    public String getToken() {
        String token = pref.getString(KEY_TOKEN, null);
        Log.d(TAG, "Getting token: " + (token != null ? "YES (length: " + token.length() + ")" : "NO"));
        return token;
    }

    // Ambil user ID
    public String getUserId() {
        User user = getUser();
        String userId = user != null ? user.getId() : null;
        Log.d(TAG, "Getting user ID: " + userId);
        return userId;
    }

    // Ambil user role
    public String getUserRole() {
        User user = getUser();
        String role = user != null ? user.getRole() : null;
        Log.d(TAG, "Getting user role: " + role);
        return role;
    }

    // Cek apakah user sudah login
    public boolean isLoggedIn() {
        boolean loggedIn = getToken() != null;
        Log.d(TAG, "isLoggedIn: " + loggedIn);
        return loggedIn;
    }

    public void logout(Context context) {
        editor.clear();
        boolean success = editor.commit();
        Log.d(TAG, "AuthManager clear successful: " + success);

        // 2. CLEAR ALL LEGACY PREFERENCES (SEMUA VERSI)
        String[] legacyPrefNames = {
                "user_pref",
                "user_prefs",
                "auth_preferences"
        };

        for (String prefName : legacyPrefNames) {
            try {
                SharedPreferences legacyPref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
                legacyPref.edit().clear().apply();
                Log.d(TAG, "Cleared legacy pref: " + prefName);
            } catch (Exception e) {
                Log.e(TAG, "Error clearing pref " + prefName + ": " + e.getMessage());
            }
        }

        // 4. VERIFY EVERYTHING IS CLEARED
        Log.d(TAG, "=== VERIFICATION AFTER CLEAR ===");
        Log.d(TAG, "Auth token: " + (pref.getString(KEY_TOKEN, null) != null ? "STILL EXISTS" : "CLEARED ✓"));
        Log.d(TAG, "Auth user: " + (pref.getString(KEY_USER, null) != null ? "STILL EXISTS" : "CLEARED ✓"));

        for (String prefName : legacyPrefNames) {
            SharedPreferences checkPref = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
            boolean isEmpty = checkPref.getAll().isEmpty();
            Log.d(TAG, prefName + " empty: " + (isEmpty ? "YES ✓" : "NO ✗"));
        }

        // 5. REDIRECT TO LOGIN WITH PROPER FLAGS
        Log.d(TAG, "Redirecting to LoginActivity...");
        Intent intent = new Intent(context, LoginActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);

        if (context instanceof Activity) {
            Activity activity = (Activity) context;

            activity.finishAffinity();
            Log.d(TAG, "All activities finished with finishAffinity()");
        }

        Toast.makeText(context, "Logout berhasil", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "=== LOGOUT PROCESS COMPLETE ===");
    }

    // Update user data (setelah update profile)
    public void updateUser(User user) {
        Log.d(TAG, "Updating user data for: " + user.getFull_name());
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
        Log.d(TAG, "User data updated");
    }

    // Method tambahan untuk debugging
    public void printAllPreferences() {
        Log.d(TAG, "=== ALL PREFERENCES ===");
        Log.d(TAG, "Token exists: " + (pref.getString(KEY_TOKEN, null) != null));
        Log.d(TAG, "User data exists: " + (pref.getString(KEY_USER, null) != null));
        Log.d(TAG, "All keys: " + pref.getAll().keySet());
    }
}