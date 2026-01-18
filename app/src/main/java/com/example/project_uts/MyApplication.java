package com.example.project_uts;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApplication extends Application {
    private static final String PREFS_NAME = "app_settings";
    private static final String THEME_PREF_KEY = "dark_mode_enabled";

    private static boolean isInitialized = false; //flag

    @Override
    public void onCreate() {
        super.onCreate();

        // CEK JIKA SUDAH DIINITIALIZE, JANGAN EXECUTE LAGI
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(THEME_PREF_KEY, false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Log
        android.util.Log.d("MyApplication",
                "Dark mode initialized: " + isDarkMode +
                        ", Mode: " + (isDarkMode ? "DARK" : "LIGHT"));
    }
}