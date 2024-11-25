package com.example.co2tracker;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class CarTrackerFragment extends Fragment {
    private TextInputEditText distanceInput;
    private TextInputEditText fuelEfficiencyInput;
    private Button calculateButton;
    private MaterialCardView resultsCard;
    private TextView emissionsResult;
    private ProgressBar emissionsBar;
    private TextView emissionsLevel;
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_tracker, container, false);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Initialize views
        distanceInput = view.findViewById(R.id.distanceInput);
        fuelEfficiencyInput = view.findViewById(R.id.fuelEfficiencyInput);
        calculateButton = view.findViewById(R.id.calculateButton);
        resultsCard = view.findViewById(R.id.resultsCard);
        emissionsResult = view.findViewById(R.id.emissionsResult);
        emissionsBar = view.findViewById(R.id.emissionsBar);
        emissionsLevel = view.findViewById(R.id.emissionsLevel);

        // Set input filters
        InputFilter[] filters = new InputFilter[] {
                new InputFilter.LengthFilter(5),
                (source, start, end, dest, dstart, dend) -> {
                    try {
                        String input = dest.toString() + source.toString();
                        if (input.length() > 0) {
                            double value = Double.parseDouble(input);
                            if (value > 99999) return "99999";
                            if (value < 1) return "1";
                        }
                    } catch (NumberFormatException e) {
                        return "";
                    }
                    return null;
                }
        };

        distanceInput.setFilters(filters);
        fuelEfficiencyInput.setFilters(filters);

        // Setup button
        calculateButton.setOnClickListener(v -> calculateEmissions());

        return view;
    }

    private void calculateEmissions() {
        String distanceStr = distanceInput.getText().toString().trim();
        String fuelEfficiencyStr = fuelEfficiencyInput.getText().toString().trim();

        if (distanceStr.isEmpty() || fuelEfficiencyStr.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        try {
            double distance = Double.parseDouble(distanceStr);
            double fuelEfficiency = Double.parseDouble(fuelEfficiencyStr);

            if (distance < 1 || fuelEfficiency < 1) {
                showError("Values must be at least 1");
                return;
            }

            distance = Math.min(distance, 99999);
            fuelEfficiency = Math.min(fuelEfficiency, 99999);

            double litersUsed = (distance * fuelEfficiency) / 100;
            double totalEmissions = litersUsed * 2310; // Convert to grams

            // Update emissions tracking
            double emissionsInKg = totalEmissions / 1000.0; // Convert grams to kg
            homeViewModel.updateEmissions(emissionsInKg, false);

            // Show results with animation
            resultsCard.setVisibility(View.VISIBLE);
            resultsCard.setAlpha(0f);
            resultsCard.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start();

            emissionsResult.setText(String.format("%.0f gCO₂e", totalEmissions));

            // Animate progress bar
            emissionsBar.setMax(10000);
            ObjectAnimator animation = ObjectAnimator.ofInt(emissionsBar, "progress", 0, (int) totalEmissions);
            animation.setDuration(1000);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

            // Set emission level text with color
            String levelText;
            int colorRes;
            if (totalEmissions < 2000) {
                levelText = "Very Low Impact";
                colorRes = android.R.color.holo_green_dark;
            } else if (totalEmissions < 4000) {
                levelText = "Low Impact";
                colorRes = android.R.color.holo_green_light;
            } else if (totalEmissions < 6000) {
                levelText = "Moderate Impact";
                colorRes = android.R.color.holo_orange_light;
            } else if (totalEmissions < 8000) {
                levelText = "High Impact";
                colorRes = android.R.color.holo_orange_dark;
            } else {
                levelText = "Very High Impact";
                colorRes = android.R.color.holo_red_dark;
            }
            emissionsLevel.setText(levelText);
            emissionsLevel.setTextColor(requireContext().getColor(colorRes));

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers");
        }
    }

    private void showError(String message) {
        resultsCard.setVisibility(View.GONE);
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
                .setAction("OK", v -> {})
                .show();
    }
}