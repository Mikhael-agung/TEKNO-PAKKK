package com.example.project_uts.network;

import android.content.Context;
import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    private static final String BASE_URL = "http://10.0.2.2:3000/";
    private static Retrofit retrofit = null;
    private static Context appContext; // Rename untuk clarity

    public static void init(Context context) {
        appContext = context.getApplicationContext(); // Pakai Application Context
        Log.d("ApiClient", "Initialized with context");
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            if (appContext == null) {
                Log.e("ApiClient", "Context is NULL! Call init() first.");
                throw new IllegalStateException("ApiClient not initialized. Call init() first.");
            }

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new AuthInterceptor(appContext)) // ← PAKAI appContext
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();

            Log.d("ApiClient", "Retrofit instance created");
        }
        return retrofit.create(ApiService.class);
    }
}