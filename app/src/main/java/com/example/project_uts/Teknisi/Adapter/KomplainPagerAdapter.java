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
                // Tab Komplain - SHOW READY COMPLAINTS
                KomplainFragment komplainFragment = new KomplainFragment();
                // Optional: bisa kasih argument jika perlu
                return komplainFragment;
            case 1:
                // Tab Progress - SHOW PROGRESS COMPLAINTS
                ProgressFragment progressFragment = new ProgressFragment();
                return progressFragment;
            case 2:
                // Tab Completed - SHOW COMPLETED COMPLAINTS
                CompletedFragment completedFragment = new CompletedFragment();
                return completedFragment;
            default:
                return new KomplainFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3; // Komplain, Progress, Completed
    }
}