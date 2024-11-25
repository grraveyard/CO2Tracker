package com.example.co2tracker;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FoodTrackerFragment extends Fragment {
    private RecyclerView ingredientList;
    private IngredientAdapter adapter;
    private Button addIngredientButton;
    private Button calculateButton;
    private MaterialCardView resultsCard;
    private TextView emissionsResult;
    private ProgressBar emissionsBar;
    private TextView emissionsLevel;
    private List<IngredientItem> ingredients = new ArrayList<>();
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_tracker, container, false);

        // Initialize ViewModel
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        // Initialize views
        ingredientList = view.findViewById(R.id.ingredientList);
        addIngredientButton = view.findViewById(R.id.addIngredientButton);
        calculateButton = view.findViewById(R.id.calculateButton);
        resultsCard = view.findViewById(R.id.resultsCard);
        emissionsResult = view.findViewById(R.id.emissionsResult);
        emissionsBar = view.findViewById(R.id.emissionsBar);
        emissionsLevel = view.findViewById(R.id.emissionsLevel);

        // Setup RecyclerView
        adapter = new IngredientAdapter(requireContext(), ingredients);
        ingredientList.setLayoutManager(new LinearLayoutManager(requireContext()));
        ingredientList.setAdapter(adapter);

        // Add first ingredient
        addIngredient();

        // Setup buttons
        addIngredientButton.setOnClickListener(v -> addIngredient());
        calculateButton.setOnClickListener(v -> calculateEmissions());

        return view;
    }

    private void addIngredient() {
        if (ingredients.size() < 10) {
            ingredients.add(new IngredientItem());
            adapter.notifyItemInserted(ingredients.size() - 1);
            ingredientList.smoothScrollToPosition(ingredients.size() - 1);
            addIngredientButton.setEnabled(ingredients.size() < 10);
        }
    }

    private void calculateEmissions() {
        boolean hasValidFood = false;
        double totalEmissions = 0;

        for (IngredientItem item : ingredients) {
            if (item.getSelectedFood() != null) {
                hasValidFood = true;
                int servings = Math.min(Math.max(item.getServings(), 1), 99999);
                totalEmissions += item.getSelectedFood().getCo2() * servings;
            }
        }

        if (!hasValidFood) {
            Snackbar.make(requireView(),
                            getString(R.string.please_select_food),
                            Snackbar.LENGTH_LONG)
                    .setAction("OK", v -> {})
                    .show();
            return;
        }

        // Update emissions tracking
        double emissionsInKg = totalEmissions / 1000.0; // Convert grams to kg
        homeViewModel.updateEmissions(emissionsInKg, true);

        // Show results with animation
        resultsCard.setVisibility(View.VISIBLE);
        resultsCard.setAlpha(0f);
        resultsCard.animate()
                .alpha(1f)
                .setDuration(300)
                .start();

        emissionsResult.setText(String.format("%.0f gCO₂e", totalEmissions));

        // Animate progress bar
        emissionsBar.setMax(5000);
        ObjectAnimator animation = ObjectAnimator.ofInt(emissionsBar, "progress", 0, (int) totalEmissions);
        animation.setDuration(1000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

        // Set emission level text with color
        String levelText;
        int colorRes;
        if (totalEmissions < 500) {
            levelText = getString(R.string.impact_very_low);
            colorRes = android.R.color.holo_green_dark;
        } else if (totalEmissions < 1000) {
            levelText = getString(R.string.impact_low);
            colorRes = android.R.color.holo_green_light;
        } else if (totalEmissions < 2000) {
            levelText = getString(R.string.impact_moderate);
            colorRes = android.R.color.holo_orange_light;
        } else if (totalEmissions < 3000) {
            levelText = getString(R.string.impact_high);
            colorRes = android.R.color.holo_orange_dark;
        } else {
            levelText = getString(R.string.impact_very_high);
            colorRes = android.R.color.holo_red_dark;
        }
        emissionsLevel.setText(levelText);
        emissionsLevel.setTextColor(requireContext().getColor(colorRes));
    }
}