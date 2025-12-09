package com.example.project_uts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Ambil role dari intent
        userRole = getIntent().getStringExtra("role");
        if (userRole == null) {
            userRole = "teknisi"; // Default ke teknisi aja dulu
        }

        // SET HANYA TEKNISI DULU - COMMENT CUSTOMER
        bottomNav.inflateMenu(R.menu.bottom_nav_teknisi);

        // Load default fragment
        if (savedInstanceState == null) {
            loadDefaultFragment();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_dashboard) {
                // HANYA TEKNISI DULU
                selectedFragment = new DashboardTeknisiFragment();
            } else if (itemId == R.id.nav_komplain) {
                selectedFragment = new KomplainListFragment();
            } else if (itemId == R.id.nav_diskusi) {
                selectedFragment = new DiskusiTeknisiFragment();
            } else if (itemId == R.id.nav_profil) {
                selectedFragment = new ProfilFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });
    }

    private void loadDefaultFragment() {
        // HANYA LOAD DASHBOARD TEKNISI
        Fragment defaultFragment = new DashboardTeknisiFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();
    }


}