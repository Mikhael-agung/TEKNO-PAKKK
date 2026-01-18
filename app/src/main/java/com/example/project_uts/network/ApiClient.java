package com.example.project_uts.network;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL = AppConfig.getBaseUrl();
    private static Retrofit retrofit = null;
    private static Context appContext;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
        Log.d(TAG, "ApiClient initialized with context");
        retrofit = null;
    }

    public static ApiService getApiService() {
        if (retrofit == null) {
            createRetrofitInstance();
        }
        return retrofit.create(ApiService.class);
    }

    // ✅ TAMBAH METHOD INI
    public static ApiService getFreshApiService() {
        Log.d(TAG, "🔄 Creating FRESH API service (no cache)");

        // Force create new Retrofit instance dengan no-cache
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Buat OkHttpClient dengan no-cache
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(appContext))
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .cache(null) // ✅ DISABLE CACHE
                .addInterceptor(chain -> {
                    // ✅ ADD NO-CACHE HEADERS
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("Cache-Control", "no-cache, no-store, must-revalidate")
                            .header("Pragma", "no-cache")
                            .header("Expires", "0")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        Retrofit freshRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        return freshRetrofit.create(ApiService.class);
    }

    private static void createRetrofitInstance() {
        if (appContext == null) {
            Log.e(TAG, "Context is NULL! Call init() first.");
            throw new IllegalStateException("ApiClient not initialized. Call init() first.");
        }

        Log.d(TAG, "Creating Retrofit instance...");

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(appContext))
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

        Log.d(TAG, "Retrofit instance created successfully");
    }

    // Method untuk debugging
    public static void printDebugInfo() {
        Log.d(TAG, "=== API CLIENT DEBUG ===");
        Log.d(TAG, "Context: " + (appContext != null ? "SET" : "NULL"));
        Log.d(TAG, "Retrofit: " + (retrofit != null ? "CREATED" : "NULL"));
        Log.d(TAG, "Base URL: " + BASE_URL);
    }
}