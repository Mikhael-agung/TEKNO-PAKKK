package com.example.project_uts.Teknisi.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.project_uts.R;
import com.example.project_uts.Teknisi.Adapter.KomplainPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class KomplainListFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_komplain_list_teknisi, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        // Initialize adapter dengan requireActivity()
        KomplainPagerAdapter pagerAdapter = new KomplainPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout dengan ViewPager
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Komplain");
                            break;
                        case 1:
                            tab.setText("Progress");
                            break;
                        case 2:
                            tab.setText("Completed");
                            break;
                    }
                }).attach();

        return view;
    }
}