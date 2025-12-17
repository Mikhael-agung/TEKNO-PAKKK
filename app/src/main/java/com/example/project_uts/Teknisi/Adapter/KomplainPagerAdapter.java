package com.example.project_uts.Teknisi.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.project_uts.Teknisi.Fragment.CompletedFragment;
import com.example.project_uts.Teknisi.Fragment.KomplainFragment;
import com.example.project_uts.Teknisi.Fragment.ProgressFragment;

public class KomplainPagerAdapter extends FragmentStateAdapter {

    public KomplainPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // Tab pertama: Komplain
                return new KomplainFragment();
            case 1:
                // Tab kedua: Progress
                return new ProgressFragment();
            case 2:
                // Tab ketiga: Completed
                return new CompletedFragment();
            default:
                return new KomplainFragment();
        }
    }

    @Override
    public int getItemCount() {
        // Jumlah tab = 3
        return 3;
    }
}
