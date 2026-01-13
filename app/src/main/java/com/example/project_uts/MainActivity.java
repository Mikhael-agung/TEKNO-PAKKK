
package com.example.project_uts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.project_uts.fragment.CustomerFragment;
import com.example.project_uts.fragment.DashboardCustomerFragment;
import com.example.project_uts.fragment.HistoryComplainFragment;
import com.example.project_uts.fragment.ProfilFragment;

public class MainActivity extends AppCompatActivity {
    private String userRole;
    private static final String TAG = "MainActivity_Customer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "=== CUSTOMER ACTIVITY STARTED ===");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        userRole = getIntent().getStringExtra("role");
        if (userRole == null) {
            userRole = "customer"; // Default customer
        }

        Log.d(TAG, "Role from intent: " + userRole);

        // validasi role
        if ("teknisi".equalsIgnoreCase(userRole)) {
            Log.w(TAG, "Wrong activity! User is teknisi, redirecting...");
            Intent intent = new Intent(this,
                    com.example.project_uts.Teknisi.Activity.MainActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
            return;
        }

        // Set menu - hanya pakai menu customer
        bottomNav.getMenu().clear(); // Clear existing items
        bottomNav.inflateMenu(R.menu.button_nav_customer);
        Log.d(TAG, "Menu inflated with " + bottomNav.getMenu().size() + " items");

        // selalu dashboard customer
        if (savedInstanceState == null) {
            loadDefaultFragment();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            Log.d(TAG, "Menu item clicked: " + item.getTitle());

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
        Log.d(TAG, "Loading default fragment: DashboardCustomerFragment");
        Fragment defaultFragment = new DashboardCustomerFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();
    }
}