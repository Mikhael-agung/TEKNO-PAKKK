package com.example.project_uts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
public class KomplainListFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_komplain_list, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);

        KomplainPagerAdapter pagerAdapter = new KomplainPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Komplain");
                    else if (position == 1) tab.setText("Progress");
                    else if (position == 2) tab.setText("Completed");
                }).attach();

        return view;
    }
}
