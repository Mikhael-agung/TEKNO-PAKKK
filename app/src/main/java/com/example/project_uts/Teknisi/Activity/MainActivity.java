package com.example.project_uts.Teknisi.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Fragment.CompletedFragment;
import com.example.project_uts.Teknisi.Fragment.DashboardTeknisiFragment;
import com.example.project_uts.Teknisi.Fragment.DiskusiTeknisiFragment;
import com.example.project_uts.Teknisi.Fragment.KomplainListFragment;
import com.example.project_uts.Teknisi.Fragment.ProgressFragment;
import com.example.project_uts.Teknisi.Fragment.ProfilFragment;
import com.example.project_uts.network.ApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private String userRole;
    private static final String TAG = "MainActivity_Teknisi";
    private ActivityResultLauncher<Intent> detailLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_teknisi);

        Log.d(TAG, "=== TEKNISI ACTIVITY STARTED ===");

        ApiClient.init(getApplicationContext());
        Log.d(TAG, "ApiClient initialized for teknisi");

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Ambil role dari intent
        userRole = getIntent().getStringExtra("role");
        if (userRole == null) {
            userRole = "teknisi"; // Default teknisi
        }

        Log.d(TAG, "Role from intent: " + userRole);

        if (!"teknisi".equalsIgnoreCase(userRole)) {
            Log.w(TAG, "Wrong activity! User is " + userRole + ", redirecting...");
            Intent intent = new Intent(this,
                    com.example.project_uts.MainActivity.class);
            intent.putExtras(getIntent());
            startActivity(intent);
            finish();
            return;
        }

        // set hanya menu teknisi
        bottomNav.getMenu().clear();
        bottomNav.inflateMenu(R.menu.bottom_nav_teknisi);
        Log.d(TAG, "Menu inflated with " + bottomNav.getMenu().size() + " items");

        // Load default fragment
        if (savedInstanceState == null) {
            loadDefaultFragment();
        }

        // Register ActivityResultLauncher untuk DetailActivity
        detailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null && data.getBooleanExtra("status_updated", false)) {
                            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                            if (current instanceof ProgressFragment) {
                                ((ProgressFragment) current).reloadComplaints();
                            }
                            if (current instanceof CompletedFragment) {
                                ((CompletedFragment) current).reloadComplaints();
                            }
                        }
                    }
                }
        );

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            Log.d(TAG, "Menu item clicked: " + item.getTitle());

            if (itemId == R.id.nav_dashboard) {
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
        Log.d(TAG, "Loading default fragment: DashboardTeknisiFragment");
        Fragment defaultFragment = new DashboardTeknisiFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, defaultFragment)
                .commit();
    }

    // 👉 method untuk buka DetailActivity dengan launcher
    public void openDetailActivity(Intent intent) {
        detailLauncher.launch(intent);
    }
}
