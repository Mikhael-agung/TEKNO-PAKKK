package com.example.project_uts.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.project_uts.models.User;
import com.google.gson.Gson;

public class AuthManage {
    private static final String PREF_NAME = "auth_pref";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_USER = "user_data";

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
    }

    // Ambil token untuk API calls
    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    // Ambil user data
    public User getUser() {
        String userJson = pref.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    // Ambil user ID
    public String getUserId() {
        User user = getUser();
        return user != null ? user.getId() : null;
    }

    // Ambil user role
    public String getUserRole() {
        User user = getUser();
        return user != null ? user.getRole() : null;
    }

    // Cek apakah user sudah login
    public boolean isLoggedIn() {
        return getToken() != null;
    }

    // Logout - clear semua data
    public void logout() {
        editor.clear();
        editor.apply();
    }

    // Update user data (setelah update profile)
    public void updateUser(User user) {
        String userJson = gson.toJson(user);
        editor.putString(KEY_USER, userJson);
        editor.apply();
    }
}