package com.example.co2tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.example.co2tracker.databinding.FragmentSettingsBinding;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Locale;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private UserManager userManager;
    private SwitchMaterial darkModeSwitch;
    private Spinner languageSpinner;
    private TextView userEmail;
    private MaterialButton logoutButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        userManager = new UserManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        initializeViews();
        return binding.getRoot();
    }

    private void initializeViews() {
        darkModeSwitch = binding.darkModeSwitch;
        languageSpinner = binding.languageSpinner;
        userEmail = binding.userEmail;
        logoutButton = binding.logoutButton;

        // Setup toolbar
        Toolbar toolbar = binding.settingsToolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setup dark mode switch
        boolean isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        darkModeSwitch.setChecked(isDarkMode);
        
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!buttonView.isPressed()) return;
            
            // Save preference
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            
            // Apply theme change
            AppCompatDelegate.setDefaultNightMode(
                isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
            
            // Save current fragment state
            Bundle currentState = new Bundle();
            saveState(currentState);
            
            // Recreate activity to apply theme changes
            requireActivity().recreate();
        });

        // Setup language spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        // Set current language selection
        String currentLang = LocaleManager.getLanguage(requireContext());
        int position = currentLang.equals("sv") ? 1 : 0;
        languageSpinner.setSelection(position);

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            private boolean isFirstSelection = true;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isFirstSelection) {
                    isFirstSelection = false;
                    return;
                }

                String newLang = position == 1 ? "sv" : "en";
                if (!newLang.equals(LocaleManager.getLanguage(requireContext()))) {
                    LocaleManager.setNewLocale(requireActivity(), newLang);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Initialize email display
        String userEmailText = userManager.getCurrentUserEmail();
        if (userEmailText != null && !userEmailText.isEmpty()) {
            userEmail.setText(userEmailText);
        } else {
            userEmail.setText("Not set");
        }

        // Initialize change password
        View passwordSetting = binding.passwordSetting;
        passwordSetting.setOnClickListener(v -> showChangePasswordDialog());

        // Setup logout button
        logoutButton.setOnClickListener(v -> {
            // Clear all user data
            userManager.clearUserData();
            
            // Clear theme and language preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("language");
            editor.apply();
            
            // Start login activity
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            
            // Finish current activity
            requireActivity().finish();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState(outState);
    }

    private void saveState(Bundle outState) {
        outState.putInt("currentTab", R.id.nav_settings);
    }

    private boolean isValidPassword(String password) {
        // Check for minimum 8 characters
        if (password.length() < 8) return false;

        // Check for at least one uppercase
        if (!password.matches(".*[A-Z].*")) return false;

        // Check for at least one number
        if (!password.matches(".*\\d.*")) return false;

        // Check for at least one special character
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) return false;

        return true;
    }

    private void showChangePasswordDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        builder.setView(dialogView);

        TextInputEditText currentPassword = dialogView.findViewById(R.id.currentPassword);
        TextInputEditText newPassword = dialogView.findViewById(R.id.newPassword);
        TextInputEditText confirmPassword = dialogView.findViewById(R.id.confirmPassword);
        MaterialButton saveButton = dialogView.findViewById(R.id.saveButton);
        MaterialButton cancelButton = dialogView.findViewById(R.id.cancelButton);

        final AlertDialog dialog = builder.create();

        saveButton.setOnClickListener(v -> {
            String currentPwd = currentPassword.getText().toString();
            String newPwd = newPassword.getText().toString();
            String confirmPwd = confirmPassword.getText().toString();

            if (currentPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isValidPassword(newPwd)) {
                newPassword.setError("Password must contain at least 8 characters, 1 uppercase, 1 number, and 1 special character");
                return;
            }

            if (!newPwd.equals(confirmPwd)) {
                confirmPassword.setError("Passwords do not match");
                return;
            }

            if (userManager.verifyPassword(currentPwd)) {
                userManager.updatePassword(newPwd);
                Toast.makeText(requireContext(), R.string.settings_password_changed, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                currentPassword.setError("Current password is incorrect");
            }
        });

        cancelButton.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}