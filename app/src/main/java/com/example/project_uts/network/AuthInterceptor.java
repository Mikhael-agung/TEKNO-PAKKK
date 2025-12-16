package com.example.project_uts.network;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private AuthManage authManage;

    public AuthInterceptor(Context context) {
        Log.d("AuthInterceptor", "Constructor called with context: " +
                (context != null ? "VALID" : "NULL"));
        authManage = new AuthManage(context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Log.d("AuthInterceptor", "Intercepting: " + originalRequest.url());

        // Ambil token dari AuthManager
        String token = authManage.getToken();
        Log.d("AuthInterceptor", "Token exists: " + (token != null));
        if (token != null) {
            Log.d("AuthInterceptor", "Token length: " + token.length());
        }

        Request.Builder builder = originalRequest.newBuilder();

        if (token != null && !token.isEmpty()) {
            // Tambah header Authorization
            builder.addHeader("Authorization", "Bearer " + token);
            Log.d("AuthInterceptor", "Added Authorization header with token");

            // Debug: Tampilkan header
            Log.d("AuthInterceptor", "Headers: " + builder.build().headers());
        } else {
            Log.w("AuthInterceptor", "No token found!");
        }

        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}