package com.example.project_uts.network;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private Context context;
    private static final String TAG = "AuthInterceptor";

    public AuthInterceptor(Context context) {
        this.context = context;
        Log.d(TAG, "Interceptor created with context: " + (context != null ? "VALID" : "NULL"));
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // DEBUG: Tampilkan URL yang diakses
        Log.d(TAG, "=== AUTH INTERCEPTOR ===");
        Log.d(TAG, "URL: " + originalRequest.url());
        Log.d(TAG, "Method: " + originalRequest.method());

        // Cek jika context null
        if (context == null) {
            Log.e(TAG, "CONTEXT IS NULL! Cannot get token");
            return chain.proceed(originalRequest);
        }

        // Ambil token dari AuthManage
        AuthManage authManage = new AuthManage(context);
        String token = authManage.getToken();

        // DEBUG DETAIL
        Log.d(TAG, "Token: " + (token != null ? "EXISTS" : "NULL"));
        if (token != null) {
            Log.d(TAG, "Token length: " + token.length());
            Log.d(TAG, "Token prefix: " + token.substring(0, Math.min(20, token.length())) + "...");
            Log.d(TAG, "Starts with 'eyJ' (JWT)?: " + token.startsWith("eyJ"));
        }

        if (token != null && !token.isEmpty()) {
            // Tambahkan header Authorization
            Request.Builder requestBuilder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + token);

            Log.d(TAG, "Authorization header ADDED");

            // Log headers untuk verifikasi
            Request newRequest = requestBuilder.build();
            Log.d(TAG, "Final request headers:");
            for (String name : newRequest.headers().names()) {
                Log.d(TAG, "  " + name + ": " + newRequest.header(name));
            }

            return chain.proceed(newRequest);
        } else {
            Log.d(TAG, "NO TOKEN FOUND, proceeding without Authorization");
            Log.d(TAG, "This will cause 401 error!");
            return chain.proceed(originalRequest);
        }
    }
}