package com.example.co2tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import com.example.co2tracker.databinding.ActivityMainBinding;
import java.util.Locale;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
    private UserManager userManager;
    private HomeViewModel homeViewModel;
    private static final String PREFS_NAME = "Settings";
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Apply theme before super.onCreate
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);
        int nightMode = isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(nightMode);
        
        try {
            userManager = new UserManager(this);
            
            // Check if user is logged in
            if (!userManager.isUserLoggedIn()) {
                Log.d(TAG, "No user logged in, redirecting to login");
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return;
            }
            
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

            // Load saved points
            updatePoints(userManager.getPoints());

            // Setup bottom navigation
            setupBottomNavigation();
            
            // Set default fragment or restore saved state
            if (savedInstanceState != null) {
                int currentTab = savedInstanceState.getInt("currentTab", R.id.nav_home);
                binding.bottomNav.setSelectedItemId(currentTab);
            } else {
                binding.bottomNav.setSelectedItemId(R.id.nav_home);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            setContentView(R.layout.activity_main);
        }
    }

    private void setupBottomNavigation() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Show/hide top bar based on fragment
            binding.topBar.setVisibility(itemId == R.id.nav_settings ? View.GONE : View.VISIBLE);

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_tracker) {
                selectedFragment = new TrackerFragment();
            } else if (itemId == R.id.nav_tasks) {
                selectedFragment = new TasksFragment();
            } else if (itemId == R.id.nav_rewards) {
                selectedFragment = new RewardsFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, selectedFragment)
                    .commit();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!userManager.isUserLoggedIn()) {
            Log.d(TAG, "User logged out, redirecting to login");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentTab", binding.bottomNav.getSelectedItemId());
    }

    public void updatePointsDisplay(int points) {
        if (binding != null && binding.pointsDisplay != null) {
            binding.pointsDisplay.setText(getString(R.string.points_display, points));
        }
    }

    public void updatePoints(int newPoints) {
        userManager.setPoints(newPoints);
        updatePointsDisplay(newPoints);
    }

    public int getCurrentPoints() {
        return userManager.getPoints();
    }

    public String getCurrentUserId() {
        String userId = userManager.getLoggedInUser();
        Log.d(TAG, "Getting current user ID: " + userId);
        return userId;
    }

    public void refreshHomeViewModel() {
        if (homeViewModel != null) {
            homeViewModel.refreshUser();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    public static void restartApp(Context context) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            if (context instanceof AppCompatActivity) {
                ((AppCompatActivity) context).finish();
            }
        }
    }
}