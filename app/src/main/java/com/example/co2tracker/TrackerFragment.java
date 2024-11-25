package com.example.co2tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayoutMediator;

public class TrackerFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        // Create adapter using Fragment constructor
        TrackerPagerAdapter pagerAdapter = new TrackerPagerAdapter(this);
        // Or use FragmentManager constructor
        // TrackerPagerAdapter pagerAdapter = new TrackerPagerAdapter(getChildFragmentManager(), getLifecycle());

        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Food");
            } else {
                tab.setText("Car");
            }
        }).attach();

        return view;
    }
}