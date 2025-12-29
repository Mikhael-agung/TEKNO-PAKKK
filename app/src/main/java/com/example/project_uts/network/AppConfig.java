package com.example.project_uts.network;

public class AppConfig {

    // MODE: "development" atau "production"
    private static String currentMode = "production"; // Default production

    // BASE URLS
    private static final String PRODUCTION_URL = "https://be-teknoserve-production.up.railway.app/";
    private static final String DEVELOPMENT_URL = "http://10.0.2.2:3000/"; // Local emulator

    public static String getBaseUrl() {
        if ("development".equals(currentMode)) {
            return DEVELOPMENT_URL;
        } else {
            return PRODUCTION_URL;
        }
    }

    public static void setDevelopmentMode() {
        currentMode = "development";
    }

    public static void setProductionMode() {
        currentMode = "production";
    }

    public static boolean isDevelopment() {
        return "development".equals(currentMode);
    }

    public static boolean isProduction() {
        return "production".equals(currentMode);
    }
}