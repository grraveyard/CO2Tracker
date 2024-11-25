package com.example.co2tracker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TrackerPagerAdapter extends FragmentStateAdapter {

    public TrackerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public TrackerPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public TrackerPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new FoodTrackerFragment();
            case 1:
                return new CarTrackerFragment();
            default:
                return new FoodTrackerFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2; // Number of tabs
    }
}