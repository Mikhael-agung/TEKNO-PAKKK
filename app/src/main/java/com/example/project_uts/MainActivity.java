
package com.example.project_uts;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.project_uts.fragment.CustomerFragment;
import com.example.project_uts.fragment.DashboardCustomerFragment;
import com.example.project_uts.fragment.HistoryComplainFragment;
import com.example.project_uts.fragment.ProfilFragment;

public class MainActivity extends AppCompatActivity {

    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        userRole = getIntent().getStringExtra("role");
        if (userRole == null) {
            userRole = "customer"; // Default customer
        }

        userRole = "customer";

        // Set menu - selalu pakai menu customer
        bottomNav.inflateMenu(R.menu.button_nav_customer);

        // selalu dashboard customer
        if (savedInstanceState == null) {
            loadDefaultFragment();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            // SELALU PAKAI FRAGMENT CUSTOMER
            if (itemId == R.id.nav_dashboard) {
                selectedFragment = new DashboardCustomerFragment();
            } else if (itemId == R.id.nav_komplain) {
                selectedFragment = new CustomerFragment();
            } else if (itemId == R.id.nav_history) {
                selectedFragment = new HistoryComplainFragment();
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void loadDefaultFragment() {
        // SELALU LOAD DASHBOARD CUSTOMER
        Fragment defaultFragment = new DashboardCustomerFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();
    }
}